package com.formation.bibliotheque.repository;

import com.formation.bibliotheque.model.Livre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {

    Optional<Livre> findByIsbn(String isbn);

    boolean existsByAuteurId(Long auteurId);

    Page<Livre> findByAuteurId(Long auteurId, Pageable pageable);

    Page<Livre> findByTitreContainingIgnoreCase(String titre, Pageable pageable);

    Page<Livre> findByStockGreaterThan(Integer stock, Pageable pageable);
}
