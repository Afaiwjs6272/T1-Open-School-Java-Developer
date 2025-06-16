package ru.ukhanov.t1.java.model.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.model.transaction.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "transaction_sum")
    private BigDecimal transactionSum;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "timestamp")
    private Instant timestamp;
}
