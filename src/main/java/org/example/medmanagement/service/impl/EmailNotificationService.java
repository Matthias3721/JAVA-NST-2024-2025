package org.example.medmanagement.service.impl;

import org.example.medmanagement.model.Reminder;
import org.example.medmanagement.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Implementacja wysyłki powiadomień e-mailowych.
 */
@Service
@Primary
@Slf4j
public class EmailNotificationService implements NotificationService {

    /**
     * Wysyła powiadomienie e-mail dla danego przypomnienia.
     */
    @Override
    public void sendNotification(Reminder reminder) {
        log.info("Wysyłam e-mailowe powiadomienie dla Reminder ID = {}", reminder.getId());
    }
}
