package com.mgerman.entity;

import com.mgerman.enums.BankAccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

import static com.mgerman.enums.BankAccountStatus.ENABLED;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
public class BankAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "account_number", unique = true, nullable = false)
	private String accountNumber;

	@Column(name = "account_name", nullable = false)
	private String accountName;

	@ManyToMany
	@JoinTable(
			name = "user_bank_account",
			joinColumns = @JoinColumn(name = "bank_account_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> users = new HashSet<>();

	@OneToMany(mappedBy = "bankAccount")
	private Set<Balance> balances = new HashSet<>();

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "status")
	private BankAccountStatus status = ENABLED;

}
