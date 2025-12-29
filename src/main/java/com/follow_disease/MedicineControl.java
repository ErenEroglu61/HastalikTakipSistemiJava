package com.follow_disease;

import java.util.List;

public class MedicineControl {

    private String medicine_name;
    private String medicine_type;
    private List<String> additional_side_effects;

    public MedicineControl() {}

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getMedicine_type() {
        return medicine_type;
    }

    public void setMedicine_type(String medicine_type) {
        this.medicine_type = medicine_type;
    }

    public List<String> getAdditional_side_effects() {
        return additional_side_effects;
    }

    public void setAdditional_side_effects(List<String> additional_side_effects) {
        this.additional_side_effects = additional_side_effects;
    }
}
