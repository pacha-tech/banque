package com.example._4.service;

import com.example._4.entite.Compte;
import com.example._4.repository.CompteInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CompteService {

    @Autowired
    private CompteInterface compteInterface;

    public Compte creerCompte(Compte compte) {
        return compteInterface.save(compte);
    }

    public List<Compte> obtenirTousLesComptes() {
        return compteInterface.findAll();
    }

    @Transactional
    public Compte depot(Long id, double montant) {
        Compte compte = compteInterface.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));


        BigDecimal nouveauSolde = compte.getSolde().add(BigDecimal.valueOf(montant));
        compte.setSolde(nouveauSolde);

        return compteInterface.save(compte);
    }

    @Transactional
    public Compte retrait(Long id, double montant) {
        Compte compte = compteInterface.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        BigDecimal montantBnd = BigDecimal.valueOf(montant);


        if (compte.getSolde().compareTo(montantBnd) < 0) {
            throw new RuntimeException("Solde insuffisant");
        }


        compte.setSolde(compte.getSolde().subtract(montantBnd));

        return compteInterface.save(compte);
    }
}
