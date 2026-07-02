package com.example._4.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransfertRequest {
    private String sourceAccountNumber;
    private String destAccountNumber;
    private BigDecimal amount;
}
