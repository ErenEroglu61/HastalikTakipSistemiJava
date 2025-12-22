package com.follow_disease;

public class VitalSign {
    private String date;
    private double sugar;
    private String bloodPressure;
    private int pulse;
    private double fever;

    public VitalSign() {}

    public VitalSign(String date, double sugar, String bloodPressure, int pulse, double fever) {
        this.date = date;
        this.sugar = sugar;
        this.bloodPressure = bloodPressure;
        this.pulse = pulse;
        this.fever = fever;
    }

    // Getters ve Setters
    public String getDate() { return date; }
    public double getSugar() { return sugar; }
    public String getBloodPressure() { return bloodPressure; }
    public int getPulse() { return pulse; }
    public double getFever() { return fever; }
    public void setDate(String date) { this.date = date; }
    public void setSugar(double sugar) { this.sugar = sugar; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }
    public void setPulse(int pulse) { this.pulse = pulse; }
    public void setFever(double fever) { this.fever = fever; }
}