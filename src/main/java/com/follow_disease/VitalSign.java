package com.follow_disease;

public class VitalSign {
    private String date;
    private Double sugar;
    private String bloodPressure;
    private Integer pulse;
    private Double fever;

    public VitalSign() {}

    public VitalSign(String date, Double sugar, String bloodPressure, Integer pulse, Double fever) {
        this.date = date;
        this.sugar = sugar;
        this.bloodPressure = bloodPressure;
        this.pulse = pulse;
        this.fever = fever;
    }

    // Getters ve Setters
    public String getDate() { return date; }
    public Double getSugar() { return sugar; }
    public String getBloodPressure() { return bloodPressure; }
    public Integer getPulse() { return pulse; }
    public Double getFever() { return fever; }
    public void setDate(String date) { this.date = date; }
    public void setSugar(double sugar) { this.sugar = sugar; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }
    public void setPulse(int pulse) { this.pulse = pulse; }
    public void setFever(double fever) { this.fever = fever; }
}