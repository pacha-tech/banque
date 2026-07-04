package com.example._4.service;

import com.example._4.dto.*;
import com.example._4.entite.Account;
import com.example._4.entite.Customer;
import com.example._4.entite.Transaction;
import com.example._4.enums.AccountType;
import com.example._4.enums.TransactionStatus;
import com.example._4.enums.TransactionType;
import com.example._4.exception.InsufficientBalanceException;
import com.example._4.exception.ResourceNotFoundException;
import com.example._4.repository.interfaces.AccountInterface;
import com.example._4.repository.interfaces.CustomerInterface;
import com.example._4.repository.interfaces.TransactionInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountInterface accountInterface;

    @Mock
    private TransactionInterface transactionInterface;

    @Mock
    private CustomerInterface customerInterface;

    @InjectMocks
    private AccountService accountService;

    // --- TESTS : openAccount ---

    @Test
    void openAccount_Success() {
        OpenAccountRequest request = new OpenAccountRequest();
        request.setCustomerId("Cust-123");
        request.setType(AccountType.SAVINGS);
        request.setInitialBalance(new BigDecimal("200.00"));

        Customer customer = new Customer();
        customer.setIdCustomer("Cust-123");

        when(customerInterface.findById("Cust-123")).thenReturn(Optional.of(customer));
        when(accountInterface.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.openAccount(request);

        assertNotNull(response);
        assertNotNull(response.getIdAccount());
        assertNotNull(response.getAccountNumber());
        assertEquals(AccountType.SAVINGS, response.getType());
        assertEquals(new BigDecimal("200.00"), response.getBalance());
        verify(accountInterface, times(1)).save(any(Account.class));
    }

    @Test
    void openAccount_WithNullValues_Success() {
        OpenAccountRequest request = new OpenAccountRequest();
        request.setCustomerId("Cust-123");
        request.setType(null);
        request.setInitialBalance(null);

        Customer customer = new Customer();

        when(customerInterface.findById("Cust-123")).thenReturn(Optional.of(customer));
        when(accountInterface.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.openAccount(request);

        assertEquals(AccountType.CHECKING, response.getType());
        assertEquals(BigDecimal.ZERO, response.getBalance());
    }

    @Test
    void openAccount_CustomerNotFound_ThrowsException() {
        OpenAccountRequest request = new OpenAccountRequest();
        request.setCustomerId("Unknown");

        when(customerInterface.findById("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.openAccount(request));
        verify(accountInterface, never()).save(any(Account.class));
    }

    // --- TESTS : getAccountsByCustomer ---

    @Test
    void getAccountsByCustomer_Success() {
        Customer customer = new Customer();
        customer.setIdCustomer("Cust-123");

        Account account = new Account();
        account.setIdAccount("ACC-1");
        account.setAccountNumber("CO-111");
        account.setType(AccountType.CHECKING);
        account.setBalance(new BigDecimal("100.00"));

        when(customerInterface.findById("Cust-123")).thenReturn(Optional.of(customer));
        when(accountInterface.findByCustomer(customer)).thenReturn(List.of(account));

        List<AccountResponse> result = accountService.getAccountsByCustomer("Cust-123");

        assertEquals(1, result.size());
        assertEquals("CO-111", result.get(0).getAccountNumber());
    }

    @Test
    void getAccountsByCustomer_CustomerNotFound_ThrowsException() {
        when(customerInterface.findById("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountsByCustomer("Unknown"));
    }

    // --- TESTS : getBalance ---

    @Test
    void getBalance_Success() {
        Account account = new Account();
        account.setBalance(new BigDecimal("150.50"));

        when(accountInterface.findByAccountNumber("CO-123")).thenReturn(Optional.of(account));

        BigDecimal result = accountService.getBalance("CO-123");

        assertEquals(new BigDecimal("150.50"), result);
    }

    @Test
    void getBalance_AccountNotFound_ThrowsException() {
        when(accountInterface.findByAccountNumber("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getBalance("Unknown"));
    }

    // --- TESTS : deposit ---

    @Test
    void deposit_Success() {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("CO-123");
        request.setAmount(new BigDecimal("50.00"));

        Account account = new Account();
        account.setAccountNumber("CO-123");
        account.setBalance(new BigDecimal("100.00"));

        when(accountInterface.findByAccountNumber("CO-123")).thenReturn(Optional.of(account));

        String msg = accountService.deposit(request);

        assertTrue(msg.contains("CO-123"));
        assertEquals(new BigDecimal("150.00"), account.getBalance());
        verify(accountInterface, times(1)).save(account);
        verify(transactionInterface, times(1)).save(any(Transaction.class));
    }

    @Test
    void deposit_AccountNotFound_ThrowsException() {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("Unknown");

        when(accountInterface.findByAccountNumber("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.deposit(request));
    }

    // --- TESTS : withdraw ---

    @Test
    void withdraw_Success() {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("CO-123");
        request.setAmount(new BigDecimal("40.00"));

        Account account = new Account();
        account.setAccountNumber("CO-123");
        account.setBalance(new BigDecimal("100.00"));

        when(accountInterface.findByAccountNumber("CO-123")).thenReturn(Optional.of(account));

        String msg = accountService.withdraw(request);

        assertTrue(msg.contains("CO-123"));
        assertEquals(new BigDecimal("60.00"), account.getBalance());
        verify(accountInterface, times(1)).save(account);
        verify(transactionInterface, times(1)).save(any(Transaction.class));
    }

    @Test
    void withdraw_InsufficientBalance_ThrowsException() {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("CO-123");
        request.setAmount(new BigDecimal("150.00"));

        Account account = new Account();
        account.setBalance(new BigDecimal("100.00"));

        when(accountInterface.findByAccountNumber("CO-123")).thenReturn(Optional.of(account));

        assertThrows(InsufficientBalanceException.class, () -> accountService.withdraw(request));
        verify(accountInterface, never()).save(any(Account.class));
    }

    @Test
    void withdraw_AccountNotFound_ThrowsException() {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("Unknown");

        when(accountInterface.findByAccountNumber("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.withdraw(request));
    }

    // --- TESTS : transfer ---

    @Test
    void transfer_Success() {
        TransfertRequest request = new TransfertRequest();
        request.setSourceAccountNumber("CO-SRC");
        request.setDestAccountNumber("CO-DEST");
        request.setAmount(new BigDecimal("50.00"));

        Account src = new Account();
        src.setAccountNumber("CO-SRC");
        src.setBalance(new BigDecimal("200.00"));

        Account dest = new Account();
        dest.setAccountNumber("CO-DEST");
        dest.setBalance(new BigDecimal("50.00"));

        when(accountInterface.findByAccountNumber("CO-SRC")).thenReturn(Optional.of(src));
        when(accountInterface.findByAccountNumber("CO-DEST")).thenReturn(Optional.of(dest));
        when(transactionInterface.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionHistory history = accountService.transfer(request);

        assertNotNull(history);
        assertEquals(new BigDecimal("150.00"), src.getBalance());
        assertEquals(new BigDecimal("100.00"), dest.getBalance());
        assertEquals(TransactionStatus.COMPLETED, history.getStatus());
        assertEquals(TransactionType.TRANSFER, history.getType());

        verify(accountInterface, times(1)).save(src);
        verify(accountInterface, times(1)).save(dest);
        verify(transactionInterface, times(1)).save(any(Transaction.class));
    }

    @Test
    void transfer_SourceNotFound_ThrowsException() {
        TransfertRequest request = new TransfertRequest();
        request.setSourceAccountNumber("Unknown");

        when(accountInterface.findByAccountNumber("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.transfer(request));
    }

    @Test
    void transfer_DestNotFound_ThrowsException() {
        TransfertRequest request = new TransfertRequest();
        request.setSourceAccountNumber("CO-SRC");
        request.setDestAccountNumber("Unknown");

        Account src = new Account();

        when(accountInterface.findByAccountNumber("CO-SRC")).thenReturn(Optional.of(src));
        when(accountInterface.findByAccountNumber("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.transfer(request));
    }

    @Test
    void transfer_InsufficientBalance_ThrowsException() {
        TransfertRequest request = new TransfertRequest();
        request.setSourceAccountNumber("CO-SRC");
        request.setDestAccountNumber("CO-DEST");
        request.setAmount(new BigDecimal("300.00"));

        Account src = new Account();
        src.setBalance(new BigDecimal("100.00"));
        Account dest = new Account();

        when(accountInterface.findByAccountNumber("CO-SRC")).thenReturn(Optional.of(src));
        when(accountInterface.findByAccountNumber("CO-DEST")).thenReturn(Optional.of(dest));

        assertThrows(InsufficientBalanceException.class, () -> accountService.transfer(request));
        verify(accountInterface, never()).save(any(Account.class));
    }

    // --- TESTS : getAccountHistory ---

    @Test
    void getAccountHistory_Success() {
        Account account = new Account();
        account.setIdAccount("ACC-123");
        account.setAccountNumber("CO-123");

        Transaction t = new Transaction();
        t.setIdTransaction("TR1");
        t.setReference("TX-REF");
        t.setStatus(TransactionStatus.COMPLETED);
        t.setType(TransactionType.DEPOSIT);
        t.setSourceAccount(account);
        t.setDestAccount(account);
        t.setAmount(new BigDecimal("50.00"));

        when(accountInterface.findByAccountNumber("CO-123")).thenReturn(Optional.of(account));
        when(transactionInterface.findBySourceAccountIdAccountOrDestAccountIdAccountOrderByExecutedAtDesc("ACC-123", "ACC-123"))
                .thenReturn(List.of(t));

        List<TransactionHistory> history = accountService.getAccountHistory("CO-123");

        assertEquals(1, history.size());
        assertEquals("TX-REF", history.get(0).getReference());
    }

    @Test
    void getAccountHistory_AccountNotFound_ThrowsException() {
        when(accountInterface.findByAccountNumber("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountHistory("Unknown"));
    }
}