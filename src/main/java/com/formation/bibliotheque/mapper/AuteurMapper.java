package com.formation.bibliotheque.mapper;

import com.formation.bibliotheque.dto.AuteurRequest;
import com.formation.bibliotheque.dto.AuteurResponse;
import com.formation.bibliotheque.model.Auteur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

import java.util.ArrayList;
import java.util.List;

public final class AuteurMapper {

    private AuteurMapper() {
    }

    public static Auteur versEntite(AuteurRequest request) {
        Auteur auteur = new Auteur();
        auteur.setNom(request.getNom());
        auteur.setPrenom(request.getPrenom());
        auteur.setNationalite(request.getNationalite());
        return auteur;
    }

    public static AuteurResponse versResponse(Auteur auteur) {
        return new AuteurResponse(
                auteur.getId(),
                auteur.getNom(),
                auteur.getPrenom(),
                auteur.getNationalite()
        );
    }

    public static List<AuteurResponse> versResponses(List<Auteur> auteurs) {
        List<AuteurResponse> reponses = new ArrayList<>();
        for (Auteur auteur : auteurs) {
            reponses.add(versResponse(auteur));
        }
        return reponses;
    }

    public static PagedModel<AuteurResponse> versPage(Page<Auteur> page) {
        List<AuteurResponse> contenu = versResponses(page.getContent());
        Page<AuteurResponse> pageDto = new PageImpl<>(contenu, page.getPageable(), page.getTotalElements());
        return new PagedModel<>(pageDto);
    }
}
