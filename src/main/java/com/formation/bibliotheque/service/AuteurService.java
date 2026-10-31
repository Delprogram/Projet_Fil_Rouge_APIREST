package com.formation.bibliotheque.service;

import com.formation.bibliotheque.model.Auteur;
import com.formation.bibliotheque.model.Livre;
import com.formation.bibliotheque.repository.AuteurRepository;
import com.formation.bibliotheque.repository.LivreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuteurService {

    private final AuteurRepository auteurRepository;
    private final LivreRepository livreRepository;

    public AuteurService(AuteurRepository auteurRepository, LivreRepository livreRepository) {
        this.auteurRepository = auteurRepository;
        this.livreRepository = livreRepository;
    }

    public Page<Auteur> recupererTous(Pageable pageable) {
        return auteurRepository.findAll(pageable);
    }

    public Optional<Auteur> recupererParId(Long id) {
        return auteurRepository.findById(id);
    }

    public boolean existe(Long id) {
        return auteurRepository.existsById(id);
    }

    public Auteur creer(Auteur auteur) {
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

    public void supprimer(Long id) {
        auteurRepository.deleteById(id);
    }

    public Page<Livre> recupererLivres(Long id, Pageable pageable) {
        return livreRepository.findByAuteurId(id, pageable);
    }
}
