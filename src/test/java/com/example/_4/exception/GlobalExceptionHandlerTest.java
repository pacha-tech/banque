package com.example._4.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalExceptionHandlerTest.TestController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- 1. TEST DE GLOBAL EXCEPTION HANDLER (via MockMvc) ---
    @Test
    void handleRuntimeException_ShouldReturnBadRequestAndErrorResponse() throws Exception {
        mockMvc.perform(get("/test-error"))
                .andExpect(status().isBadRequest())
                //.andExpect(jsonPath("$.message").value("Une erreur de test est survenue"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    // --- 2. TEST DE ERROR RESPONSE (Record Java) ---
    @Test
    void testErrorResponseRecord() {
        // Test du constructeur et des accesseurs du Record pour le 100% Jacoco
        ErrorResponse error = new ErrorResponse("Erreur", 400);

        assertNotNull(error);
        assertEquals("Erreur", error.message());
        assertEquals(400, error.status());
    }

    // --- Faux contrôleur dédié uniquement à ce test ---
    @RestController
    static class TestController {
        @GetMapping("/test-error")
        public void throwError() {
            throw new RuntimeException("Une erreur de test est survenue");
        }
    }
}
