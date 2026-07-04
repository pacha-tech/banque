package com.example._4.controller;

import com.example._4.dto.AccountResponse;
import com.example._4.dto.OpenAccountRequest;
import com.example._4.dto.TransactionHistory;
import com.example._4.dto.TransactionRequest;
import com.example._4.dto.TransfertRequest;
import com.example._4.enums.AccountType;
import com.example._4.enums.TransactionStatus;
import com.example._4.enums.TransactionType;
import com.example._4.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- TEST : /api/accounts/open ---
    @Test
    void openAccount_ShouldReturnAccountResponse() throws Exception {
        OpenAccountRequest request = new OpenAccountRequest();
        request.setCustomerId("Cust-123");
        request.setType(AccountType.CHECKING);
        request.setInitialBalance(new BigDecimal("50.00"));

        AccountResponse response = new AccountResponse("ACC-1", "CO-888", AccountType.CHECKING, new BigDecimal("50.00"));

        when(accountService.openAccount(any(OpenAccountRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/accounts/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAccount").value("ACC-1"))
                .andExpect(jsonPath("$.accountNumber").value("CO-888"))
                .andExpect(jsonPath("$.balance").value(50.00));
    }

    // --- TEST : /api/accounts/customer/{customerId} ---
    @Test
    void getCustomerAccounts_ShouldReturnList() throws Exception {
        AccountResponse acc1 = new AccountResponse("ACC-1", "CO-111", AccountType.CHECKING, new BigDecimal("100.00"));
        List<AccountResponse> list = List.of(acc1);

        when(accountService.getAccountsByCustomer("Cust-123")).thenReturn(list);

        mockMvc.perform(get("/api/accounts/customer/Cust-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("CO-111"))
                .andExpect(jsonPath("$[0].balance").value(100.00));
    }

    // --- TEST : /api/accounts/{accountNumber}/balance ---
    @Test
    void getBalance_ShouldReturnBigDecimal() throws Exception {
        when(accountService.getBalance("CO-777")).thenReturn(new BigDecimal("350.75"));

        mockMvc.perform(get("/api/accounts/CO-777/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("350.75"));
    }

    // --- TEST : /api/accounts/deposit ---
    @Test
    void deposit_ShouldReturnSuccessMessage() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("CO-777");
        request.setAmount(new BigDecimal("100.00"));

        when(accountService.deposit(any(TransactionRequest.class)))
                .thenReturn("Depot Effectuer avec succes dans le compte CO-777");

        mockMvc.perform(post("/api/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Depot Effectuer avec succes dans le compte CO-777"));
    }

    // --- TEST : /api/accounts/withdraw ---
    @Test
    void withdraw_ShouldReturnSuccessMessage() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("CO-777");
        request.setAmount(new BigDecimal("50.00"));

        when(accountService.withdraw(any(TransactionRequest.class)))
                .thenReturn("Retrait Effectuer avec succes dans le compte CO-777");

        mockMvc.perform(post("/api/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Retrait Effectuer avec succes dans le compte CO-777"));
    }

    // --- TEST : /api/accounts/transfer ---
    @Test
    void transfer_ShouldReturnTransactionHistory() throws Exception {
        TransfertRequest request = new TransfertRequest();
        request.setSourceAccountNumber("CO-SRC");
        request.setDestAccountNumber("CO-DEST");
        request.setAmount(new BigDecimal("20.00"));

        TransactionHistory history = new TransactionHistory(
                "TR-99", "TX-REF12", TransactionStatus.COMPLETED,
                TransactionType.TRANSFER, "CO-SRC", "CO-DEST", new BigDecimal("20.00")
        );

        when(accountService.transfer(any(TransfertRequest.class))).thenReturn(history);

        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTransaction").value("TR-99"))
                .andExpect(jsonPath("$.reference").value("TX-REF12"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    // --- TEST : /api/accounts/{accountNumber}/history ---
    @Test
    void getHistory_ShouldReturnHistoryList() throws Exception {
        TransactionHistory h1 = new TransactionHistory(
                "TR-01", "TX-F1", TransactionStatus.COMPLETED,
                TransactionType.DEPOSIT, "CO-777", "CO-777", new BigDecimal("200.00")
        );

        when(accountService.getAccountHistory("CO-777")).thenReturn(List.of(h1));

        mockMvc.perform(get("/api/accounts/CO-777/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTransaction").value("TR-01"))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"));
    }
}
