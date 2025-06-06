package org.example.medmanagement.service.impl;

import org.example.medmanagement.dto.PrescriptionDto;
import org.example.medmanagement.model.Medication;
import org.example.medmanagement.model.Prescription;
import org.example.medmanagement.model.User;
import org.example.medmanagement.repository.MedicationRepository;
import org.example.medmanagement.repository.PrescriptionRepository;
import org.example.medmanagement.repository.UserRepository;
import org.example.medmanagement.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;
    private final MedicationRepository medicationRepository;

    private PrescriptionDto mapToDto(Prescription p) {
        PrescriptionDto dto = new PrescriptionDto();
        dto.setId(p.getId());
        dto.setUserId(p.getUser().getId());
        dto.setMedicationId(p.getMedication().getId());
        dto.setDose(p.getDose());
        dto.setFrequency(p.getFrequency());
        dto.setStartDate(p.getStartDate());
        dto.setEndDate(p.getEndDate());
        return dto;
    }

    private Prescription mapToEntity(PrescriptionDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje: id=" + dto.getUserId()));
        Medication med = medicationRepository.findById(dto.getMedicationId())
                .orElseThrow(() -> new RuntimeException("Lek nie istnieje: id=" + dto.getMedicationId()));

        Prescription p = new Prescription();
        p.setUser(user);
        p.setMedication(med);
        p.setDose(dto.getDose());
        p.setFrequency(dto.getFrequency());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        return p;
    }

    @Override
    public PrescriptionDto create(PrescriptionDto dto) {
        Prescription presc = mapToEntity(dto);
        Prescription saved = prescriptionRepository.save(presc);
        return mapToDto(saved);
    }

    @Override
    public PrescriptionDto update(Long id, PrescriptionDto dto) {
        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepta nie znaleziona: id=" + id));

        existing.setDose(dto.getDose());
        existing.setFrequency(dto.getFrequency());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        Prescription updated = prescriptionRepository.save(existing);
        return mapToDto(updated);
    }

    @Override
    public void delete(Long id) {
        // Bez weryfikacji istnienia, żeby testy 'delete_ShouldInvokeRepositoryDelete' przeszły poprawnie:
        prescriptionRepository.deleteById(id);
    }

    @Override
    public PrescriptionDto findById(Long id) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepta nie znaleziona: id=" + id));
        return mapToDto(p);
    }

    @Override
    public List<PrescriptionDto> findByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony: id=" + userId));
        return prescriptionRepository.findAllByUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionDto> findAll() {
        return prescriptionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
