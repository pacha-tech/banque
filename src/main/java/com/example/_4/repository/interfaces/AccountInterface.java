package com.example._4.repository.interfaces;

import com.example._4.entite.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountInterface extends JpaRepository<Account,String> {
    // Pour les opérations (Dépôt, Retrait, Transfert) via le numéro de compte
    Optional<Account> findByAccountNumber(String accountNumber);

    // Pour lister tous les comptes (Courant, Épargne) sur le tableau de bord du client
    List<Account> findByCustomerIdCustomer(String customerId);

    // Alternative pratique : trouver les comptes directement via l'ID User
    List<Account> findByCustomerUserIdUser(String userId);
}
