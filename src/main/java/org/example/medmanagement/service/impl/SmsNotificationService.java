package org.example.medmanagement.service.impl;

import org.example.medmanagement.model.Reminder;
import org.example.medmanagement.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementacja wysyłki powiadomień SMS.
 */
@Service
@Slf4j
public class SmsNotificationService implements NotificationService {

    /**
     * Wysyła powiadomienie SMS dla danego przypomnienia.
     */
    @Override
    public void sendNotification(Reminder reminder) {
        log.info("Wysyłam SMS-owe powiadomienie dla Reminder ID = {}", reminder.getId());
    }
}
