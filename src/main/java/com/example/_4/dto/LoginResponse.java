package com.example._4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token; // Contienra votre chaîne "Connexion reussis" ou un vrai token plus tard
    private String customerId;
}