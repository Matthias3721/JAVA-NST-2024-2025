package org.example.medmanagement.controller;

import org.example.medmanagement.model.Stock;
import org.example.medmanagement.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<Stock>> getAll() {
        return ResponseEntity.ok(stockService.findAll());
    }

    @GetMapping("/{medId}")
    public ResponseEntity<Stock> getByMedication(@PathVariable Long medId) {
        return ResponseEntity.ok(stockService.getByMedicationId(medId));
    }

    @PutMapping("/{medId}")
    public ResponseEntity<Stock> updateQuantity(@PathVariable Long medId,
                                                @RequestParam Integer quantity) {
        return ResponseEntity.ok(stockService.updateQuantity(medId, quantity));
    }
}
