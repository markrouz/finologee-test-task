package com.mgerman.repository;

import com.mgerman.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUsername(String username);

	@Query("SELECT user FROM User user LEFT JOIN FETCH user.bankAccounts bankAccounts where user.username = :username")
	User findByUsernameWithBankAccountsFetched(String username);
}
