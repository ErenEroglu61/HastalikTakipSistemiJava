package com.follow_disease;

public class VitalSign {
    private String date;
    private double sugar;
    private String bloodPressure;
    private int pulse;

    public VitalSign() {}

    public VitalSign(String date, double sugar, String bloodPressure, int pulse) {
        this.date = date;
        this.sugar = sugar;
        this.bloodPressure = bloodPressure;
        this.pulse = pulse;
    }

    // Getters ve Setters
    public String getDate() { return date; }
    public double getSugar() { return sugar; }
    public String getBloodPressure() { return bloodPressure; }
    public int getPulse() { return pulse; }
    public void setDate(String date) { this.date = date; }
    public void setSugar(double sugar) { this.sugar = sugar; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }
    public void setPulse(int pulse) { this.pulse = pulse; }
}