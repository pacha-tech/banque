package com.example._4.controller;

import com.example._4.entite.Compte;
import com.example._4.service.CompteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/comptes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Comptes", description = "API pour gérer les opérations bancaires")
public class CompteController {

    private final CompteService compteService;

    @PostMapping
    @Operation(summary = "Créer un nouveau compte")
    @ApiResponse(responseCode = "201", description = "Compte créé avec succès")
    public ResponseEntity<String> creerCompte(@RequestBody Compte compte) {
        Compte nouveauCompte = compteService.creerCompte(compte);
        return ResponseEntity.ok("Compte creer avec succes");
    }

    @GetMapping
    @Operation(summary = "Lister tous les comptes")
    public ResponseEntity<List<Compte>> listerComptes() {
        List<Compte> comptes = compteService.obtenirTousLesComptes();
        return ResponseEntity.ok(comptes);
    }

    @PostMapping("/{id}/depot")
    @Operation(summary = "Déposer de l'argent", description = "Ajoute un montant au solde")
    @ApiResponse(responseCode = "200", description = "Dépôt réussi")
    public ResponseEntity<String> deposer(@PathVariable Long id, @RequestBody double montant) {
        Compte compteMaj = compteService.depot(id, montant);
        return ResponseEntity.ok("Depot de "+montant+" effectuer avec succes");
    }

    @PostMapping("/{id}/retrait")
    @Operation(summary = "Retirer de l'argent", description = "Soustrait un montant du solde")
    @ApiResponse(responseCode = "200", description = "Retrait réussi")
    @ApiResponse(responseCode = "400", description = "Solde insuffisant")
    public ResponseEntity<String> retirer(@PathVariable Long id, @RequestBody double montant) {
        Compte compteMaj = compteService.retrait(id, montant);
        return ResponseEntity.ok("Retrait de "+montant+" effectuer avec succes");
    }
}

