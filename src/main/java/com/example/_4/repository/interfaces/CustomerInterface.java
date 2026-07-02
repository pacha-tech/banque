package com.example._4.repository.interfaces;

import com.example._4.entite.Customer;
import com.example._4.entite.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerInterface extends JpaRepository<Customer, String> {
    Optional<Customer> findByUserIdUser(String userId);

    // Permet de retrouver le profil client grâce à l'entité User liée
    Optional<Customer> findByUser(User user);
}
