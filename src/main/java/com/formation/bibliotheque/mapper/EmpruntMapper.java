package com.formation.bibliotheque.mapper;

import com.formation.bibliotheque.dto.EmpruntRequest;
import com.formation.bibliotheque.dto.EmpruntResponse;
import com.formation.bibliotheque.model.Emprunt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class EmpruntMapper {

    private EmpruntMapper() {
    }

    // Les dates et "rendu" restent vides : c'est le service qui les remplit (RM5).
    public static Emprunt versEntite(EmpruntRequest request) {
        Emprunt emprunt = new Emprunt();
        emprunt.setLivreId(request.getLivreId());
        emprunt.setEmprunteur(request.getEmprunteur());
        emprunt.setEmailEmprunteur(request.getEmailEmprunteur());
        return emprunt;
    }

    // Entité -> réponse. On ne recopie PAS l'email (RM8).
    public static EmpruntResponse versResponse(Emprunt emprunt) {
        // RM7 : en retard = pas rendu ET date prévue déjà passée
        boolean enRetard = !emprunt.getRendu()
                && emprunt.getDateRetourPrevue().isBefore(LocalDate.now());

        return new EmpruntResponse(
                emprunt.getId(),
                emprunt.getLivreId(),
                emprunt.getEmprunteur(),
                emprunt.getDateEmprunt(),
                emprunt.getDateRetourPrevue(),
                emprunt.getDateRetourEffective(),
                emprunt.getRendu(),
                enRetard
        );
    }

    public static List<EmpruntResponse> versResponses(List<Emprunt> emprunts) {
        List<EmpruntResponse> reponses = new ArrayList<>();
        for (Emprunt emprunt : emprunts) {
            reponses.add(versResponse(emprunt));
        }
        return reponses;
    }

    public static PagedModel<EmpruntResponse> versPage(Page<Emprunt> page) {
        List<EmpruntResponse> contenu = versResponses(page.getContent());
        Page<EmpruntResponse> pageDto = new PageImpl<>(contenu, page.getPageable(), page.getTotalElements());
        return new PagedModel<>(pageDto);
    }
}
