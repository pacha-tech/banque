package com.example._4.controller;

import com.example._4.dto.LoginRequest;
import com.example._4.dto.LoginResponse;
import com.example._4.dto.RegisterRequest;
import com.example._4.entite.Customer;
import com.example._4.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- TEST : /api/auth/register ---
    @Test
    void register_ShouldReturnSuccessMessage() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPhone("987654321");

        // Le contrôleur ignore la valeur de retour de register(), mais on mock pour éviter un comportement indéfini
        when(authService.register(any(RegisterRequest.class))).thenReturn(new Customer());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Compte creer avec succes"));
    }

    // --- TEST : /api/auth/login ---
    @Test
    void login_ShouldReturnLoginResponse() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        // "Connexion reussis" est passé au constructeur en tant que champ 'token'
        LoginResponse response = new LoginResponse("Connexion reussis", "Cust-123");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("Connexion reussis")) // 👈 Correction : $.token au lieu de $.message
                .andExpect(jsonPath("$.customerId").value("Cust-123"));    // 👈 Correction : Juste la valeur du mock
    }
}
