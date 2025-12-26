package com.follow_disease;

import java.util.List;

public class Disease {

    private String disease_name;
    private List<String> disease_symptoms;
    private List<String> disease_course;

    public Disease() {}

    public String getDisease_name() {
        return disease_name;}
    public void setDisease_name(String disease_name) {
        this.disease_name = disease_name;
    }
    public List<String> getDisease_symptoms() {
        return disease_symptoms;}
    public void setDisease_symptoms(List<String> disease_symptoms) {
        this.disease_symptoms = disease_symptoms;
    }
    public List<String> getDisease_course() {
        return disease_course;}
    public void setDisease_course(List<String> disease_course) {
        this.disease_course = disease_course;}
}