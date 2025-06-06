package org.example.medmanagement.service;

import org.example.medmanagement.model.Stock;

import java.util.List;

public interface StockService {
    Stock getByMedicationId(Long medId);
    Stock updateQuantity(Long medId, Integer newQuantity);
    List<Stock> findAll();
}
