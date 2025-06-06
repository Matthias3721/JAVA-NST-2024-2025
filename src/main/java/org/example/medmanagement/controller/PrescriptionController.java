package org.example.medmanagement.controller;

import org.example.medmanagement.dto.PrescriptionDto;
import org.example.medmanagement.service.PrescriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler obsługujący operacje CRUD dla encji Prescription.
 */
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    /**
     * Tworzy nową receptę i zwraca DTO utworzonego zasobu (HTTP 201).
     */
    @PostMapping
    public ResponseEntity<PrescriptionDto> create(@RequestBody PrescriptionDto dto) {
        PrescriptionDto created = prescriptionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Zwraca receptę o danym ID (HTTP 200).
     * Jeśli serwis rzuci RuntimeException("Recepta nie znaleziona: id=..."), zwracamy HTTP 404 z treścią wyjątku.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            PrescriptionDto dto = prescriptionService.findById(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    /**
     * Aktualizuje receptę o danym ID (HTTP 200).
     * W razie braku recepty serwis rzuca RuntimeException("Recepta nie znaleziona: id=...") → zwracamy HTTP 404.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody PrescriptionDto dto) {
        try {
            PrescriptionDto updated = prescriptionService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    /**
     * Usuwa receptę o danym ID (HTTP 204).
     * W razie braku recepty serwis rzuca RuntimeException("Recepta nie znaleziona: id=...") → zwracamy HTTP 404.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            prescriptionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    /**
     * Zwraca listę wszystkich recept (HTTP 200).
     */
    @GetMapping
    public ResponseEntity<List<PrescriptionDto>> findAll() {
        List<PrescriptionDto> list = prescriptionService.findAll();
        return ResponseEntity.ok(list);
    }

    /**
     * Zwraca listę recept dla danego userId (HTTP 200).
     * Przykładowy endpoint: /api/prescriptions/search?userId=123
     */
    @GetMapping("/search")
    public ResponseEntity<List<PrescriptionDto>> findByUserId(@RequestParam("userId") Long userId) {
        List<PrescriptionDto> list = prescriptionService.findByUserId(userId);
        return ResponseEntity.ok(list);
    }
}
