package org.example.medmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presc_id", nullable = false)
    private Prescription prescription;

    @Column(nullable = false)
    private LocalDateTime remindTime;    // data i godzina przypomnienia

    @Column(nullable = false)
    private String channel;              // np. "EMAIL" lub "SMS"

    @Column(nullable = false)
    private boolean sent;                // czy już wysłano

    public Reminder() { }

    public Reminder(Long id,
                    Prescription prescription,
                    LocalDateTime remindTime,
                    String channel,
                    boolean sent) {
        this.id = id;
        this.prescription = prescription;
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

    // Getter i setter dla prescription
    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
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
