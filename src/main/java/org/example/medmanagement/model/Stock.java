package org.example.medmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Powiązanie do leku
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "med_id", nullable = false)
    private Medication medication;

    @Column(nullable = false)
    private Integer quantity;     // dostępna ilość w magazynie

    @Column(nullable = false)
    private Integer threshold;    // próg alarmowy

    public Stock() { }

    public Stock(Long id, Medication medication, Integer quantity, Integer threshold) {
        this.id = id;
        this.medication = medication;
        this.quantity = quantity;
        this.threshold = threshold;
    }

    // Getter i setter dla id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter i setter dla medication
    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    // Getter i setter dla quantity
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // Getter i setter dla threshold
    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}
