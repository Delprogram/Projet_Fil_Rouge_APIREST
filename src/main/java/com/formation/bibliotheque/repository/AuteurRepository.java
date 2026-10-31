package com.formation.bibliotheque.repository;

import com.formation.bibliotheque.model.Auteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository nous donne déjà findAll, findById, existsById, save, deleteById
@Repository
public interface AuteurRepository extends JpaRepository<Auteur, Long> {
    // aucune méthode à ajouter
}
