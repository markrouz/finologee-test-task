package com.mgerman.entity;

import com.mgerman.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@Column(name = "currency", nullable = false)
	private String currency;

	@ManyToOne
	@JoinColumn(name = "giver_bank_account_id", nullable = false)
	private BankAccount giverBankAccount;

	@Column(name = "beneficiary_account_number", nullable = false)
	private String beneficiaryAccountNumber;

	@Column(name = "beneficiary_name", nullable = false)
	private String beneficiaryName;

	@Column(name = "communication")
	private String communication;

	@Column(name = "creation_date", nullable = false)
	private LocalDateTime creationDate;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "status")
	private PaymentStatus status = PaymentStatus.EXECUTED;
}
