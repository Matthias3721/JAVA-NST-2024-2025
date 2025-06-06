package org.example.medmanagement.service;

import org.example.medmanagement.model.Reminder;

public interface NotificationService {
    void sendNotification(Reminder reminder);
}
