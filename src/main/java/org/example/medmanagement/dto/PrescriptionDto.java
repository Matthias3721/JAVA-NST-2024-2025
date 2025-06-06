package org.example.medmanagement.dto;

import java.time.LocalDate;

public class PrescriptionDto {
    private Long id;
    private Long userId;
    private Long medicationId;
    private String dose;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;

    public PrescriptionDto() { }

    public PrescriptionDto(Long id,
                           Long userId,
                           Long medicationId,
                           String dose,
                           String frequency,
                           LocalDate startDate,
                           LocalDate endDate) {
        this.id = id;
        this.userId = userId;
        this.medicationId = medicationId;
        this.dose = dose;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter i setter dla id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter i setter dla userId
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Getter i setter dla medicationId
    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    // Getter i setter dla dose
    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    // Getter i setter dla frequency
    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    // Getter i setter dla startDate
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // Getter i setter dla endDate
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
