package org.example.medmanagement.service.impl;

import org.example.medmanagement.model.Medication;
import org.example.medmanagement.model.Stock;
import org.example.medmanagement.repository.MedicationRepository;
import org.example.medmanagement.repository.StockRepository;
import org.example.medmanagement.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final MedicationRepository medicationRepository;

    @Override
    public Stock getByMedicationId(Long medId) {
        Medication med = medicationRepository.findById(medId)
                .orElseThrow(() -> new RuntimeException("Lek nie istnieje: id=" + medId));
        return stockRepository.findByMedication(med)
                .orElseThrow(() -> new RuntimeException("Stan magazynowy nie znaleziony dla leku: id=" + medId));
    }

    @Override
    public Stock updateQuantity(Long medId, Integer newQuantity) {
        Stock stock = getByMedicationId(medId);
        stock.setQuantity(newQuantity);
        return stockRepository.save(stock);
    }

    @Override
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }
}
