package org.example.medmanagement.repository;

import org.example.medmanagement.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repozytorium dla encji Medication.
 */
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    /**
     * Wyszukiwanie leków po fragmencie nazwy
     */
    List<Medication> findByNameContainingIgnoreCase(String name);
}
