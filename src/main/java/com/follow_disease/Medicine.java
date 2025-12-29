package com.follow_disease;

import java.util.List;
import java.util.ArrayList;

public abstract class Medicine {
    private String type;
    private String dosage;

    private List<String> additionalSideEffects = new ArrayList<>();
    // JSON'dan gelecek veya doktorun ekleyeceği ek yan etkiler için

    public Medicine(String type, String dosage) {
        this.type = type;
        this.dosage = dosage;
    }

    // Getter ve Setter metotlar
    public String getType() {
        return type; }
    public String getDosage() {
        return dosage; }
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    // Ortak metot: Kategorik yan etkileri ve ek yan etkileri birleştirmek için
    public List<String> getAllSideEffects() {
        List<String> allSideEffects = new ArrayList<>(getCategorySideEffects());
        allSideEffects.addAll(additionalSideEffects);
        return allSideEffects;
    }

    // Soyut metot: Her kategori kendi temel etkilerini dönecek
    protected abstract List<String> getCategorySideEffects();

    // Doktorun yeni yan etki eklemesi için metot
    public void addSideEffect(String effect) {
        if (!additionalSideEffects.contains(effect)) {
            additionalSideEffects.add(effect);
        }
    }

}
