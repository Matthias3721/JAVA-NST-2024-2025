package org.example.medmanagement.service.impl;

import org.example.medmanagement.dto.ReminderDto;
import org.example.medmanagement.model.Prescription;
import org.example.medmanagement.model.Reminder;
import org.example.medmanagement.repository.PrescriptionRepository;
import org.example.medmanagement.repository.ReminderRepository;
import org.example.medmanagement.service.NotificationService;
import org.example.medmanagement.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementacja serwisu do zarządzania przypomnieniami.
 */
@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final NotificationService notificationService;

    private ReminderDto mapToDto(Reminder rem) {
        ReminderDto dto = new ReminderDto();
        dto.setId(rem.getId());
        dto.setPrescriptionId(rem.getPrescription().getId());
        dto.setRemindTime(rem.getRemindTime());
        dto.setChannel(rem.getChannel());
        dto.setSent(rem.isSent());
        return dto;
    }

    private Reminder mapToEntity(ReminderDto dto) {
        Prescription prescription = prescriptionRepository.findById(dto.getPrescriptionId())
                .orElseThrow(() -> new RuntimeException("Recepta nie istnieje: id=" + dto.getPrescriptionId()));

        Reminder rem = new Reminder();
        rem.setId(dto.getId());
        rem.setPrescription(prescription);
        rem.setRemindTime(dto.getRemindTime());
        rem.setChannel(dto.getChannel());
        rem.setSent(dto.isSent());
        return rem;
    }

    @Override
    public ReminderDto create(ReminderDto dto) {
        Reminder remToSave = mapToEntity(dto);
        remToSave.setSent(false);
        Reminder saved = reminderRepository.save(remToSave);
        return mapToDto(saved);
    }

    @Override
    public ReminderDto update(Long id, ReminderDto dto) {
        Reminder existing = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Przypomnienie nie znalezione: id=" + id));

        if (!existing.getPrescription().getId().equals(dto.getPrescriptionId())) {
            Prescription newPrescription = prescriptionRepository.findById(dto.getPrescriptionId())
                    .orElseThrow(() -> new RuntimeException("Recepta nie istnieje: id=" + dto.getPrescriptionId()));
            existing.setPrescription(newPrescription);
        }

        existing.setRemindTime(dto.getRemindTime());
        existing.setChannel(dto.getChannel());
        existing.setSent(dto.isSent());

        Reminder updated = reminderRepository.save(existing);
        return mapToDto(updated);
    }

    @Override
    public void delete(Long id) {
        reminderRepository.deleteById(id);
    }

    @Override
    public ReminderDto findById(Long id) {
        Reminder rem = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Przypomnienie nie znalezione: id=" + id));
        return mapToDto(rem);
    }

    @Override
    public List<ReminderDto> findAll() {
        return reminderRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reminder> getDueReminders(LocalDateTime beforeTime) {
        return reminderRepository.findByRemindTimeBeforeAndSentFalse(beforeTime);
    }

    @Override
    public void sendReminder(Long id) {
        Reminder rem = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Przypomnienie nie znalezione: id=" + id));

        notificationService.sendNotification(rem);

        rem.setSent(true);
        reminderRepository.save(rem);
    }

    @Override
    public void processPendingReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> dueList = getDueReminders(now);
        for (Reminder rem : dueList) {
            notificationService.sendNotification(rem);
            rem.setSent(true);
            reminderRepository.save(rem);
        }
    }

    @Scheduled(fixedRate = 60_000)
    public void scheduleProcessPending() {
        processPendingReminders();
    }

    @Override
    public List<ReminderDto> findByPrescriptionId(Long prescriptionId) {
        List<Reminder> reminders = reminderRepository.findByPrescriptionId(prescriptionId);
        return reminders.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
