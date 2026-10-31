package com.formation.bibliotheque.controller;

import com.formation.bibliotheque.dto.LivreRequest;
import com.formation.bibliotheque.dto.LivreResponse;
import com.formation.bibliotheque.mapper.LivreMapper;
import com.formation.bibliotheque.model.Livre;
import com.formation.bibliotheque.service.LivreService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

// Endpoints 7 à 14 : les livres. Même plan : vérifier, agir, répondre.
@Tag(name = "Livres", description = "Catalogue des livres")
@RestController
@RequestMapping("/api/v1/livres")
public class LivreController {

    private final LivreService livreService;

    public LivreController(LivreService livreService) {
        this.livreService = livreService;
    }

    // Endpoint 7 — GET /api/v1/livres
    @Operation(summary = "Lister les livres (paginé, trié par titre)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page de livres")
    })
    @GetMapping
    public ResponseEntity<PagedModel<LivreResponse>> listerTous(
            @PageableDefault(size = 10, sort = "titre") Pageable pageable) {
        Page<Livre> page = livreService.recupererTous(pageable);
        return ResponseEntity.ok(LivreMapper.versPage(page));                      // 200
    }

    // Endpoint 8 — GET /api/v1/livres/{id}
    @Operation(summary = "Obtenir un livre par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livre trouvé"),
            @ApiResponse(responseCode = "404", description = "Livre inexistant")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LivreResponse> obtenirParId(@PathVariable Long id) {
        Optional<Livre> resultat = livreService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();                              // 404
        }
        return ResponseEntity.ok(LivreMapper.versResponse(resultat.get()));        // 200
    }

    // Endpoint 9 — POST /api/v1/livres
    @Operation(summary = "Créer un livre")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Livre créé"),
            @ApiResponse(responseCode = "400", description = "Corps invalide (validation) ou auteur inexistant"),
            @ApiResponse(responseCode = "409", description = "ISBN déjà utilisé")
    })
    @PostMapping
    public ResponseEntity<LivreResponse> creer(@Valid @RequestBody LivreRequest request) {
        // 1. Vérifier
        if (!livreService.auteurExiste(request.getAuteurId())) {
            return ResponseEntity.badRequest().build();                            // 400 auteur inexistant
        }
        if (livreService.isbnExiste(request.getIsbn())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();             // 409 (RM2)
        }
        // 2. Agir
        Livre livreCree = livreService.creer(LivreMapper.versEntite(request));
        // 3. Répondre
        return ResponseEntity.status(HttpStatus.CREATED).body(LivreMapper.versResponse(livreCree)); // 201
    }

    // Endpoint 10 — PUT /api/v1/livres/{id}
    // Attention à l'ordre : d'abord 404 si le livre n'existe pas
    @Operation(summary = "Modifier un livre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livre modifié"),
            @ApiResponse(responseCode = "400", description = "Corps invalide (validation) ou auteur inexistant"),
            @ApiResponse(responseCode = "404", description = "Livre inexistant"),
            @ApiResponse(responseCode = "409", description = "ISBN déjà utilisé par un autre livre")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LivreResponse> mettreAJour(@PathVariable Long id,
                                                     @Valid @RequestBody LivreRequest request) {
        // 1. Vérifier
        Optional<Livre> resultat = livreService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();                              // 404
        }
        if (!livreService.auteurExiste(request.getAuteurId())) {
            return ResponseEntity.badRequest().build();                            // 400 auteur inexistant
        }
        if (livreService.isbnUtiliseParUnAutreLivre(request.getIsbn(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();             // 409 (RM2)
        }
        // 2. Agir
        Livre livreModifie = livreService.mettreAJour(resultat.get(), LivreMapper.versEntite(request));
        // 3. Répondre
        return ResponseEntity.ok(LivreMapper.versResponse(livreModifie));          // 200
    }

    // Endpoint 11 — DELETE /api/v1/livres/{id}
    @Operation(summary = "Supprimer un livre")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Livre supprimé"),
            @ApiResponse(responseCode = "404", description = "Livre inexistant")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!livreService.existe(id)) {
            return ResponseEntity.notFound().build();                              // 404
        }
        livreService.supprimer(id);
        return ResponseEntity.noContent().build();                                 // 204
    }

    // Endpoint 12 — GET /api/v1/livres/recherche?titre=...
    // Si "titre" est absent, Spring renvoie 400 tout seul
    @Operation(summary = "Rechercher des livres par titre (sans tenir compte des majuscules)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des livres trouvés (peut être vide)"),
            @ApiResponse(responseCode = "400", description = "Paramètre titre absent")
    })
    @GetMapping("/recherche")
    public ResponseEntity<PagedModel<LivreResponse>> rechercher(
            @RequestParam String titre,
            @PageableDefault(size = 10, sort = "titre") Pageable pageable) {
        Page<Livre> page = livreService.rechercherParTitre(titre, pageable);
        return ResponseEntity.ok(LivreMapper.versPage(page));                      // 200
    }

    // Endpoint 13 — GET /api/v1/livres/disponibles (RM4 : stock > 0)
    @Operation(summary = "Lister les livres disponibles (stock > 0)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des livres disponibles")
    })
    @GetMapping("/disponibles")
    public ResponseEntity<PagedModel<LivreResponse>> listerDisponibles(
            @PageableDefault(size = 10, sort = "titre") Pageable pageable) {
        Page<Livre> page = livreService.recupererDisponibles(pageable);
        return ResponseEntity.ok(LivreMapper.versPage(page));                      // 200
    }

    // Endpoint 14 — PATCH /api/v1/livres/{id}/stock?quantite=...
    @Operation(summary = "Réapprovisionner un livre (ajoute quantite au stock)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock mis à jour"),
            @ApiResponse(responseCode = "400", description = "Quantité absente, non numérique ou ≤ 0"),
            @ApiResponse(responseCode = "404", description = "Livre inexistant")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<LivreResponse> ajouterStock(@PathVariable Long id,
                                                      @RequestParam Integer quantite) {
        // 1. Vérifier : d'abord la quantité, ensuite le livre
        if (quantite <= 0) {
            return ResponseEntity.badRequest().build();                            // 400
        }
        Optional<Livre> resultat = livreService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();                              // 404
        }
        // 2. Agir
        Livre livre = livreService.ajouterStock(resultat.get(), quantite);
        // 3. Répondre
        return ResponseEntity.ok(LivreMapper.versResponse(livre));                 // 200
    }
}
