package com.mgerman.repository;

import com.mgerman.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByGiverBankAccountAccountNumberInOrderByCreationDate(List<String> giverBankAccountNumber);

	List<Payment> findByGiverBankAccountIdInAndBeneficiaryAccountNumberAndCreationDateBetweenOrderByCreationDate(List<Long> giverBankAccountIds,
																												 String beneficiaryAccountNumber,
																												 LocalDateTime startDate,
																												 LocalDateTime endDate);
}
