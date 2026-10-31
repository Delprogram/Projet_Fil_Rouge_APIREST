package com.formation.bibliotheque.service;

import com.formation.bibliotheque.model.Auteur;
import com.formation.bibliotheque.model.Livre;
import com.formation.bibliotheque.repository.AuteurRepository;
import com.formation.bibliotheque.repository.LivreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

// La logique métier des auteurs. Le service ne connaît pas HTTP :
// il renvoie des objets, c'est le contrôleur qui choisit le code (200, 404...).
@Service
public class AuteurService {

    private final AuteurRepository auteurRepository;
    private final LivreRepository livreRepository;

    public AuteurService(AuteurRepository auteurRepository, LivreRepository livreRepository) {
        this.auteurRepository = auteurRepository;
        this.livreRepository = livreRepository;
    }

    // Une page d'auteurs
    public Page<Auteur> recupererTous(Pageable pageable) {
        return auteurRepository.findAll(pageable);
    }

    // L'auteur, ou un Optional vide s'il n'existe pas
    public Optional<Auteur> recupererParId(Long id) {
        return auteurRepository.findById(id);
    }

    public boolean existe(Long id) {
        return auteurRepository.existsById(id);
    }

    public Auteur creer(Auteur auteur) {
        // id à null pour être sûr de créer, et pas de modifier un auteur existant
        auteur.setId(null);
        return auteurRepository.save(auteur);
    }

    public Auteur mettreAJour(Auteur auteurExistant, Auteur nouvellesValeurs) {
        auteurExistant.setNom(nouvellesValeurs.getNom());
        auteurExistant.setPrenom(nouvellesValeurs.getPrenom());
        auteurExistant.setNationalite(nouvellesValeurs.getNationalite());
        return auteurRepository.save(auteurExistant);
    }

    // RM3 : true si l'auteur a encore au moins un livre
    public boolean aDesLivres(Long id) {
        return livreRepository.existsByAuteurId(id);
    }

    // Les vérifications sont déjà faites par le contrôleur
    public void supprimer(Long id) {
        auteurRepository.deleteById(id);
    }

    // Endpoint 6 : les livres d'un auteur
    public Page<Livre> recupererLivres(Long id, Pageable pageable) {
        return livreRepository.findByAuteurId(id, pageable);
    }
}
