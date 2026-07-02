package com.example._4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAccountRequest {
    private String customerId;
    private String type;
    private BigDecimal initialBalance;
}
