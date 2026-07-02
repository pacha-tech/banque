package com.example._4;

import com.example._4.entite.Account;
import com.example._4.repository.interfaces.AccountInterface;
import com.example._4.repository.interfaces.TransactionInterface;
import com.example._4.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompteServiceTest {

    @Mock
    private AccountInterface accountInterface;

    @Mock
    private TransactionInterface transactionInterface;

    @InjectMocks
    private AccountService accountService;

    @Test
    void testGetBalanceReturnsAccountBalance() {
        Account account = new Account();
        account.setBalance(new BigDecimal("150.50"));

        when(accountInterface.findByAccountNumber("CO-123")).thenReturn(Optional.of(account));

        BigDecimal result = accountService.getBalance("CO-123");

        assertEquals(new BigDecimal("150.50"), result);
    }
}
