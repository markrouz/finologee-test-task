package com.mgerman.service;

import com.mgerman.entity.BankAccount;
import com.mgerman.repository.BankAccountRepository;
import com.mgerman.service.api.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

	private final BankAccountRepository bankAccountRepository;

	@Override
	public Set<BankAccount> findBankAccountsOfCurrentUser() {
		return bankAccountRepository.findByUserNameWithUsersAndBalances(
				SecurityContextHolder.getContext().getAuthentication().getName()
		);
	}
}
