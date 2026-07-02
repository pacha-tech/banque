package com.example._4.entite;

import com.example._4.enums.TransactionStatus;
import com.example._4.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @Column(name = "id_transaction", length = 10)
    private String idTransaction;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @Column(nullable = false, length = 20, columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(length = 20, columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.COMPLETED;

    @ManyToOne
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "dest_account_id", nullable = false)
    private Account destAccount;

    @Column(name = "executed_at", updatable = false)
    private LocalDateTime executedAt = LocalDateTime.now();
}
