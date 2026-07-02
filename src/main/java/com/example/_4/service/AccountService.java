package com.example._4.service;

import com.example._4.dto.OpenAccountRequest;
import com.example._4.dto.TransactionHistory;
import com.example._4.dto.TransactionRequest;
import com.example._4.dto.TransfertRequest;
import com.example._4.entite.Account;
import com.example._4.entite.Customer;
import com.example._4.entite.Transaction;
import com.example._4.enums.AccountType;
import com.example._4.enums.TransactionStatus;
import com.example._4.enums.TransactionType;
import com.example._4.exception.InsufficientBalanceException;
import com.example._4.exception.ResourceNotFoundException;
import com.example._4.repository.interfaces.AccountInterface;
import com.example._4.repository.interfaces.CustomerInterface;
import com.example._4.repository.interfaces.TransactionInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountInterface accountInterface;
    private final TransactionInterface transactionInterface;
    private final CustomerInterface customerInterface;

    private String generateIdTransaction() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);
        return "TRans" + number;
    }

    // Générateur de référence unique pour les reçus de transactions
    private String generateReference() {
        return "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Génère un numéro de compte unique (Ex: CO-48291039)
    private String generateAccountNumber() {
        Random random = new Random();
        int number = 10000000 + random.nextInt(90000000);
        return "CO-" + number;
    }

    private String generateIdAccount() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);
        return "ACC-" + number;
    }

    // CRÉATION DE COMPTE : Pour ouvrir un compte manuellement (ex: Épargne)
    public String openAccount(OpenAccountRequest request) {
        Customer customer = customerInterface.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable."));

        Account account = new Account();
        // Optionnel : si l'id de l'Account nécessite aussi une gestion manuelle, tu
        // peux ajouter un generateIdAccount() ici
        account.setIdAccount(generateIdAccount());
        account.setAccountNumber(generateAccountNumber());
        account.setType(request.getType() != null ? request.getType() : AccountType.CHECKING);
        account.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO);
        account.setCustomer(customer);

        return "Compte creer avec succes";
    }

    // 1. CONSULTATION DE SOLDE
    public BigDecimal getBalance(String accountNumber) {
        Account account = accountInterface.findByAccountNumber(accountNumber)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Compte introuvable avec le numéro : " + accountNumber));
        return account.getBalance();
    }

    // 2. DÉPÔT D'ARGENT
    @Transactional
    public String deposit(TransactionRequest request) {
        Account account = accountInterface.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable : " + request.getAccountNumber()));

        // On ajoute l'argent au solde
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountInterface.save(account);

        // On enregistre la transaction dans l'historique
        Transaction tx = new Transaction();
        tx.setIdTransaction(generateIdTransaction());
        tx.setReference(generateReference());
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(request.getAmount());
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setSourceAccount(account); // Pour un dépôt, source = destination
        tx.setDestAccount(account);

        return "Depot Effectuer avec succes dans le compte " + request.getAccountNumber();
    }

    // 3. RETRAIT D'ARGENT
    @Transactional
    public String withdraw(TransactionRequest request) {
        Account account = accountInterface.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable : " + request.getAccountNumber()));

        // Vérification du solde : balance.compareTo(amount) < 0 signifie balance <
        // amount
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant pour effectuer ce retrait.");
        }

        // On soustrait l'argent
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountInterface.save(account);

        // Historique
        Transaction tx = new Transaction();
        tx.setIdTransaction(generateIdTransaction());
        tx.setReference(generateReference());
        tx.setType(TransactionType.WITHDRAWAL);
        tx.setAmount(request.getAmount());
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setSourceAccount(account);
        tx.setDestAccount(account);

        return "Retrait Effectuer avec succes dans le compte " + request.getAccountNumber();
    }

    // 4. TRANSFERT / VIREMENT BANCAIRE
    @Transactional
    public Transaction transfer(TransfertRequest request) {
        Account sourceAccount = accountInterface.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compte source introuvable : " + request.getSourceAccountNumber()));

        Account destAccount = accountInterface.findByAccountNumber(request.getDestAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compte destination introuvable : " + request.getDestAccountNumber()));

        // Vérification du solde du compte émetteur
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant pour ce virement.");
        }

        // Débit du compte source et Crédit du compte destination
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        destAccount.setBalance(destAccount.getBalance().add(request.getAmount()));

        // Sauvegarde des nouveaux soldes
        accountInterface.save(sourceAccount);
        accountInterface.save(destAccount);

        // Création de la transaction de transfert
        Transaction tx = new Transaction();
        tx.setReference(generateReference());
        tx.setType(TransactionType.TRANSFER);
        tx.setAmount(request.getAmount());
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setSourceAccount(sourceAccount);
        tx.setDestAccount(destAccount);

        return transactionInterface.save(tx);
    }

    // 5. CONSULTER L'HISTORIQUE D'UN COMPTE
    public List<TransactionHistory> getAccountHistory(String accountNumber) {
        Account account = accountInterface.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable : " + accountNumber));

        return transactionInterface
                .findBySourceAccountIdAccountOrDestAccountIdAccountOrderByExecutedAtDesc(account.getIdAccount(),
                        account.getIdAccount())
                .stream().map(t -> new TransactionHistory(
                        t.getIdTransaction(),
                        t.getReference(),
                        t.getStatus(),
                        t.getType(),
                        t.getDestAccount().getAccountNumber(),
                        t.getSourceAccount().getAccountNumber(),
                        t.getAmount()))
                .toList();
    }
}
