package com.mgerman.controller;

import com.mgerman.dto.PaymentRecord;
import com.mgerman.dto.PaymentRequestRecord;
import com.mgerman.entity.Payment;
import com.mgerman.service.api.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/create")
	@Operation(summary = "Create single payment")
	PaymentRecord createPayment(@RequestBody @Valid PaymentRequestRecord payment) {
		Payment createdPayment = paymentService.createPayment(payment);
		return new PaymentRecord(
				createdPayment.getId(),
				createdPayment.getAmount(),
				createdPayment.getCurrency(),
				createdPayment.getGiverBankAccount().getAccountNumber(),
				createdPayment.getBeneficiaryAccountNumber(),
				createdPayment.getBeneficiaryName(),
				createdPayment.getCommunication(),
				createdPayment.getCreationDate(),
				createdPayment.getStatus()
		);
	}

	//todo pagination
	@GetMapping
	@Operation(summary = "List created payments order by creation date")
	List<PaymentRecord> getAllPayments() {
		return paymentService.getAll()
				.stream()
				.map(p -> new PaymentRecord(
						p.getId(),
						p.getAmount(),
						p.getCurrency(),
						p.getGiverBankAccount().getAccountNumber(),
						p.getBeneficiaryAccountNumber(),
						p.getBeneficiaryName(),
						p.getCommunication(),
						p.getCreationDate(),
						p.getStatus()
				))
				.toList();
	}

	@GetMapping("/get-by-beneficiary-and-period")
	@Operation(summary = "All created payments to a given beneficiaryAccountNumber",
			description = "List all created payments to a given beneficiaryAccountNumber and within a given period order by creation date")
	List<PaymentRecord> getAllPaymentsByBeneficiaryBankAccountWithinPeriod(@RequestParam String beneficiaryBankAccount,
																	 @RequestParam LocalDate startDate,
																	 @RequestParam LocalDate endDate) {
		return paymentService.getAllByBeneficiaryAccountNumberWithingGivenPeriod(beneficiaryBankAccount, startDate, endDate)
				.stream()
				.map(p -> new PaymentRecord(
						p.getId(),
						p.getAmount(),
						p.getCurrency(),
						p.getGiverBankAccount().getAccountNumber(),
						p.getBeneficiaryAccountNumber(),
						p.getBeneficiaryName(),
						p.getCommunication(),
						p.getCreationDate(),
						p.getStatus()
				))
				.toList();

	}

	@DeleteMapping("{id}")
	@Operation(summary = "Delete payment by id")
	@ApiResponse(responseCode = "204", description = "Payment deleted successfully")
	ResponseEntity<Void> deletePayment(@PathVariable Long id) {
		paymentService.deletePaymentById(id);
		return ResponseEntity.noContent().build();
	}

}
