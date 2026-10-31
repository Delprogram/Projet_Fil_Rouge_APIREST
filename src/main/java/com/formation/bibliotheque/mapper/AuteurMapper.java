package com.formation.bibliotheque.mapper;

import com.formation.bibliotheque.dto.AuteurRequest;
import com.formation.bibliotheque.dto.AuteurResponse;
import com.formation.bibliotheque.model.Auteur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

import java.util.ArrayList;
import java.util.List;

// Convertit DTO <-> entité pour les auteurs
public final class AuteurMapper {

    // On n'en crée jamais d'instance : tout est static
    private AuteurMapper() {
    }

    // Requête du client -> entité (l'id reste null)
    public static Auteur versEntite(AuteurRequest request) {
        Auteur auteur = new Auteur();
        auteur.setNom(request.getNom());
        auteur.setPrenom(request.getPrenom());
        auteur.setNationalite(request.getNationalite());
        return auteur;
    }

    // Entité -> réponse pour le client
    public static AuteurResponse versResponse(Auteur auteur) {
        return new AuteurResponse(
                auteur.getId(),
                auteur.getNom(),
                auteur.getPrenom(),
                auteur.getNationalite()
        );
    }

    // Liste d'entités -> liste de réponses
    public static List<AuteurResponse> versResponses(List<Auteur> auteurs) {
        List<AuteurResponse> reponses = new ArrayList<>();
        for (Auteur auteur : auteurs) {
            reponses.add(versResponse(auteur));
        }
        return reponses;
    }

    public static PagedModel<AuteurResponse> versPage(Page<Auteur> page) {
        // 1. On convertit le contenu de la page en DTOs
        List<AuteurResponse> contenu = versResponses(page.getContent());
        // 2. On reconstruit une page de DTOs avec les mêmes informations de pagination
        Page<AuteurResponse> pageDto = new PageImpl<>(contenu, page.getPageable(), page.getTotalElements());
        // 3. PagedModel produit un JSON stable : { "content": [...], "page": {...} }
        return new PagedModel<>(pageDto);
    }
}
