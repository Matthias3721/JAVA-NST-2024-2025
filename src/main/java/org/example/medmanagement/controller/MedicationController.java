package org.example.medmanagement.controller;

import org.example.medmanagement.dto.MedicationDto;
import org.example.medmanagement.service.MedicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler obsługujący wszystkie operacje związane z lekami.
 */
@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    /**
     * Tworzy nowy lek i zwraca DTO utworzonego zasobu.
     */
    @PostMapping
    public ResponseEntity<MedicationDto> create(@RequestBody MedicationDto dto) {
        MedicationDto created = medicationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Zwraca szczegóły leku o podanym ID.
     * Jeśli serwis rzuci RuntimeException("Lek nie znaleziony: id=..."),
     * zwracamy 404 Not Found z komunikatem błędu w ciele odpowiedzi.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            MedicationDto dto = medicationService.findById(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    /**
     * Aktualizuje lek o danym ID.
     * W razie braku leku – 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody MedicationDto dto) {
        try {
            MedicationDto updated = medicationService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    /**
     * Usuwa lek o danym ID.
     * W razie braku leku – 404 Not Found,
     * w przeciwnym razie 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            medicationService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    /**
     * Zwraca listę wszystkich leków w postaci DTO.
     */
    @GetMapping
    public ResponseEntity<List<MedicationDto>> findAll() {
        List<MedicationDto> list = medicationService.findAll();
        return ResponseEntity.ok(list);
    }

    /**
     * Wyszukuje leki zawierające w nazwie (case‐insensitive) podany parametr name.
     */
    @GetMapping("/search")
    public ResponseEntity<List<MedicationDto>> searchByName(@RequestParam("name") String name) {
        List<MedicationDto> list = medicationService.searchByName(name);
        return ResponseEntity.ok(list);
    }
}
