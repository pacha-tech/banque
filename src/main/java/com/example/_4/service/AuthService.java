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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserInterface userInterface;
    private final CustomerInterface customerInterface;
    // private final AccountInterface accountInterface;

    private String generateIdUser() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);
        return "User-" + number;
    }

    // Génère un identifiant pour le Customer de manière manuelle
    private String generateIdCustomer() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);
        return "Cust-" + number;
    }

    // Crée un User, un Customer et un compte courant avec 50€ offerts
    @Transactional
    public Customer register(RegisterRequest request) {
        if (userInterface.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet e-mail est déjà utilisé.");
        }

        // Création de l'User (Sécurité)
        User user = new User();
        user.setIdUser(generateIdUser());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        user.setStatus(UserStatus.ACTIVE);
        user = userInterface.save(user);

        // Création du Customer (Profil physique)
        Customer customer = new Customer();
        customer.setIdCustomer(generateIdCustomer());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        customer.setUser(user);
        customer = customerInterface.save(customer);

        /*
         * // Ouverture automatique d'un compte de bienvenue
         * OpenAccountRequest initialAccount = new OpenAccountRequest();
         * initialAccount.setCustomerId(customer.getIdCustomer());
         * initialAccount.setType(AccountType.CHECKING);
         * initialAccount.setInitialBalance(new BigDecimal("50.00"));
         * this.openAccount(initialAccount);
         */

        return customer;
    }

    // Renvoie maintenant un LoginResponse
    public LoginResponse login(LoginRequest request) {
        // Vérifier si l'utilisateur existe
        User user = userInterface.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Identifiants incorrects."));

        // Vérifier le mot de passe
        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new IllegalArgumentException("Identifiants incorrects.");
        }

        // Récupérer le Customer lié à cet User pour obtenir son ID
        Customer customer = customerInterface.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profil client introuvable pour cet utilisateur."));

        // Retourner le nouvel objet contenant le message et l'id du client
        return new LoginResponse("Connexion reussis ", customer.getIdCustomer());
    }

}
