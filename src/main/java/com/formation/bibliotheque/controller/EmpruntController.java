package com.formation.bibliotheque.controller;

import com.formation.bibliotheque.dto.EmpruntRequest;
import com.formation.bibliotheque.dto.EmpruntResponse;
import com.formation.bibliotheque.mapper.EmpruntMapper;
import com.formation.bibliotheque.model.Emprunt;
import com.formation.bibliotheque.service.EmpruntService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

// Endpoints 15 à 20 : les emprunts. Même plan : vérifier, agir, répondre.
@Tag(name = "Emprunts", description = "Emprunts et retours des livres")
@RestController
@RequestMapping("/api/v1/emprunts")
public class EmpruntController {

    private final EmpruntService empruntService;

    public EmpruntController(EmpruntService empruntService) {
        this.empruntService = empruntService;
    }

    // Endpoint 15 — GET /api/v1/emprunts
    @Operation(summary = "Lister les emprunts (paginé, trié par date d'emprunt)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page d'emprunts")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EmpruntResponse>> listerTous(
            @PageableDefault(size = 10, sort = "dateEmprunt") Pageable pageable) {
        Page<Emprunt> page = empruntService.recupererTous(pageable);
        return ResponseEntity.ok(EmpruntMapper.versPage(page));                    // 200
    }

    // Endpoint 16 — GET /api/v1/emprunts/{id}
    @Operation(summary = "Obtenir un emprunt par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprunt trouvé"),
            @ApiResponse(responseCode = "404", description = "Emprunt inexistant")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmpruntResponse> obtenirParId(@PathVariable Long id) {
        Optional<Emprunt> resultat = empruntService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();                              // 404
        }
        return ResponseEntity.ok(EmpruntMapper.versResponse(resultat.get()));      // 200
    }

    // Endpoint 17 — POST /api/v1/emprunts
    @Operation(summary = "Emprunter un livre (stock - 1, retour prévu dans 14 jours)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Emprunt créé"),
            @ApiResponse(responseCode = "400", description = "Corps invalide (validation) ou livre inexistant"),
            @ApiResponse(responseCode = "409", description = "Livre indisponible (stock à 0)")
    })
    @PostMapping
    public ResponseEntity<EmpruntResponse> creer(@Valid @RequestBody EmpruntRequest request) {
        // 1. Vérifier : d'abord le livre existe, ensuite il est disponible
        if (!empruntService.livreExiste(request.getLivreId())) {
            return ResponseEntity.badRequest().build();                            // 400 livre inexistant
        }
        if (!empruntService.livreDisponible(request.getLivreId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();             // 409 (RM4)
        }
        // 2. Agir (le service applique RM5)
        Emprunt empruntCree = empruntService.creer(EmpruntMapper.versEntite(request));
        // 3. Répondre
        return ResponseEntity.status(HttpStatus.CREATED).body(EmpruntMapper.versResponse(empruntCree)); // 201
    }

    // Endpoint 18 — PATCH /api/v1/emprunts/{id}/retour (pas de corps)
    @Operation(summary = "Rendre un livre emprunté (stock + 1)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprunt rendu"),
            @ApiResponse(responseCode = "404", description = "Emprunt inexistant"),
            @ApiResponse(responseCode = "409", description = "Emprunt déjà rendu")
    })
    @PatchMapping("/{id}/retour")
    public ResponseEntity<EmpruntResponse> retourner(@PathVariable Long id) {
        // 1. Vérifier
        Optional<Emprunt> resultat = empruntService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();                              // 404
        }
        Emprunt emprunt = resultat.get();
        if (emprunt.getRendu()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();             // 409 (RM6)
        }
        // 2. Agir
        Emprunt empruntRendu = empruntService.retourner(emprunt);
        // 3. Répondre
        return ResponseEntity.ok(EmpruntMapper.versResponse(empruntRendu));        // 200
    }

    // Endpoint 19 — GET /api/v1/emprunts/en-cours
    @Operation(summary = "Lister les emprunts en cours (non rendus)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des emprunts non rendus")
    })
    @GetMapping("/en-cours")
    public ResponseEntity<PagedModel<EmpruntResponse>> listerEnCours(
            @PageableDefault(size = 10, sort = "dateEmprunt") Pageable pageable) {
        Page<Emprunt> page = empruntService.recupererEnCours(pageable);
        return ResponseEntity.ok(EmpruntMapper.versPage(page));                    // 200
    }

    // Endpoint 20 — GET /api/v1/emprunts/en-retard (RM7)
    @Operation(summary = "Lister les emprunts en retard")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des emprunts en retard")
    })
    @GetMapping("/en-retard")
    public ResponseEntity<PagedModel<EmpruntResponse>> listerEnRetard(
            @PageableDefault(size = 10, sort = "dateEmprunt") Pageable pageable) {
        Page<Emprunt> page = empruntService.recupererEnRetard(pageable);
        return ResponseEntity.ok(EmpruntMapper.versPage(page));                    // 200
    }
}
