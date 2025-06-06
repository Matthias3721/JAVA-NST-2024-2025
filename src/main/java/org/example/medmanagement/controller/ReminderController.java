package org.example.medmanagement.controller;

import org.example.medmanagement.dto.ReminderDto;
import org.example.medmanagement.model.Reminder;
import org.example.medmanagement.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Kontroler REST dla przypomnień.
 */
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    public ResponseEntity<ReminderDto> create(@RequestBody ReminderDto dto) {
        ReminderDto created = reminderService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderDto> getById(@PathVariable Long id) {
        ReminderDto dto = reminderService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderDto> update(@PathVariable Long id, @RequestBody ReminderDto dto) {
        ReminderDto updated = reminderService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reminderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReminderDto>> getAll() {
        List<ReminderDto> list = reminderService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/due")
    public ResponseEntity<List<Reminder>> getDue(@RequestParam("before") String beforeTime) {
        LocalDateTime time = LocalDateTime.parse(beforeTime);
        List<Reminder> dueList = reminderService.getDueReminders(time);
        return ResponseEntity.ok(dueList);
    }

    @PostMapping("/send/{id}")
    public ResponseEntity<Void> send(@PathVariable Long id) {
        reminderService.sendReminder(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint pobierający przypomnienia powiązane z konkretną receptą.
     */
    @GetMapping("/by-prescription/{prescriptionId}")
    public ResponseEntity<List<ReminderDto>> getByPrescription(@PathVariable Long prescriptionId) {
        List<ReminderDto> list = reminderService.findByPrescriptionId(prescriptionId);
        return ResponseEntity.ok(list);
    }
}
