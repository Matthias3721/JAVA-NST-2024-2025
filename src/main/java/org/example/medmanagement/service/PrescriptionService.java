package org.example.medmanagement.service;

import org.example.medmanagement.dto.PrescriptionDto;

import java.util.List;

/**
 * Serwis zarządzający receptami.
 *
 * Wszystkie metody pracują na obiektach DTO (PrescriptionDto), nie na encjach bezpośrednio.
 */
public interface PrescriptionService {

    /**
     * Tworzy nową receptę i zwraca utworzony DTO.
     */
    PrescriptionDto create(PrescriptionDto dto);

    /**
     * Aktualizuje receptę o podanym ID na podstawie przekazanego DTO,
     * następnie zwraca zaktualizowany DTO.
     */
    PrescriptionDto update(Long id, PrescriptionDto dto);

    /**
     * Usuwa receptę o danym ID.
     */
    void delete(Long id);

    /**
     * Zwraca DTO recepty o danym ID.
     */
    PrescriptionDto findById(Long id);

    /**
     * Zwraca wszystkie recepty należące do użytkownika o podanym ID.
     */
    List<PrescriptionDto> findByUserId(Long userId);

    /**
     * Zwraca listę wszystkich recept (w postaci DTO).
     */
    List<PrescriptionDto> findAll();
}
