package com.example._4.dto;

import com.example._4.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAccountRequest {
    private String customerId;
    private AccountType type;
    private BigDecimal initialBalance;
}
