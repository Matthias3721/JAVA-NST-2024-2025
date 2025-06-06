package org.example.medmanagement.service;

import org.example.medmanagement.dto.ReminderDto;
import org.example.medmanagement.model.Reminder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfejs serwisu przypomnień.
 */
public interface ReminderService {

    ReminderDto create(ReminderDto dto);

    ReminderDto update(Long id, ReminderDto dto);

    void delete(Long id);

    ReminderDto findById(Long id);

    List<ReminderDto> findAll();

    List<Reminder> getDueReminders(LocalDateTime beforeTime);

    void sendReminder(Long id);

    void processPendingReminders();

    /**
     * Zwraca wszystkie przypomnienia (DTO) dla danej recepty.
     */
    List<ReminderDto> findByPrescriptionId(Long prescriptionId);
}
