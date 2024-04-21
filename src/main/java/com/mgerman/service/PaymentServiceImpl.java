package com.mgerman.service;

import com.mgerman.dto.PaymentRequestRecord;
import com.mgerman.entity.Balance;
import com.mgerman.entity.BankAccount;
import com.mgerman.entity.Payment;
import com.mgerman.entity.User;
import com.mgerman.enums.PaymentStatus;
import com.mgerman.integration.IbanValidatorClient;
import com.mgerman.repository.BalanceRepository;
import com.mgerman.repository.BankAccountRepository;
import com.mgerman.repository.PaymentRepository;
import com.mgerman.repository.UserRepository;
import com.mgerman.service.api.PaymentService;
import com.mgerman.enums.BalanceType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private static final Set<String> FORBIDDEN_ACCOUNTS = Set.of("LU280019400644750000", "LU120010001234567891");

	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;
	private final IbanValidatorClient ibanValidatorClient;
	private final BankAccountRepository bankAccountRepository;
	private final BalanceRepository balanceRepository;

	@Override
	@Transactional
	public Payment createPayment(PaymentRequestRecord paymentRequest) {
		validateRequest(paymentRequest);

		//find user bank account based on giver bank account
		Optional<BankAccount> userBankAccountOptional = getCurrentUserBankAccount(paymentRequest.giverBankAccountNumber());

		// the giver account should belong to the authenticated user
		if (userBankAccountOptional.isEmpty()) {
			throw new IllegalArgumentException("The giver account number doesn't belong to the user");
		}

		BankAccount userBankAccount = userBankAccountOptional.get();
		Optional<Balance> userBalanceOptional = getCurrenUserBalanceByPaymentRequest(userBankAccount, paymentRequest);
		// payments that exceed the available balance of the account should not be valid
		if (userBalanceOptional.isEmpty()) {
			throw new IllegalArgumentException("Could not find appropriate balance for payment");
		}

		// the receiver account should belong to the bank
		Optional<BankAccount> beneficiaryAccountOptional = bankAccountRepository.findByAccountNumber(paymentRequest.beneficiaryBankAccountNumber());
		if (beneficiaryAccountOptional.isEmpty()) {
			throw new IllegalArgumentException("The receiver account should belong to the bank");
		}

		//decrease giver account balance
		Balance userBalance = userBalanceOptional.get();
		userBalance.setAmount(userBalanceOptional.get().getAmount().subtract(paymentRequest.amount()));

		// increase receiver account balance
		Balance receiverBalance = getReceiverBalance(beneficiaryAccountOptional.get(), paymentRequest.currency());
		receiverBalance.setAmount(receiverBalance.getAmount().add(paymentRequest.amount()));

		var newPayment = Payment.builder()
				.amount(paymentRequest.amount())
				.currency(paymentRequest.currency())
				.giverBankAccount(userBankAccount)
				.beneficiaryAccountNumber(paymentRequest.beneficiaryBankAccountNumber())
				.beneficiaryName(paymentRequest.beneficiaryName())
				.communication(paymentRequest.communication())
				.creationDate(LocalDateTime.now())
				.status(PaymentStatus.EXECUTED)
				.build();
		return paymentRepository.save(newPayment);
	}


	//todo pagination
	@Override
	public List<Payment> getAll() {
		User currentUser = userRepository.findByUsernameWithBankAccountsFetched(SecurityContextHolder.getContext().getAuthentication().getName());
		return paymentRepository.findByGiverBankAccountAccountNumberInOrderByCreationDate(
				currentUser.getBankAccounts()
						.stream()
						.map(BankAccount::getAccountNumber)
						.toList()
		);
	}

	//todo pagination
	@Override
	public List<Payment> getAllByBeneficiaryAccountNumberWithingGivenPeriod(String beneficiaryAccountNumber, LocalDate startDate, LocalDate endDate) {
		if (beneficiaryAccountNumber == null || startDate == null || endDate == null) {
			throw new IllegalArgumentException("Missing input parameters!");
		}
		if (endDate.isBefore(startDate)) {
			throw new IllegalArgumentException("End date cannot be less than start date");
		}
		User currentUser = userRepository.findByUsernameWithBankAccountsFetched(SecurityContextHolder.getContext().getAuthentication().getName());
		return paymentRepository.findByGiverBankAccountIdInAndBeneficiaryAccountNumberAndCreationDateBetweenOrderByCreationDate(
				currentUser.getBankAccounts()
						.stream()
						.map(BankAccount::getId)
						.toList(),
				beneficiaryAccountNumber,
				startDate.atStartOfDay(),
				endDate.atStartOfDay());
	}

	@Override
	public void deletePaymentById(Long id) {
		Optional<Payment> paymentToDeleteOptional = paymentRepository.findById(id);
		if (paymentToDeleteOptional.isEmpty()) {
			throw new IllegalArgumentException(String.format("Payment with id %s not found", id));
		}
		Payment paymentToDelete = paymentToDeleteOptional.get();
		User currentUser = userRepository.findByUsernameWithBankAccountsFetched(SecurityContextHolder.getContext().getAuthentication().getName());
		Set<String> userBankAccountNumbers = currentUser.getBankAccounts().stream().map(BankAccount::getAccountNumber).collect(toSet());
		if (!userBankAccountNumbers.contains(paymentToDelete.getGiverBankAccount().getAccountNumber())) {
			throw new IllegalArgumentException(String.format("Payment with id %s not found", id));
		}
		paymentRepository.deleteById(id);
	}

	private void validateRequest(PaymentRequestRecord paymentRequest) {
		if (paymentRequest.giverBankAccountNumber().equals(paymentRequest.beneficiaryBankAccountNumber())) {
			throw new IllegalArgumentException("Payments to the same account number are not allowed");
		}
		if (FORBIDDEN_ACCOUNTS.contains(paymentRequest.beneficiaryBankAccountNumber())) {
			throw new IllegalArgumentException("Payments to forbidden accounts are not allowed");
		}
		if (paymentRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Payment amount should be more than zero");
		}
		if (!ibanValidatorClient.isValidIban(paymentRequest.beneficiaryBankAccountNumber())) {
			throw new IllegalArgumentException("Invalid IBAN: " + paymentRequest.beneficiaryBankAccountNumber());
		}
		if (!ibanValidatorClient.isValidIban(paymentRequest.giverBankAccountNumber())) {
			throw new IllegalArgumentException("Invalid IBAN: " + paymentRequest.giverBankAccountNumber());
		}
	}

	private Optional<BankAccount> getCurrentUserBankAccount(String giverAccountNumber) {
		User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		return currentUser.getBankAccounts()
				.stream()
				.filter(ba -> ba.getAccountNumber().equals(giverAccountNumber))
				.findFirst();
	}

	private Optional<Balance> getCurrenUserBalanceByPaymentRequest(BankAccount userBankAccount, PaymentRequestRecord paymentRequest) {
		return userBankAccount.getBalances().stream()
				.filter(b -> b.getCurrency().equals(paymentRequest.currency()))
				.filter(b -> b.getAmount().compareTo(paymentRequest.amount()) >= 0)
				.findFirst();
	}

	private Balance getReceiverBalance(BankAccount beneficiaryBankAccount, String currency) {
		// the receiver account should have the balance of appropriate currency, or create a new one
		return beneficiaryBankAccount.getBalances()
				.stream()
				.filter(b -> b.getCurrency().equals(currency))
				.findFirst()
				.orElseGet(() -> {
					var newBalance = Balance.builder()
							.amount(BigDecimal.ZERO)
							.currency(currency)
							.bankAccount(beneficiaryBankAccount)
							.balanceType(BalanceType.AVAILABLE).build();

					balanceRepository.save(newBalance);
					return newBalance;
				});
	}
}
