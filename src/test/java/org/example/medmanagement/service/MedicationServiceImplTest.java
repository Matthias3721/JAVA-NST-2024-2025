package org.example.medmanagement.service;

import org.example.medmanagement.dto.MedicationDto;
import org.example.medmanagement.model.Medication;
import org.example.medmanagement.repository.MedicationRepository;
import org.example.medmanagement.service.impl.MedicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class MedicationServiceImplTest {

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private MedicationServiceImpl medicationService;

    private Medication exampleMedication;

    @BeforeEach
    void setUp() {
        // Przygotowujemy „wzorcowy” obiekt Medication
        exampleMedication = new Medication();
        exampleMedication.setId(1L);
        exampleMedication.setName("Aspirin");
        exampleMedication.setSubstance("Acetylsalicylic acid");
        exampleMedication.setUnit("mg");
        exampleMedication.setManufacturer("PharmaCo");
    }

    @Test
    void create_ShouldMapDtoToEntityAndReturnDtoWithId() {
        // Arrange: DTO wejściowe (bez id)
        MedicationDto dtoIn = new MedicationDto();
        dtoIn.setName("Paracetamol");
        dtoIn.setSubstance("Acetaminophen");
        dtoIn.setUnit("mg");
        dtoIn.setManufacturer("MediCorp");

        Medication saved = new Medication();
        saved.setId(2L);
        saved.setName(dtoIn.getName());
        saved.setSubstance(dtoIn.getSubstance());
        saved.setUnit(dtoIn.getUnit());
        saved.setManufacturer(dtoIn.getManufacturer());
        when(medicationRepository.save(any(Medication.class))).thenReturn(saved);

        // Act
        MedicationDto result = medicationService.create(dtoIn);

        // Assert: sprawdzamy mapping na DTO
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Paracetamol", result.getName());
        assertEquals("Acetaminophen", result.getSubstance());
        assertEquals("mg", result.getUnit());
        assertEquals("MediCorp", result.getManufacturer());

        ArgumentCaptor<Medication> capt = ArgumentCaptor.forClass(Medication.class);
        verify(medicationRepository, times(1)).save(capt.capture());
        Medication passed = capt.getValue();
        assertNull(passed.getId(), "Przed zapisem id powinno być null");
        assertEquals("Paracetamol", passed.getName());
        assertEquals("Acetaminophen", passed.getSubstance());
        assertEquals("mg", passed.getUnit());
        assertEquals("MediCorp", passed.getManufacturer());
    }

    @Test
    void findById_ShouldReturnDto_WhenExists() {
        // Arrange
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(exampleMedication));

        // Act
        MedicationDto dto = medicationService.findById(1L);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Aspirin", dto.getName());
        assertEquals("Acetylsalicylic acid", dto.getSubstance());
        assertEquals("mg", dto.getUnit());
        assertEquals("PharmaCo", dto.getManufacturer());
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        // Arrange
        when(medicationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> medicationService.findById(99L));
        assertTrue(ex.getMessage().contains("Lek nie znaleziony"));
    }

    @Test
    void update_ShouldThrow_WhenMedicationNotExist() {
        // Arrange
        when(medicationRepository.findById(50L)).thenReturn(Optional.empty());
        MedicationDto dto = new MedicationDto();
        dto.setName("Ibuprofen");
        dto.setSubstance("Ibuprofen");
        dto.setUnit("mg");
        dto.setManufacturer("Generic");

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> medicationService.update(50L, dto));
        assertTrue(ex.getMessage().contains("Lek nie znaleziony"));
    }

    @Test
    void update_ShouldChangeFieldsAndReturnUpdatedDto() {
        // Arrange
        // repo.findById → zwraca exampleMedication
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(exampleMedication));

        //DTO z nowymi wartościami
        MedicationDto dto = new MedicationDto();
        dto.setName("Ibuprofen");
        dto.setSubstance("Ibuprofen");
        dto.setUnit("g");
        dto.setManufacturer("NewPharma");

        Medication updated = new Medication();
        updated.setId(1L);
        updated.setName(dto.getName());
        updated.setSubstance(dto.getSubstance());
        updated.setUnit(dto.getUnit());
        updated.setManufacturer(dto.getManufacturer());
        when(medicationRepository.save(any(Medication.class))).thenReturn(updated);

        // Act
        MedicationDto result = medicationService.update(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ibuprofen", result.getName());
        assertEquals("Ibuprofen", result.getSubstance());
        assertEquals("g", result.getUnit());
        assertEquals("NewPharma", result.getManufacturer());

        ArgumentCaptor<Medication> capt = ArgumentCaptor.forClass(Medication.class);
        verify(medicationRepository, times(1)).save(capt.capture());
        Medication passed = capt.getValue();
        assertEquals("Ibuprofen", passed.getName());
        assertEquals("Ibuprofen", passed.getSubstance());
        assertEquals("g", passed.getUnit());
        assertEquals("NewPharma", passed.getManufacturer());
    }

    @Test
    void delete_ShouldCallRepositoryDeleteById() {
        // Act
        medicationService.delete(5L);

        // Assert
        verify(medicationRepository, times(1)).deleteById(5L);
    }

    @Test
    void findAll_ShouldReturnListOfDtos() {
        // Arrange
        Medication m2 = new Medication();
        m2.setId(2L);
        m2.setName("Penicillin");
        m2.setSubstance("Penicillin G");
        m2.setUnit("mg");
        m2.setManufacturer("BioLabs");

        when(medicationRepository.findAll()).thenReturn(Arrays.asList(exampleMedication, m2));

        // Act
        List<MedicationDto> list = medicationService.findAll();

        // Assert
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(d -> d.getId().equals(1L) && d.getName().equals("Aspirin")));
        assertTrue(list.stream().anyMatch(d -> d.getId().equals(2L) && d.getName().equals("Penicillin")));
    }

    @Test
    void searchByName_ShouldReturnMatchingDtos() {
        // Arrange
        Medication m2 = new Medication();
        m2.setId(2L);
        m2.setName("Aspirin Extra");
        m2.setSubstance("Acetylsalicylic acid");
        m2.setUnit("mg");
        m2.setManufacturer("PharmaCo");

        when(medicationRepository.findByNameContainingIgnoreCase("asp"))
                .thenReturn(Arrays.asList(exampleMedication, m2));

        // Act
        List<MedicationDto> result = medicationService.searchByName("asp");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> d.getName().contains("Aspirin")));
    }
}
