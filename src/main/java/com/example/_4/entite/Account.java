package com.example._4.entite;

import com.example._4.enums.AccountType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Account")
@Data
public class Account {
    @Id
    @Column(name = "id_account", length = 20)
    private String idAccount;

    @Column(name = "account_number", unique = true, nullable = false, length = 50)
    private String accountNumber;

    @Column(nullable = false, length = 20, columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(length = 10)
    private String currency = "Fcfa";

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "id_customer", nullable = false)
    private Customer customer;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
