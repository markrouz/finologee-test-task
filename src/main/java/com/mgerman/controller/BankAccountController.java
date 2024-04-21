package com.mgerman.controller;

import com.mgerman.dto.BalanceRecord;
import com.mgerman.dto.BankAccountRecord;
import com.mgerman.service.api.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {
	private final BankAccountService bankAccountService;

	@GetMapping
	@Operation(summary = "List bank account of authenticated user")
	public List<BankAccountRecord> getBankAccountsOfCurrentUser() {
		return bankAccountService.findBankAccountsOfCurrentUser().stream().map(e -> new BankAccountRecord(
						e.getAccountNumber(), e.getAccountName(),
						e.getBalances().stream().map(b -> new BalanceRecord(b.getAmount(), b.getCurrency(), b.getBalanceType())).toList(), e.getStatus()))
				.toList();
	}

}
