package com.formation.bibliotheque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "emprunts")
public class Emprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long livreId;

    @Column(nullable = false)
    private String emprunteur;

    // RM8 : enregistré en base, mais jamais renvoyé par l'API
    @Column(nullable = false)
    private String emailEmprunteur;

    @Column(nullable = false)
    private LocalDate dateEmprunt;

    @Column(nullable = false)
    private LocalDate dateRetourPrevue;

    private LocalDate dateRetourEffective;

    @Column(nullable = false)
    private Boolean rendu;

    public Emprunt() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLivreId() { return livreId; }
    public void setLivreId(Long livreId) { this.livreId = livreId; }

    public String getEmprunteur() { return emprunteur; }
    public void setEmprunteur(String emprunteur) { this.emprunteur = emprunteur; }

    public String getEmailEmprunteur() { return emailEmprunteur; }
    public void setEmailEmprunteur(String emailEmprunteur) { this.emailEmprunteur = emailEmprunteur; }

    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(LocalDate dateEmprunt) { this.dateEmprunt = dateEmprunt; }

    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }

    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public void setDateRetourEffective(LocalDate dateRetourEffective) { this.dateRetourEffective = dateRetourEffective; }

    public Boolean getRendu() { return rendu; }
    public void setRendu(Boolean rendu) { this.rendu = rendu; }
}
