package org.example.medmanagement.service;

import org.example.medmanagement.dto.MedicationDto;

import java.util.List;

public interface MedicationService {
    MedicationDto create(MedicationDto dto);
    MedicationDto update(Long id, MedicationDto dto);
    void delete(Long id);
    MedicationDto findById(Long id);
    List<MedicationDto> findAll();
    List<MedicationDto> searchByName(String name);
}
