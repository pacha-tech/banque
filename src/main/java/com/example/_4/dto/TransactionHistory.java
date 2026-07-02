package com.example._4.dto;

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
    private String status;
    private String type;
    private String compteDestinataire;
    private String compteSource;
    private BigDecimal amount;
}
