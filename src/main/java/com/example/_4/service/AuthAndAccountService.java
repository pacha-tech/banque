package com.example._4.service;

import com.example._4.dto.LoginRequest;
import com.example._4.dto.OpenAccountRequest;
import com.example._4.dto.RegisterRequest;
import com.example._4.entite.Account;
import com.example._4.entite.Customer;
import com.example._4.entite.User;
import com.example._4.exception.ResourceNotFoundException;
import com.example._4.repository.interfaces.AccountInterface;
import com.example._4.repository.interfaces.CustomerInterface;
import com.example._4.repository.interfaces.UserInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthAndAccountService {
    private final UserInterface userInterface;
    private final CustomerInterface customerInterface;
    private final AccountInterface accountInterface;

    // Génère un numéro de compte unique (Ex: CO-48291039)
    private String generateAccountNumber() {
        Random random = new Random();
        int number = 10000000 + random.nextInt(90000000);
        return "CO-" + number;
    }

    private String generateIdUser() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);
        return "User-" + number;
    }

    // NOUVEAU : Génère un identifiant pour le Customer de manière manuelle
    private String generateIdCustomer() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);
        return "Cust-" + number;
    }

    private String generateIdAccount() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);
        return "ACC-" + number;
    }

    // INSCRIPTION : Crée un User, un Customer et un compte courant avec 50€ offerts
    @Transactional
    public Customer register(RegisterRequest request) {
        if (userInterface.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet e-mail est déjà utilisé.");
        }

        // 1. Création de l'User (Sécurité)
        User user = new User();
        user.setIdUser(generateIdUser());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        user.setStatus("ACTIVE");
        user = userInterface.save(user);

        // 2. Création du Customer (Profil physique)
        Customer customer = new Customer();
        customer.setIdCustomer(generateIdCustomer());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        customer.setUser(user);
        customer = customerInterface.save(customer);

        /*
        // 3. Ouverture automatique d'un compte de bienvenue
        OpenAccountRequest initialAccount = new OpenAccountRequest();
        initialAccount.setCustomerId(customer.getIdCustomer());
        initialAccount.setType("CHECKING");
        initialAccount.setInitialBalance(new BigDecimal("50.00"));
        this.openAccount(initialAccount);
        */

        return customer;
    }

    // CONNEXION : Vérifie l'email et le mot de passe
    public String login(LoginRequest request) {
        User user = userInterface.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Identifiants incorrects."));

        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new IllegalArgumentException("Identifiants incorrects.");
        }

        return "Connexion reussis";
    }

    // CRÉATION DE COMPTE : Pour ouvrir un compte manuellement (ex: Épargne)
    public String openAccount(OpenAccountRequest request) {
        Customer customer = customerInterface.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable."));

        Account account = new Account();
        // Optionnel : si l'id de l'Account nécessite aussi une gestion manuelle, tu peux ajouter un generateIdAccount() ici
        account.setIdAccount(generateIdAccount());
        account.setAccountNumber(generateAccountNumber());
        account.setType(request.getType().toUpperCase());
        account.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO);
        account.setCustomer(customer);

        return "Compte creer avec succes";
    }
}
