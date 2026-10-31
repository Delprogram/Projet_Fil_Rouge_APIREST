package com.formation.bibliotheque.service;

import com.formation.bibliotheque.model.Emprunt;
import com.formation.bibliotheque.model.Livre;
import com.formation.bibliotheque.repository.EmpruntRepository;
import com.formation.bibliotheque.repository.LivreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

// La logique métier des emprunts (RM4 à RM7)
@Service
public class EmpruntService {

    /** Durée maximale d'un emprunt, en jours (RM5). */
    public static final int DUREE_MAX_JOURS = 14;

    private final EmpruntRepository empruntRepository;
    private final LivreRepository livreRepository;

    public EmpruntService(EmpruntRepository empruntRepository, LivreRepository livreRepository) {
        this.empruntRepository = empruntRepository;
        this.livreRepository = livreRepository;
    }

    public Page<Emprunt> recupererTous(Pageable pageable) {
        return empruntRepository.findAll(pageable);
    }

    public Optional<Emprunt> recupererParId(Long id) {
        return empruntRepository.findById(id);
    }

    public boolean livreExiste(Long livreId) {
        return livreRepository.existsById(livreId);
    }

    // RM4 : le livre existe ET il en reste au moins un
    public boolean livreDisponible(Long livreId) {
        Optional<Livre> resultat = livreRepository.findById(livreId);
        if (resultat.isEmpty()) {
            return false;
        }
        return resultat.get().getStock() > 0;
    }

    // RM5 : on remplit l'emprunt et on retire 1 au stock du livre
    public Emprunt creer(Emprunt emprunt) {
        LocalDate aujourdhui = LocalDate.now();

        emprunt.setId(null);
        emprunt.setDateEmprunt(aujourdhui);
        emprunt.setDateRetourPrevue(aujourdhui.plusDays(DUREE_MAX_JOURS));
        emprunt.setDateRetourEffective(null);
        emprunt.setRendu(false);

        // Stock du livre - 1
        Optional<Livre> resultat = livreRepository.findById(emprunt.getLivreId());
        if (resultat.isPresent()) {
            Livre livre = resultat.get();
            livre.setStock(livre.getStock() - 1);
            livreRepository.save(livre);
        }

        return empruntRepository.save(emprunt);
    }

    // RM6 : on marque l'emprunt rendu et on remet 1 au stock du livre
    public Emprunt retourner(Emprunt emprunt) {
        emprunt.setRendu(true);
        emprunt.setDateRetourEffective(LocalDate.now());

        // Le livre a pu être supprimé entre-temps : on vérifie
        Optional<Livre> resultat = livreRepository.findById(emprunt.getLivreId());
        if (resultat.isPresent()) {
            Livre livre = resultat.get();
            livre.setStock(livre.getStock() + 1);
            livreRepository.save(livre);
        }

        return empruntRepository.save(emprunt);
    }

    // Emprunts pas encore rendus
    public Page<Emprunt> recupererEnCours(Pageable pageable) {
        return empruntRepository.findByRenduFalse(pageable);
    }

    // RM7 : pas rendus et date prévue avant aujourd'hui
    public Page<Emprunt> recupererEnRetard(Pageable pageable) {
        return empruntRepository.findByRenduFalseAndDateRetourPrevueBefore(LocalDate.now(), pageable);
    }
}
