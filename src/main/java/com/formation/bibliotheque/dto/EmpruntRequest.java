package com.formation.bibliotheque.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Ce que le client envoie pour emprunter un livre.
// Pas d'id, pas de dates, pas de "rendu" : c'est le serveur qui les remplit (RM5).
public class EmpruntRequest {

    @NotNull(message = "L'identifiant du livre est obligatoire")
    private Long livreId;

    @NotBlank(message = "Le nom de l'emprunteur est obligatoire")
    @Size(max = 100, message = "Le nom de l'emprunteur ne doit pas dépasser 100 caractères")
    private String emprunteur;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String emailEmprunteur;

    public EmpruntRequest() {
    }

    public Long getLivreId() { return livreId; }
    public void setLivreId(Long livreId) { this.livreId = livreId; }

    public String getEmprunteur() { return emprunteur; }
    public void setEmprunteur(String emprunteur) { this.emprunteur = emprunteur; }

    public String getEmailEmprunteur() { return emailEmprunteur; }
    public void setEmailEmprunteur(String emailEmprunteur) { this.emailEmprunteur = emailEmprunteur; }
}
