package com.formation.bibliotheque.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "titre", "isbn", "prix", "anneePublication", "stock", "auteurId"})
public class LivreResponse {

    private final Long id;
    private final String titre;
    private final String isbn;
    private final Double prix;
    private final Integer anneePublication;
    private final Integer stock;
    private final Long auteurId;

    public LivreResponse(Long id, String titre, String isbn, Double prix,
                         Integer anneePublication, Integer stock, Long auteurId) {
        this.id = id;
        this.titre = titre;
        this.isbn = isbn;
        this.prix = prix;
        this.anneePublication = anneePublication;
        this.stock = stock;
        this.auteurId = auteurId;
    }

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public String getIsbn() { return isbn; }
    public Double getPrix() { return prix; }
    public Integer getAnneePublication() { return anneePublication; }
    public Integer getStock() { return stock; }
    public Long getAuteurId() { return auteurId; }
}
