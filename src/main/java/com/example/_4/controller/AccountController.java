package com.example._4.controller;

import com.example._4.dto.OpenAccountRequest;
import com.example._4.dto.TransactionHistory;
import com.example._4.dto.TransactionRequest;
import com.example._4.dto.TransfertRequest;
import com.example._4.entite.Account;
import com.example._4.entite.Transaction;
import com.example._4.service.AccountService;
import com.example._4.service.AuthAndAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "2. Comptes & Opérations Bancaires", description = "Endpoints métiers pour la gestion des comptes, dépôts, retraits et virements")
public class AccountController {
    private final AccountService accountService;
    private final AuthAndAccountService authAndAccountService;

    @PostMapping("/open")
    @Operation(
            summary = "Ouvrir un compte supplémentaire",
            description = "Permet à un client existant d'ajouter un nouveau compte à son profil en spécifiant le type (ex: SAVINGS pour un compte épargne)."
    )
    public ResponseEntity<String> openAccount(@RequestBody OpenAccountRequest request) {
        return ResponseEntity.ok(authAndAccountService.openAccount(request));
    }

    @GetMapping("/{accountNumber}/balance")
    @Operation(summary = "Consulter le solde en temps réel", description = "Renvoie le montant exact disponible sur un compte via son numéro unique.")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getBalance(accountNumber));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Déposer de l'argent cash", description = "Alimente instantanément le compte indiqué par son numéro.")
    public ResponseEntity<String> deposit(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(accountService.deposit(request));
    }

    @PostMapping("/withdraw")
    @Operation(
            summary = "Retirer des fonds cash",
            description = "Soustrait le montant demandé du compte. Renvoie une erreur si les provisions sont insuffisantes."
    )
    @ApiResponse(responseCode = "200", description = "Retrait effectué")
    @ApiResponse(responseCode = "400", description = "Solde insuffisant pour finaliser l'opération")
    public ResponseEntity<String> withdraw(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(accountService.withdraw(request));
    }

    @PostMapping("/transfer")
    @Operation(
            summary = "Effectuer un virement / virement interne",
            description = "Déplace de l'argent de manière sécurisée d'un compte émetteur vers un compte bénéficiaire."
    )
    public ResponseEntity<Transaction> transfer(@RequestBody TransfertRequest request) {
        return ResponseEntity.ok(accountService.transfer(request));
    }

    @GetMapping("/{accountNumber}/history")
    @Operation(
            summary = "Consulter l'historique complet",
            description = "Renvoie la liste chronologique de toutes les écritures (dépôts, retraits, virements) associées à ce numéro de compte."
    )
    public ResponseEntity<List<TransactionHistory>> getHistory(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountHistory(accountNumber));
    }
}

