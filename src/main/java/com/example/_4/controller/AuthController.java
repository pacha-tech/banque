package com.example._4.controller;

import com.example._4.dto.LoginRequest;
import com.example._4.dto.LoginResponse;
import com.example._4.dto.RegisterRequest;
import com.example._4.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentification & Inscription", description = "Endpoints pour créer un compte client et se connecter")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Créer un nouveau profil client", description = "Génère un identifiant utilisateur, son profil personnel (Customer) et lui ouvre automatiquement un compte courant initial doté d'un bonus de 50€.")
    // @ApiResponse(responseCode = "200", description = "Utilisateur et compte
    // bancaire créés avec succès")
    // @ApiResponse(responseCode = "400", description = "Cet e-mail est déjà associé
    // à un compte existant")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        System.out.println("Donnees recu " + request);
        authService.register(request);
        return ResponseEntity.ok("Compte creer avec succes");
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter à l'application bancaire", description = "Vérifie l'adresse e-mail et le mot de passe, puis renvoie un token de session simulé et l'identifiant du client.")
    // @ApiResponse(responseCode = "200", description = "Connexion réussie, jeton
    // généré")
    // @ApiResponse(responseCode = "404", description = "Identifiants ou mot de
    // passe incorrects")
    // LOGIN MODIFIÉ : Utilise ResponseEntity<LoginResponse>
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
