package com.mgerman.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequestRecord(
		@NotBlank
		String giverBankAccountNumber,
		@NotNull
		BigDecimal amount,
		@NotBlank
		String currency,
		@NotBlank
		String beneficiaryBankAccountNumber,
		@NotBlank
		String beneficiaryName,
		@NotBlank
		String communication
) {
}
