package com.example._4.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomExceptionsTest {

    @Test
    void testInsufficientBalanceException_Message() {
        String errorMessage = "Solde insuffisant pour cette opération.";

        // Instanciation directe de l'exception
        InsufficientBalanceException exception = new InsufficientBalanceException(errorMessage);

        // Vérifications
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testResourceNotFoundException_Message() {
        String errorMessage = "La ressource demandée n'existe pas.";

        // Instanciation directe de l'exception
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        // Vérifications
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }
}
