package com.mgerman.service.api;

import com.mgerman.dto.PaymentRequestRecord;
import com.mgerman.entity.Payment;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
	Payment createPayment(PaymentRequestRecord payment);

	List<Payment> getAll();

	List<Payment> getAllByBeneficiaryAccountNumberWithingGivenPeriod(String beneficiaryAccountNumber, LocalDate startDate, LocalDate endDate);

	void deletePaymentById(Long id);
}
