package com.example._4.repository.interfaces;

import com.example._4.entite.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerInterface extends JpaRepository<Customer , String> {
    Optional<Customer> findByUserIdUser(String userId);
}
