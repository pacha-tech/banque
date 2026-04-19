package com.example._4.entite;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Compte")
@Data
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String numeroCompte;

    @Column(nullable = false)
    private String nomClient;

    @Column(nullable = false)
    private BigDecimal solde;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateCreation;


    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.numeroCompte == null) {
            this.numeroCompte = "AC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.solde == null) {
            this.solde = BigDecimal.ZERO;
        }
    }
}
