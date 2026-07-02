package com.example._4.dto;

import com.example._4.enums.TransactionStatus;
import com.example._4.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistory {
    private String idTransaction;
    private String reference;
    private TransactionStatus status;
    private TransactionType type;
    private String compteDestinataire;
    private String compteSource;
    private BigDecimal amount;
}
