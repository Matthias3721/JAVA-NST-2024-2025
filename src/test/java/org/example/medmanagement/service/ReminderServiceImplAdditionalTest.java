package org.example.medmanagement.service;

import org.example.medmanagement.dto.ReminderDto;
import org.example.medmanagement.model.Prescription;
import org.example.medmanagement.model.Reminder;
import org.example.medmanagement.repository.PrescriptionRepository;
import org.example.medmanagement.repository.ReminderRepository;
import org.example.medmanagement.service.impl.ReminderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ReminderServiceImplAdditionalTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReminderServiceImpl reminderService;

    private Reminder exampleReminder;
    private Prescription examplePrescription;

    @BeforeEach
    void setUp() {
        // Tworzymy „wzorcową” receptę, nadajemy tylko id
        examplePrescription = new Prescription();
        examplePrescription.setId(1L);

        // Tworzymy „wzorcowe” przypomnienie
        exampleReminder = new Reminder();
        exampleReminder.setId(100L);
        exampleReminder.setPrescription(examplePrescription);
        exampleReminder.setRemindTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        exampleReminder.setChannel("EMAIL");
        exampleReminder.setSent(false);
    }

    @Test
    void create_ShouldSaveWithSentFalseAndReturnDto() {
        // Przygotowujemy DTO wejściowe
        ReminderDto dtoIn = new ReminderDto();
        dtoIn.setId(null);
        dtoIn.setPrescriptionId(1L);
        dtoIn.setRemindTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        dtoIn.setChannel("EMAIL");
        dtoIn.setSent(true); // nawet jeśli użytkownik ustawił sent=true, metoda powinna wymusić false

        // Mockujemy prescriptionRepository.findById(1L)
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(examplePrescription));

        // Przygotowujemy zachowanie save(...) – zwracamy „zapisany” z id=100
        Reminder saved = new Reminder();
        saved.setId(100L);
        saved.setPrescription(examplePrescription);
        saved.setRemindTime(dtoIn.getRemindTime());
        saved.setChannel(dtoIn.getChannel());
        saved.setSent(false);

        when(reminderRepository.save(any(Reminder.class))).thenReturn(saved);

        // Act
        ReminderDto result = reminderService.create(dtoIn);

        // Assert: sprawdzamy, że wynikowy DTO ma id=100, prescriptionId=1, sent=false
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1L, result.getPrescriptionId());
        assertEquals(dtoIn.getRemindTime(), result.getRemindTime());
        assertFalse(result.isSent(), "Metoda create(...) powinna zawsze ustawić sent=false, niezależnie od dtoIn");

        // Dodatkowo: upewniamy się, że w repo trafił Reminder z sent=false
        ArgumentCaptor<Reminder> captor = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository, times(1)).save(captor.capture());
        Reminder passed = captor.getValue();
        assertFalse(passed.isSent());
        assertEquals(dtoIn.getChannel(), passed.getChannel());
    }

    @Test
    void findById_ShouldReturnDto_WhenExists() {
        // Arrange
        when(reminderRepository.findById(100L)).thenReturn(Optional.of(exampleReminder));

        // Act
        ReminderDto dto = reminderService.findById(100L);

        // Assert
        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals(1L, dto.getPrescriptionId());
        assertEquals("EMAIL", dto.getChannel());
        assertFalse(dto.isSent());
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        // Arrange
        when(reminderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reminderService.findById(999L));
        assertTrue(ex.getMessage().contains("Przypomnienie nie znalezione"));
    }

    @Test
    void update_ShouldThrow_WhenReminderNotExist() {
        // Arrange
        when(reminderRepository.findById(123L)).thenReturn(Optional.empty());
        ReminderDto dto = new ReminderDto();
        dto.setPrescriptionId(1L);
        dto.setChannel("SMS");
        dto.setRemindTime(LocalDateTime.now());
        dto.setSent(true);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reminderService.update(123L, dto));
        assertTrue(ex.getMessage().contains("Przypomnienie nie znalezione"));
    }

    @Test
    void update_ShouldChangePrescription_WhenIdsDifferent() {
        // Arrange: istniejące przypomnienie ma prescriptionId=1
        when(reminderRepository.findById(100L)).thenReturn(Optional.of(exampleReminder));

        // Tworzymy nową receptę o id=2 (reszta pól nie jest używana w serwisie)
        Prescription newPrescription = new Prescription();
        newPrescription.setId(2L);
        when(prescriptionRepository.findById(2L)).thenReturn(Optional.of(newPrescription));

        // DTO z prescriptionId=2
        ReminderDto dto = new ReminderDto();
        dto.setPrescriptionId(2L);
        dto.setRemindTime(LocalDateTime.of(2025, 2, 2, 10, 0));
        dto.setChannel("PUSH");
        dto.setSent(true);

        // Gdy save(...) → zwracamy zaktualizowany Reminder
        Reminder updated = new Reminder();
        updated.setId(100L);
        updated.setPrescription(newPrescription);
        updated.setRemindTime(dto.getRemindTime());
        updated.setChannel(dto.getChannel());
        updated.setSent(dto.isSent());
        when(reminderRepository.save(any(Reminder.class))).thenReturn(updated);

        // Act
        ReminderDto result = reminderService.update(100L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(2L, result.getPrescriptionId());
        assertEquals("PUSH", result.getChannel());
        assertTrue(result.isSent());

        // Sprawdzamy, że w zapisywanym Reminder zrobiło się prescriptionId=2
        ArgumentCaptor<Reminder> capt = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository, times(1)).save(capt.capture());
        Reminder passedRem = capt.getValue();
        assertEquals(2L, passedRem.getPrescription().getId());
        assertEquals(dto.getRemindTime(), passedRem.getRemindTime());
    }

    @Test
    void update_ShouldNotChangePrescription_WhenIdsEqual() {
        // Arrange: istniejące przypomnienie ma prescriptionId=1
        when(reminderRepository.findById(100L)).thenReturn(Optional.of(exampleReminder));

        // DTO również z prescriptionId=1
        ReminderDto dto = new ReminderDto();
        dto.setPrescriptionId(1L);
        dto.setRemindTime(LocalDateTime.of(2025, 3, 3, 15, 0));
        dto.setChannel("SMS");
        dto.setSent(false);

        // Przygotowujemy zachowanie save(...) → zwracamy zaktualizowane
        Reminder saved = new Reminder();
        saved.setId(100L);
        saved.setPrescription(examplePrescription);
        saved.setRemindTime(dto.getRemindTime());
        saved.setChannel(dto.getChannel());
        saved.setSent(dto.isSent());
        when(reminderRepository.save(any(Reminder.class))).thenReturn(saved);

        // Act
        ReminderDto result = reminderService.update(100L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1L, result.getPrescriptionId());
        assertEquals("SMS", result.getChannel());
        assertFalse(result.isSent());

        // Upewniamy się, że prescriptionRepository.findById(...) nie zostało wywołane (bo prescriptionId się nie zmienia)
        verify(prescriptionRepository, never()).findById(anyLong());
        verify(reminderRepository, times(1)).save(any(Reminder.class));
    }

    @Test
    void sendReminder_ShouldMarkSentAndSave_WhenExists() {
        // Arrange
        exampleReminder.setSent(false);
        when(reminderRepository.findById(100L)).thenReturn(Optional.of(exampleReminder));

        // Act
        reminderService.sendReminder(100L);

        // Assert: najpierw metoda powinna wywołać notificationService.sendNotification(...)
        verify(notificationService, times(1)).sendNotification(exampleReminder);

        // Potem zapis z sent=true
        ArgumentCaptor<Reminder> capt = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository, times(1)).save(capt.capture());
        Reminder passed = capt.getValue();
        assertTrue(passed.isSent());
    }

    @Test
    void sendReminder_ShouldThrow_WhenNotFound() {
        // Arrange
        when(reminderRepository.findById(500L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reminderService.sendReminder(500L));
        assertTrue(ex.getMessage().contains("Przypomnienie nie znalezione"));
    }

    @Test
    void getDueReminders_ShouldDelegateToRepository() {
        // Arrange
        LocalDateTime someTime = LocalDateTime.of(2025, 4, 4, 8, 0);
        Reminder r1 = new Reminder();
        r1.setId(1L);
        when(reminderRepository.findByRemindTimeBeforeAndSentFalse(someTime))
                .thenReturn(List.of(r1));

        // Act
        List<Reminder> result = reminderService.getDueReminders(someTime);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(reminderRepository, times(1)).findByRemindTimeBeforeAndSentFalse(someTime);
    }

    @Test
    void processPendingReminders_ShouldSendAndSaveAllDue() {
        // Arrange: przygotowujemy dwa przypomnienia do wysłania
        Reminder rA = new Reminder();
        rA.setId(10L);
        rA.setSent(false);
        Reminder rB = new Reminder();
        rB.setId(20L);
        rB.setSent(false);

        // Gdy repository zwróci listę [rA, rB]
        when(reminderRepository.findByRemindTimeBeforeAndSentFalse(any(LocalDateTime.class)))
                .thenReturn(List.of(rA, rB));

        // Act
        reminderService.processPendingReminders();

        // Assert: dla obu przypomnień wywołało się sendNotification() i save()
        verify(notificationService, times(1)).sendNotification(rA);
        verify(notificationService, times(1)).sendNotification(rB);

        // Weryfikujemy, że po wywołaniu metody sent=true było ustawione i przekazane do save()
        ArgumentCaptor<Reminder> captAll = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository, times(2)).save(captAll.capture());
        List<Reminder> savedList = captAll.getAllValues();
        assertTrue(savedList.get(0).isSent());
        assertTrue(savedList.get(1).isSent());
    }
}
