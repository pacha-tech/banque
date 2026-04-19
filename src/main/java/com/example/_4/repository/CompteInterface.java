package com.example._4.repository;

import com.example._4.entite.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteInterface extends JpaRepository<Compte, Long> {
}
