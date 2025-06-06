package org.example.medmanagement.repository;

import org.example.medmanagement.model.Prescription;
import org.example.medmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repozytorium dla encji Prescription.
 */
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    /**
     * Zwraca wszystkie recepty przypisane do danego użytkownika.
     */
    List<Prescription> findAllByUser(User user);
}
