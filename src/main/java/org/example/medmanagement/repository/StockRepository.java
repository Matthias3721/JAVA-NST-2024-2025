package org.example.medmanagement.repository;

import org.example.medmanagement.model.Stock;
import org.example.medmanagement.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByMedication(Medication medication);
}
