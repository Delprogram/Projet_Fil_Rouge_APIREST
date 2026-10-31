package com.formation.bibliotheque.controller;

import com.formation.bibliotheque.dto.AuteurRequest;
import com.formation.bibliotheque.dto.AuteurResponse;
import com.formation.bibliotheque.dto.LivreResponse;
import com.formation.bibliotheque.mapper.AuteurMapper;
import com.formation.bibliotheque.mapper.LivreMapper;
import com.formation.bibliotheque.model.Auteur;
import com.formation.bibliotheque.model.Livre;
import com.formation.bibliotheque.service.AuteurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

// Endpoints 1 à 6 : les auteurs.
// Chaque méthode suit le même plan : 1. vérifier  2. agir  3. répondre
@Tag(name = "Auteurs", description = "Gestion des auteurs")
@RestController
@RequestMapping("/api/v1/auteurs")
public class AuteurController {

    private final AuteurService auteurService;

    public AuteurController(AuteurService auteurService) {
        this.auteurService = auteurService;
    }

    // Endpoint 1 — GET /api/v1/auteurs : une liste paginée
    @Operation(summary = "Lister les auteurs (paginé, trié par nom)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page d'auteurs")
    })
    @GetMapping
    public ResponseEntity<PagedModel<AuteurResponse>> listerTous(
            @PageableDefault(size = 10, sort = "nom") Pageable pageable) {
        Page<Auteur> page = auteurService.recupererTous(pageable);
        return ResponseEntity.ok(AuteurMapper.versPage(page));                     // 200
    }

    // Endpoint 2 — GET /api/v1/auteurs/{id}
    @Operation(summary = "Obtenir un auteur par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auteur trouvé"),
            @ApiResponse(responseCode = "404", description = "Auteur inexistant")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuteurResponse> obtenirParId(@PathVariable Long id) {
        // 1. Vérifier
        Optional<Auteur> resultat = auteurService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();                              // 404
        }
        // 3. Répondre (rien à "faire" ici, on lit seulement)
        return ResponseEntity.ok(AuteurMapper.versResponse(resultat.get()));       // 200
    }

    // Endpoint 3 — POST /api/v1/auteurs
    // Si le corps est invalide, @Valid renvoie 400 tout seul
    @Operation(summary = "Créer un auteur")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Auteur créé"),
            @ApiResponse(responseCode = "400", description = "Corps invalide (validation)")
    })
    @PostMapping
    public ResponseEntity<AuteurResponse> creer(@Valid @RequestBody AuteurRequest request) {
        // 2. Agir
        Auteur auteurCree = auteurService.creer(AuteurMapper.versEntite(request));
        // 3. Répondre
        return ResponseEntity.status(HttpStatus.CREATED).body(AuteurMapper.versResponse(auteurCree)); // 201
    }

    // Endpoint 4 — PUT /api/v1/auteurs/{id}
    @Operation(summary = "Modifier un auteur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auteur modifié"),
            @ApiResponse(responseCode = "400", description = "Corps invalide (validation)"),
            @ApiResponse(responseCode = "404", description = "Auteur inexistant")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AuteurResponse> mettreAJour(@PathVariable Long id,
                                                      @Valid @RequestBody AuteurRequest request) {
        // 1. Vérifier
        Optional<Auteur> resultat = auteurService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();                              // 404
        }
        // 2. Agir
        Auteur auteurModifie = auteurService.mettreAJour(resultat.get(), AuteurMapper.versEntite(request));
        // 3. Répondre
        return ResponseEntity.ok(AuteurMapper.versResponse(auteurModifie));        // 200
    }

    // Endpoint 5 — DELETE /api/v1/auteurs/{id} : deux vérifications, puis l'action
    @Operation(summary = "Supprimer un auteur (impossible s'il a encore des livres)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Auteur supprimé"),
            @ApiResponse(responseCode = "404", description = "Auteur inexistant"),
            @ApiResponse(responseCode = "409", description = "L'auteur a encore des livres")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        // 1. Vérifier, dans l'ordre du tableau
        if (!auteurService.existe(id)) {
            return ResponseEntity.notFound().build();                              // 404
        }
        if (auteurService.aDesLivres(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();             // 409 (RM3)
        }
        // 2. Agir
        auteurService.supprimer(id);
        // 3. Répondre
        return ResponseEntity.noContent().build();                                 // 204
    }

    // Endpoint 6 — GET /api/v1/auteurs/{id}/livres
    @Operation(summary = "Lister les livres d'un auteur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des livres de l'auteur"),
            @ApiResponse(responseCode = "404", description = "Auteur inexistant")
    })
    @GetMapping("/{id}/livres")
    public ResponseEntity<PagedModel<LivreResponse>> listerLivres(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "titre") Pageable pageable) {
        // 1. Vérifier
        if (!auteurService.existe(id)) {
            return ResponseEntity.notFound().build();                              // 404
        }
        // 2. Agir
        Page<Livre> page = auteurService.recupererLivres(id, pageable);
        // 3. Répondre
        return ResponseEntity.ok(LivreMapper.versPage(page));                      // 200
    }
}
