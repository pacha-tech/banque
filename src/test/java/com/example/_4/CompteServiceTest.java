package com.example._4;


import com.example._4.entite.Account;
import com.example._4.repository.interfaces.CompteInterface;
import com.example._4.service.CompteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CompteServiceTest {

    @Mock
    private CompteInterface compteInterface; // On simule le repository

    @InjectMocks
    private CompteService compteService; // On injecte le mock dans le service

    @Test
    void testRetraitSoldeInsuffisant() {

        Account compte = new Account();
        compte.setSolde(100);

        when(compteInterface.findById(1L)).thenReturn(Optional.of(compte));

        assertThrows(RuntimeException.class, () -> {
            compteService.retrait(1L, 200);
        });
    }


    /*
    @Test
    void testDepotSucces(){
        Compte compte = new Compte();
        compte.setSolde(BigDecimal.valueOf(500));

        when(compteInterface.findById(1L)).thenReturn(Optional.of(compte));
        when(compteInterface.save(compte)).thenReturn(compte);

        Compte result = compteService.depot(1L , 500);

        assertEquals(0, BigDecimal.valueOf(700).compareTo(result.getSolde()));
        verify(compteInterface, times(1)).save(compte);
    }
     */


    @Test
    void testCreerCompte() {
        Account nouveauCompte = new Account();
        nouveauCompte.setNomClient("Jean Dupont");
        when(compteInterface.save(nouveauCompte)).thenReturn(nouveauCompte);

        Account resultat = compteService.creerCompte(nouveauCompte);

        assertNotNull(resultat);
        assertEquals("Jean Dupont", resultat.getNomClient());
    }

    @Test
    void testOperationSurCompteInexistant() {
        when(compteInterface.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            compteService.depot(99L, 50);
        });
    }


}

