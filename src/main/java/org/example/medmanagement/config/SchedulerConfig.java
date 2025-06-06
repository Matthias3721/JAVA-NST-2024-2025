package org.example.medmanagement.config;

import org.example.medmanagement.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerConfig {

    private final ReminderService reminderService;

    // Wywołuj co minutę (60000 ms) proces wysyłania przypomnień
    @Scheduled(fixedRateString = "60000")
    public void runReminderProcessor() {
        reminderService.processPendingReminders();
    }
}
