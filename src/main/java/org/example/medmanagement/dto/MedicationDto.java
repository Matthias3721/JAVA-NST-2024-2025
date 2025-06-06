package org.example.medmanagement.dto;

public class MedicationDto {
    private Long id;
    private String name;
    private String substance;
    private String unit;
    private String manufacturer;

    public MedicationDto() { }

    public MedicationDto(Long id, String name, String substance, String unit, String manufacturer) {
        this.id = id;
        this.name = name;
        this.substance = substance;
        this.unit = unit;
        this.manufacturer = manufacturer;
    }

    // Getter i setter dla id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter i setter dla name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter i setter dla substance
    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

    // Getter i setter dla unit
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // Getter i setter dla manufacturer
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
