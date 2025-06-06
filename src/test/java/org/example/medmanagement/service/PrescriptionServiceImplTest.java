package org.example.medmanagement.service;

import org.example.medmanagement.dto.PrescriptionDto;
import org.example.medmanagement.model.Medication;
import org.example.medmanagement.model.Prescription;
import org.example.medmanagement.model.User;
import org.example.medmanagement.repository.MedicationRepository;
import org.example.medmanagement.repository.PrescriptionRepository;
import org.example.medmanagement.repository.UserRepository;
import org.example.medmanagement.service.impl.PrescriptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PrescriptionServiceImplTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    private User exampleUser;
    private Medication exampleMed;
    private Prescription examplePresc;
    private PrescriptionDto exampleDto;

    @BeforeEach
    void setUp() {
        // Użytkownik z ID=1
        exampleUser = new User();
        exampleUser.setId(1L);
        exampleUser.setEmail("testuser@example.com");
        exampleUser.setEnabled(true);
        exampleUser.setPassword("irrelevant");

        // Lek z ID=2
        exampleMed = new Medication();
        exampleMed.setId(2L);
        exampleMed.setName("TestMed");
        exampleMed.setSubstance("TestSub");
        exampleMed.setUnit("mg");
        exampleMed.setManufacturer("TestPharma");

        // Przykładowa recepta z ID=3
        examplePresc = new Prescription();
        examplePresc.setId(3L);
        examplePresc.setUser(exampleUser);
        examplePresc.setMedication(exampleMed);
        examplePresc.setDose("1 pill");
        examplePresc.setFrequency("Twice a day");
        examplePresc.setStartDate(LocalDate.of(2025, 1, 1));
        examplePresc.setEndDate(LocalDate.of(2025, 12, 31));

        // DTO do tworzenia i aktualizacji (bez ID)
        exampleDto = new PrescriptionDto();
        exampleDto.setUserId(1L);
        exampleDto.setMedicationId(2L);
        exampleDto.setDose("1 pill");
        exampleDto.setFrequency("Twice a day");
        exampleDto.setStartDate(LocalDate.of(2025, 1, 1));
        exampleDto.setEndDate(LocalDate.of(2025, 12, 31));
    }

    // 1) create(...) – wyjątki

    @Test
    void create_ShouldThrow_WhenUserNotExist() {
        // userRepository.findById(1L) → empty
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        PrescriptionDto dto = new PrescriptionDto();
        dto.setUserId(1L);
        dto.setMedicationId(2L);
        dto.setDose("X");
        dto.setFrequency("Y");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> prescriptionService.create(dto));
        assertTrue(ex.getMessage().contains("Użytkownik nie istnieje: id=1"));
    }

    @Test
    void create_ShouldThrow_WhenMedicationNotExist() {
        // user istnieje, medication nie
        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));
        when(medicationRepository.findById(2L)).thenReturn(Optional.empty());

        PrescriptionDto dto = new PrescriptionDto();
        dto.setUserId(1L);
        dto.setMedicationId(2L);
        dto.setDose("X");
        dto.setFrequency("Y");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> prescriptionService.create(dto));
        assertTrue(ex.getMessage().contains("Lek nie istnieje: id=2"));
    }

    // 2) create(...) – ścieżka poprawna

    @Test
    void create_ShouldMapAndSave_WhenUserAndMedicationExist() {
        // Przygotowanie stubów
        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));
        when(medicationRepository.findById(2L)).thenReturn(Optional.of(exampleMed));

        // Gdy save(...) → zwracamy obiekt z ID=3
        Prescription toBeSaved = new Prescription();
        toBeSaved.setUser(exampleUser);
        toBeSaved.setMedication(exampleMed);
        toBeSaved.setDose(exampleDto.getDose());
        toBeSaved.setFrequency(exampleDto.getFrequency());
        toBeSaved.setStartDate(exampleDto.getStartDate());
        toBeSaved.setEndDate(exampleDto.getEndDate());

        Prescription saved = new Prescription();
        saved.setId(3L);
        saved.setUser(exampleUser);
        saved.setMedication(exampleMed);
        saved.setDose(exampleDto.getDose());
        saved.setFrequency(exampleDto.getFrequency());
        saved.setStartDate(exampleDto.getStartDate());
        saved.setEndDate(exampleDto.getEndDate());

        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(saved);

        // Act
        PrescriptionDto result = prescriptionService.create(exampleDto);

        // Assert – sprawdźmy mapping na wynikowym DTO
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(2L, result.getMedicationId());
        assertEquals("1 pill", result.getDose());
        assertEquals("Twice a day", result.getFrequency());
        assertEquals(exampleDto.getStartDate(), result.getStartDate());
        assertEquals(exampleDto.getEndDate(), result.getEndDate());

        // Upewnijmy się, że toBeSaved trafiło do save() z poprawnymi polami
        ArgumentCaptor<Prescription> capt = ArgumentCaptor.forClass(Prescription.class);
        verify(prescriptionRepository, times(1)).save(capt.capture());
        Prescription passed = capt.getValue();
        assertNull(passed.getId(), "Podczas tworzenia ID powinno być null");
        assertEquals(1L, passed.getUser().getId());
        assertEquals(2L, passed.getMedication().getId());
    }

    // 3) findById(...) – wyjątek i sukces

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(prescriptionRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> prescriptionService.findById(99L));
        assertTrue(ex.getMessage().contains("Recepta nie znaleziona: id=99"));
    }

    @Test
    void findById_ShouldReturnDto_WhenExists() {
        when(prescriptionRepository.findById(3L)).thenReturn(Optional.of(examplePresc));
        PrescriptionDto dto = prescriptionService.findById(3L);
        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        assertEquals(1L, dto.getUserId());
        assertEquals(2L, dto.getMedicationId());
        assertEquals("1 pill", dto.getDose());
    }

    // 4) update(...) – wyjątek i sukces

    @Test
    void update_ShouldThrow_WhenNotFound() {
        when(prescriptionRepository.findById(50L)).thenReturn(Optional.empty());

        PrescriptionDto dto = new PrescriptionDto();
        dto.setUserId(1L);
        dto.setMedicationId(2L);
        dto.setDose("Z");
        dto.setFrequency("X");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> prescriptionService.update(50L, dto));
        assertTrue(ex.getMessage().contains("Recepta nie znaleziona: id=50"));
    }

    @Test
    void update_ShouldModifyFieldsAndSave() {
        // findById(3) → zwróć examplePresc
        when(prescriptionRepository.findById(3L)).thenReturn(Optional.of(examplePresc));

        // Ta sama recepta (ID=3), zmieniamy tylko pola drugorzędne
        PrescriptionDto dto = new PrescriptionDto();
        dto.setUserId(1L);
        dto.setMedicationId(2L);
        dto.setDose("ChangedDose");
        dto.setFrequency("Once a day");
        dto.setStartDate(LocalDate.of(2025, 5, 5));
        dto.setEndDate(LocalDate.of(2025, 6, 6));

        // Po save(...) repository zwróci zaktualizowany obiekt
        Prescription updated = new Prescription();
        updated.setId(3L);
        updated.setUser(exampleUser);
        updated.setMedication(exampleMed);
        updated.setDose(dto.getDose());
        updated.setFrequency(dto.getFrequency());
        updated.setStartDate(dto.getStartDate());
        updated.setEndDate(dto.getEndDate());
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(updated);

        // Act
        PrescriptionDto result = prescriptionService.update(3L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("ChangedDose", result.getDose());
        assertEquals("Once a day", result.getFrequency());
        assertEquals(dto.getStartDate(), result.getStartDate());

        // Verify, że save() dostało poprawnie zmodyfikowany obiekt
        ArgumentCaptor<Prescription> capt = ArgumentCaptor.forClass(Prescription.class);
        verify(prescriptionRepository, times(1)).save(capt.capture());
        Prescription passed = capt.getValue();
        assertEquals(3L, passed.getId());
        assertEquals("ChangedDose", passed.getDose());
        assertEquals(LocalDate.of(2025, 5, 5), passed.getStartDate());
    }

    // 5) delete(...) – proste wywołanie repo.deleteById

    @Test
    void delete_ShouldInvokeRepositoryDeleteById() {
        prescriptionService.delete(7L);
        verify(prescriptionRepository, times(1)).deleteById(7L);
    }

    // 6) findByUserId(...) – wyjątek i sukces

    @Test
    void findByUserId_ShouldThrow_WhenUserNotExist() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> prescriptionService.findByUserId(10L));
        assertTrue(ex.getMessage().contains("Użytkownik nie znaleziony: id=10"));
    }

    @Test
    void findByUserId_ShouldReturnListOfDtos_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));
        // Zwróćmy dwie recepty powiązane z exampleUser
        Prescription presc1 = new Prescription();
        presc1.setId(5L);
        presc1.setUser(exampleUser);
        presc1.setMedication(exampleMed);
        presc1.setDose("D1");
        presc1.setFrequency("F1");
        presc1.setStartDate(LocalDate.now());
        presc1.setEndDate(LocalDate.now());

        Prescription presc2 = new Prescription();
        presc2.setId(6L);
        presc2.setUser(exampleUser);
        presc2.setMedication(exampleMed);
        presc2.setDose("D2");
        presc2.setFrequency("F2");
        presc2.setStartDate(LocalDate.now());
        presc2.setEndDate(LocalDate.now());

        when(prescriptionRepository.findAllByUser(exampleUser)).thenReturn(Arrays.asList(presc1, presc2));

        List<PrescriptionDto> list = prescriptionService.findByUserId(1L);
        assertEquals(2, list.size());
        Set<Long> ids = new HashSet<>();
        list.forEach(d -> ids.add(d.getId()));
        assertTrue(ids.contains(5L));
        assertTrue(ids.contains(6L));
    }

    // 7) findAll(...) – proste mapowanie wszystkiego

    @Test
    void findAll_ShouldReturnListOfDtos() {
        when(prescriptionRepository.findAll()).thenReturn(Collections.singletonList(examplePresc));
        List<PrescriptionDto> list = prescriptionService.findAll();
        assertEquals(1, list.size());
        PrescriptionDto dto = list.get(0);
        assertEquals(3L, dto.getId());
        assertEquals(1L, dto.getUserId());
    }
}
