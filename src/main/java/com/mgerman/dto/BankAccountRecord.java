package com.mgerman.dto;


import com.mgerman.enums.BankAccountStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BankAccountRecord(
		@NotBlank
		String accountNumber,
		@NotBlank
		String accountName,
		List<BalanceRecord> balances,
		@NotNull
		BankAccountStatus status
) {
}
