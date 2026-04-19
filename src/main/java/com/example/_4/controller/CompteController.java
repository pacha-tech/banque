package com.example._4.controller;

import com.example._4.entite.Compte;
import com.example._4.service.CompteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Record pour structurer les messages de succès
record SuccessResponse<T>(String message, T data) {}

@RestController
@RequestMapping("/api/comptes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Comptes", description = "API pour gérer les opérations bancaires")
public class CompteController {

    private final CompteService compteService;

    @PostMapping
    @Operation(summary = "Créer un nouveau compte")
    public ResponseEntity<SuccessResponse<Compte>> creerCompte(@RequestBody Compte compte) {
        Compte nouveauCompte = compteService.creerCompte(compte);
        return new ResponseEntity<>(
                new SuccessResponse<>("Compte créé avec succès", nouveauCompte),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    @Operation(summary = "Lister tous les comptes")
    public ResponseEntity<List<Compte>> listerComptes() {
        List<Compte> comptes = compteService.obtenirTousLesComptes();
        return ResponseEntity.ok(comptes);
    }

    @PostMapping("/{id}/depot")
    @Operation(summary = "Déposer de l'argent", description = "Ajoute un montant au solde d'un compte spécifique")
    public ResponseEntity<SuccessResponse<Compte>> deposer(@PathVariable Long id, @RequestBody double montant) {
        Compte compteMaj = compteService.depot(id, montant);
        return ResponseEntity.ok(new SuccessResponse<>(
                "Le dépôt de " + montant + " a été effectué avec succès.",
                compteMaj
        ));
    }

    @PostMapping("/{id}/retrait")
    @Operation(summary = "Retirer de l'argent", description = "Soustrait un montant du solde d'un compte spécifique")
    public ResponseEntity<SuccessResponse<Compte>> retirer(@PathVariable Long id, @RequestBody double montant) {
        Compte compteMaj = compteService.retrait(id, montant);
        return ResponseEntity.ok(new SuccessResponse<>(
                "Le retrait de " + montant + " a été effectué avec succès.",
                compteMaj
        ));
    }
}
