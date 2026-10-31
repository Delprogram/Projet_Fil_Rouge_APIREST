package com.formation.bibliotheque.service;

import com.formation.bibliotheque.model.Livre;
import com.formation.bibliotheque.repository.AuteurRepository;
import com.formation.bibliotheque.repository.LivreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

// La logique métier des livres
@Service
public class LivreService {

    private final LivreRepository livreRepository;
    private final AuteurRepository auteurRepository;

    public LivreService(LivreRepository livreRepository, AuteurRepository auteurRepository) {
        this.livreRepository = livreRepository;
        this.auteurRepository = auteurRepository;
    }

    public Page<Livre> recupererTous(Pageable pageable) {
        return livreRepository.findAll(pageable);
    }

    public Optional<Livre> recupererParId(Long id) {
        return livreRepository.findById(id);
    }

    public boolean existe(Long id) {
        return livreRepository.existsById(id);
    }

    public boolean auteurExiste(Long auteurId) {
        return auteurRepository.existsById(auteurId);
    }

    // RM2 (création) : est-ce qu'un livre a déjà cet ISBN ?
    public boolean isbnExiste(String isbn) {
        return livreRepository.findByIsbn(isbn).isPresent();
    }

    // RM2 (modification) : l'ISBN est-il pris par un AUTRE livre ?
    // Si c'est le livre lui-même, pas de souci : il garde son ISBN.
    public boolean isbnUtiliseParUnAutreLivre(String isbn, Long idLivre) {
        Optional<Livre> resultat = livreRepository.findByIsbn(isbn);
        if (resultat.isEmpty()) {
            return false;
        }
        Livre livreTrouve = resultat.get();
        // equals et pas == pour comparer deux Long
        return !livreTrouve.getId().equals(idLivre);
    }

    public Livre creer(Livre livre) {
        livre.setId(null);
        return livreRepository.save(livre);
    }

    public Livre mettreAJour(Livre livreExistant, Livre nouvellesValeurs) {
        livreExistant.setTitre(nouvellesValeurs.getTitre());
        livreExistant.setIsbn(nouvellesValeurs.getIsbn());
        livreExistant.setPrix(nouvellesValeurs.getPrix());
        livreExistant.setAnneePublication(nouvellesValeurs.getAnneePublication());
        livreExistant.setStock(nouvellesValeurs.getStock());
        livreExistant.setAuteurId(nouvellesValeurs.getAuteurId());
        return livreRepository.save(livreExistant);
    }

    public void supprimer(Long id) {
        livreRepository.deleteById(id);
    }

    // Recherche dans le titre, sans tenir compte des majuscules
    public Page<Livre> rechercherParTitre(String titre, Pageable pageable) {
        return livreRepository.findByTitreContainingIgnoreCase(titre, pageable);
    }

    // RM4 : disponible = stock > 0
    public Page<Livre> recupererDisponibles(Pageable pageable) {
        return livreRepository.findByStockGreaterThan(0, pageable);
    }

    // Réapprovisionnement : on ajoute la quantité au stock actuel
    public Livre ajouterStock(Livre livre, Integer quantite) {
        livre.setStock(livre.getStock() + quantite);
        return livreRepository.save(livre);
    }
}
