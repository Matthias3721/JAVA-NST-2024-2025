package org.example.medmanagement.service;

import org.example.medmanagement.dto.ReminderDto;
import org.example.medmanagement.model.Prescription;
import org.example.medmanagement.model.Reminder;
import org.example.medmanagement.repository.PrescriptionRepository;
import org.example.medmanagement.repository.ReminderRepository;
import org.example.medmanagement.service.impl.ReminderServiceImpl;
import org.example.medmanagement.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testy jednostkowe dla ReminderServiceImpl.
 */
public class ReminderServiceImplTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReminderServiceImpl reminderService;

    private Prescription prescription;
    private Reminder reminder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Przygotowanie przykładowej encji Prescription
        prescription = new Prescription();
        prescription.setId(1L);

        // Przygotowanie przykładowej encji Reminder
        reminder = new Reminder();
        reminder.setId(100L);
        reminder.setPrescription(prescription);
        reminder.setRemindTime(LocalDateTime.of(2025, 6, 5, 9, 0));
        reminder.setChannel("EMAIL");
        reminder.setSent(false);
    }

    @Test
    void create_ShouldReturnDto_WhenSuccess() {
        ReminderDto dtoIn = new ReminderDto();
        dtoIn.setPrescriptionId(1L);
        dtoIn.setRemindTime(LocalDateTime.of(2025, 6, 5, 9, 0));
        dtoIn.setChannel("EMAIL");

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(reminder);

        ReminderDto out = reminderService.create(dtoIn);

        assertNotNull(out);
        assertEquals(100L, out.getId());
        assertEquals(1L, out.getPrescriptionId());
        assertEquals(LocalDateTime.of(2025, 6, 5, 9, 0), out.getRemindTime());
        assertEquals("EMAIL", out.getChannel());
        assertFalse(out.isSent());

        verify(prescriptionRepository, times(1)).findById(1L);
        verify(reminderRepository, times(1)).save(any(Reminder.class));
    }

    @Test
    void create_ShouldThrow_WhenPrescriptionNotFound() {
        when(prescriptionRepository.findById(2L)).thenReturn(Optional.empty());

        ReminderDto dtoIn = new ReminderDto();
        dtoIn.setPrescriptionId(2L);
        dtoIn.setRemindTime(LocalDateTime.of(2025, 6, 5, 9, 0));
        dtoIn.setChannel("EMAIL");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            reminderService.create(dtoIn);
        });

        assertTrue(ex.getMessage().contains("Recepta nie istnieje: id=2"));
        verify(prescriptionRepository, times(1)).findById(2L);
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void findById_ShouldReturnDto_WhenFound() {
        when(reminderRepository.findById(100L)).thenReturn(Optional.of(reminder));

        ReminderDto dto = reminderService.findById(100L);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals(1L, dto.getPrescriptionId());
        assertEquals("EMAIL", dto.getChannel());
        assertFalse(dto.isSent());

        verify(reminderRepository, times(1)).findById(100L);
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(reminderRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            reminderService.findById(999L);
        });

        assertTrue(ex.getMessage().contains("Przypomnienie nie znalezione: id=999"));
        verify(reminderRepository, times(1)).findById(999L);
    }

    @Test
    void update_ShouldReturnUpdatedDto_WhenExists() {
        when(reminderRepository.findById(100L)).thenReturn(Optional.of(reminder));

        Reminder updatedEntity = new Reminder();
        updatedEntity.setId(100L);
        updatedEntity.setPrescription(prescription);
        updatedEntity.setRemindTime(LocalDateTime.of(2025, 6, 6, 10, 0));
        updatedEntity.setChannel("SMS");
        updatedEntity.setSent(true);

        when(reminderRepository.save(any(Reminder.class))).thenReturn(updatedEntity);

        ReminderDto toUpdate = new ReminderDto();
        toUpdate.setPrescriptionId(1L);
        toUpdate.setRemindTime(LocalDateTime.of(2025, 6, 6, 10, 0));
        toUpdate.setChannel("SMS");
        toUpdate.setSent(true);

        ReminderDto result = reminderService.update(100L, toUpdate);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("SMS", result.getChannel());
        assertTrue(result.isSent());
        assertEquals(LocalDateTime.of(2025, 6, 6, 10, 0), result.getRemindTime());

        verify(reminderRepository, times(1)).findById(100L);
        verify(reminderRepository, times(1)).save(any(Reminder.class));
    }

    @Test
    void update_ShouldThrow_WhenReminderNotFound() {
        when(reminderRepository.findById(200L)).thenReturn(Optional.empty());

        ReminderDto toUpdate = new ReminderDto();
        toUpdate.setPrescriptionId(1L);
        toUpdate.setRemindTime(LocalDateTime.of(2025, 6, 6, 10, 0));
        toUpdate.setChannel("SMS");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            reminderService.update(200L, toUpdate);
        });

        assertTrue(ex.getMessage().contains("Przypomnienie nie znalezione: id=200"));
        verify(reminderRepository, times(1)).findById(200L);
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void delete_ShouldInvokeRepositoryDelete() {
        doNothing().when(reminderRepository).deleteById(100L);

        reminderService.delete(100L);

        verify(reminderRepository, times(1)).deleteById(100L);
    }

    @Test
    void getDueReminders_ShouldReturnList_WhenDue() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 5, 9, 30);

        when(reminderRepository.findByRemindTimeBeforeAndSentFalse(now))
                .thenReturn(List.of(reminder));

        List<Reminder> due = reminderService.getDueReminders(now);

        assertNotNull(due);
        assertEquals(1, due.size());
        assertEquals(100L, due.get(0).getId());

        verify(reminderRepository, times(1)).findByRemindTimeBeforeAndSentFalse(now);
    }

    @Test
    void sendReminder_ShouldMarkSentAndNotify_WhenExists() {
        when(reminderRepository.findById(100L)).thenReturn(Optional.of(reminder));
        doNothing().when(notificationService).sendNotification(reminder);
        when(reminderRepository.save(any(Reminder.class))).thenReturn(reminder);

        reminderService.sendReminder(100L);

        assertTrue(reminder.isSent());

        verify(notificationService, times(1)).sendNotification(reminder);
        verify(reminderRepository, times(1)).findById(100L);
        verify(reminderRepository, times(1)).save(reminder);
    }

    @Test
    void sendReminder_ShouldThrow_WhenNotFound() {
        when(reminderRepository.findById(300L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            reminderService.sendReminder(300L);
        });

        assertTrue(ex.getMessage().contains("Przypomnienie nie znalezione: id=300"));
        verify(reminderRepository, times(1)).findById(300L);
        verify(notificationService, never()).sendNotification(any(Reminder.class));
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void findAll_ShouldReturnListOfDtos() {
        when(reminderRepository.findAll()).thenReturn(List.of(reminder));

        List<ReminderDto> list = reminderService.findAll();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(100L, list.get(0).getId());
        assertEquals(1L, list.get(0).getPrescriptionId());

        verify(reminderRepository, times(1)).findAll();
    }

    @Test
    void processPendingReminders_ShouldSendAndMarkAllDue() {
        when(reminderRepository.findByRemindTimeBeforeAndSentFalse(any(LocalDateTime.class)))
                .thenReturn(List.of(reminder));
        doNothing().when(notificationService).sendNotification(reminder);
        when(reminderRepository.save(any(Reminder.class))).thenReturn(reminder);

        reminderService.processPendingReminders();

        assertTrue(reminder.isSent());

        verify(reminderRepository, times(1)).findByRemindTimeBeforeAndSentFalse(any(LocalDateTime.class));
        verify(notificationService, times(1)).sendNotification(reminder);
        verify(reminderRepository, times(1)).save(reminder);
    }

    @Test
    void findByPrescriptionId_ShouldReturnDtos_WhenExist() {
        when(reminderRepository.findByPrescriptionId(1L)).thenReturn(List.of(reminder));

        List<ReminderDto> list = reminderService.findByPrescriptionId(1L);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(100L, list.get(0).getId());
        assertEquals(1L, list.get(0).getPrescriptionId());

        verify(reminderRepository, times(1)).findByPrescriptionId(1L);
    }
}
