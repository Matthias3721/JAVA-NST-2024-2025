package org.example.medmanagement.dto;

import java.time.LocalDateTime;

public class ReminderDto {
    private Long id;
    private Long prescriptionId;
    private LocalDateTime remindTime;
    private String channel;
    private boolean sent;

    public ReminderDto() { }

    public ReminderDto(Long id,
                       Long prescriptionId,
                       LocalDateTime remindTime,
                       String channel,
                       boolean sent) {
        this.id = id;
        this.prescriptionId = prescriptionId;
        this.remindTime = remindTime;
        this.channel = channel;
        this.sent = sent;
    }

    // Getter i setter dla id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter i setter dla prescriptionId
    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    // Getter i setter dla remindTime
    public LocalDateTime getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(LocalDateTime remindTime) {
        this.remindTime = remindTime;
    }

    // Getter i setter dla channel
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    // Getter i setter dla sent
    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
