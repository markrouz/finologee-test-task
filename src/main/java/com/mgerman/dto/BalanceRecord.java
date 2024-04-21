package com.mgerman.dto;

import com.mgerman.enums.BalanceType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BalanceRecord(
		@NotNull
		BigDecimal amount,
		@NotNull
		String currency,
		@NotNull
		BalanceType balanceType) {
}
