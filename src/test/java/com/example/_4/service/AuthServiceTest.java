package com.example._4.service;

import com.example._4.dto.LoginRequest;
import com.example._4.dto.LoginResponse;
import com.example._4.dto.RegisterRequest;
import com.example._4.entite.Customer;
import com.example._4.entite.User;
import com.example._4.enums.UserStatus;
import com.example._4.exception.ResourceNotFoundException;
import com.example._4.repository.interfaces.CustomerInterface;
import com.example._4.repository.interfaces.UserInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserInterface userInterface;

    @Mock
    private CustomerInterface customerInterface;

    @InjectMocks
    private AuthService authService;

    // --- TESTS : register ---

    @Test
    void register_Success() {
        // Préparation des données d'entrée
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("securePassword");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("123456789");

        // Simulation des comportements des Repositories
        when(userInterface.existsByEmail("test@example.com")).thenReturn(false);
        when(userInterface.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerInterface.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution de la méthode
        Customer createdCustomer = authService.register(request);

        // Vérifications
        assertNotNull(createdCustomer);
        assertNotNull(createdCustomer.getIdCustomer());
        assertEquals("John", createdCustomer.getFirstName());
        assertEquals("Doe", createdCustomer.getLastName());
        assertEquals("123456789", createdCustomer.getPhone());

        assertNotNull(createdCustomer.getUser());
        assertNotNull(createdCustomer.getUser().getIdUser());
        assertEquals("test@example.com", createdCustomer.getUser().getEmail());
        assertEquals("securePassword", createdCustomer.getUser().getPasswordHash());
        assertEquals(UserStatus.ACTIVE, createdCustomer.getUser().getStatus());

        verify(userInterface, times(1)).existsByEmail("test@example.com");
        verify(userInterface, times(1)).save(any(User.class));
        verify(customerInterface, times(1)).save(any(Customer.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userInterface.existsByEmail("existing@example.com")).thenReturn(true);

        // Vérification du lever d'exception
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));

        // Sécurité : s'assurer qu'aucune sauvegarde n'est tentée en BDD
        verify(userInterface, never()).save(any(User.class));
        verify(customerInterface, never()).save(any(Customer.class));
    }

    // --- TESTS : login ---

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("password123");

        Customer customer = new Customer();
        customer.setIdCustomer("Cust-999");
        customer.setUser(user);

        when(userInterface.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(customerInterface.findByUser(user)).thenReturn(Optional.of(customer));

        // Exécution de la méthode
        LoginResponse response = authService.login(request);

        // Vérifications
        assertNotNull(response);
        //assertEquals("Connexion reussis", response.get);
        assertEquals("Cust-999", response.getCustomerId());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("anyPassword");

        when(userInterface.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
        verify(customerInterface, never()).findByUser(any(User.class));
    }

    @Test
    void login_IncorrectPassword_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("correctPassword");

        when(userInterface.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        verify(customerInterface, never()).findByUser(any(User.class));
    }

    @Test
    void login_CustomerProfileNotFound_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("password123");

        when(userInterface.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        // L'utilisateur existe mais aucun profil Customer n'est lié
        when(customerInterface.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
    }
}
