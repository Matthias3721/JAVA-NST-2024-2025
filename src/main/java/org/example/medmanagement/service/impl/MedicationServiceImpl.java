package org.example.medmanagement.service.impl;

import org.example.medmanagement.dto.MedicationDto;
import org.example.medmanagement.model.Medication;
import org.example.medmanagement.repository.MedicationRepository;
import org.example.medmanagement.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;

    private MedicationDto mapToDto(Medication m) {
        MedicationDto dto = new MedicationDto();
        dto.setId(m.getId());
        dto.setName(m.getName());
        dto.setSubstance(m.getSubstance());
        dto.setUnit(m.getUnit());
        dto.setManufacturer(m.getManufacturer());
        return dto;
    }

    private Medication mapToEntity(MedicationDto dto) {
        Medication m = new Medication();
        m.setName(dto.getName());
        m.setSubstance(dto.getSubstance());
        m.setUnit(dto.getUnit());
        m.setManufacturer(dto.getManufacturer());
        return m;
    }

    @Override
    public MedicationDto create(MedicationDto dto) {
        Medication med = mapToEntity(dto);
        Medication saved = medicationRepository.save(med);
        return mapToDto(saved);
    }

    @Override
    public MedicationDto findById(Long id) {
        Medication m = medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lek nie znaleziony: id=" + id));
        return mapToDto(m);
    }

    @Override
    public MedicationDto update(Long id, MedicationDto dto) {
        Medication existing = medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lek nie znaleziony: id=" + id));

        existing.setName(dto.getName());
        existing.setSubstance(dto.getSubstance());
        existing.setUnit(dto.getUnit());
        existing.setManufacturer(dto.getManufacturer());
        Medication updated = medicationRepository.save(existing);
        return mapToDto(updated);
    }

    @Override
    public void delete(Long id) {
        // Usuwamy bez uprzedniego sprawdzania istnienia
        medicationRepository.deleteById(id);
    }

    @Override
    public List<MedicationDto> findAll() {
        return medicationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicationDto> searchByName(String name) {
        return medicationRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
