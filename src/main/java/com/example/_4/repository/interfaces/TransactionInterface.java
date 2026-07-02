package com.example._4.repository.interfaces;

import com.example._4.entite.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionInterface extends JpaRepository<Transaction , String> {
    // 1. Historique complet d'UN COMPTE spécifique (trié du plus récent au plus ancien)
    // Trouve toutes les transactions où ce compte est émetteur OU récepteur
    List<Transaction> findBySourceAccountIdAccountOrDestAccountIdAccountOrderByExecutedAtDesc(String sourceAccountId, String destAccountId);

    // 2. Historique global de TOUS les comptes d'un client (via son userId)
    List<Transaction> findBySourceAccountCustomerUserIdUserOrDestAccountCustomerUserIdUserOrderByExecutedAtDesc(String sourceUserId, String destUserId);
}
