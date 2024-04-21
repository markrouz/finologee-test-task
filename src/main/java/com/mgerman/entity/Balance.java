package com.mgerman.entity;

import com.mgerman.enums.BalanceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;


@Entity
@Table(name = "balance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@Column(name = "currency", nullable = false)
	private String currency;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "type")
	private BalanceType balanceType;

	@ManyToOne
	@JoinColumn(name = "bank_account_id", nullable = false)
	private BankAccount bankAccount;
}
