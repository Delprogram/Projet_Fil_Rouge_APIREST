package com.formation.bibliotheque.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

// Pas d'email ici (RM8). "enRetard" n'existe pas en base : il est calculé par le mapper.
@JsonPropertyOrder({"id", "livreId", "emprunteur", "dateEmprunt", "dateRetourPrevue",
        "dateRetourEffective", "rendu", "enRetard"})
public class EmpruntResponse {

    private final Long id;
    private final Long livreId;
    private final String emprunteur;
    private final LocalDate dateEmprunt;
    private final LocalDate dateRetourPrevue;
    private final LocalDate dateRetourEffective;
    private final Boolean rendu;
    private final Boolean enRetard;

    public EmpruntResponse(Long id, Long livreId, String emprunteur, LocalDate dateEmprunt,
                           LocalDate dateRetourPrevue, LocalDate dateRetourEffective,
                           Boolean rendu, Boolean enRetard) {
        this.id = id;
        this.livreId = livreId;
        this.emprunteur = emprunteur;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
        this.rendu = rendu;
        this.enRetard = enRetard;
    }

    public Long getId() { return id; }
    public Long getLivreId() { return livreId; }
    public String getEmprunteur() { return emprunteur; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public Boolean getRendu() { return rendu; }
    public Boolean getEnRetard() { return enRetard; }
}
