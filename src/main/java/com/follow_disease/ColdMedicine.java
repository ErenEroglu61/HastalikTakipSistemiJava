package com.follow_disease;

import java.util.ArrayList;
import java.util.List;

public class ColdMedicine extends Medicine{
    private String name;

    public ColdMedicine(String type, String dosage, String name) {
        super(type, dosage);
        this.name = name;
    }

    //Getter ve Setter
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<String> getCategorySideEffects() {
        List<String> side_effects = new ArrayList<>();
        side_effects.add("Yüksek ateş");
        side_effects.add("Uyku hali ve uyuşukluk");
        side_effects.add("Aşırı miktarda terleme");
        side_effects.add("Ağız kuruluğu");
        side_effects.add("Bulanık Görme");

        return side_effects;
    }
}
