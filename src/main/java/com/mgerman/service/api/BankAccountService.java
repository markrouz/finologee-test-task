package com.mgerman.service.api;

import com.mgerman.entity.BankAccount;

import java.util.Set;

public interface BankAccountService {
	Set<BankAccount> findBankAccountsOfCurrentUser();
}
