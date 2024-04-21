package com.mgerman.dto;

import com.mgerman.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRecord(
		Long id,
		@NotNull
		BigDecimal amount,
		@NotBlank
		String currency,
		@NotBlank
		String giverBankAccountNumber,
		@NotBlank
		String beneficiaryAccountNumber,
		@NotBlank
		String beneficiaryName,
		@NotBlank
		String communication,
		@NotNull
		LocalDateTime creationDate,
		@NotNull
		PaymentStatus status
) {
}
