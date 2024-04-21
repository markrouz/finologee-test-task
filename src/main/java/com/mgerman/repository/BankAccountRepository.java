package com.mgerman.repository;

import com.mgerman.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

	@Query("""
			SELECT bankAccount FROM BankAccount bankAccount
			 LEFT JOIN FETCH bankAccount.users users
			 LEFT JOIN FETCH bankAccount.balances
			 where users.username = :username
			""")
	Set<BankAccount> findByUserNameWithUsersAndBalances(String username);

	Optional<BankAccount> findByAccountNumber(String accountNumber);

}
