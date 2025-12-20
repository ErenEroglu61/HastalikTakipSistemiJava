package com.follow_disease;

import java.util.ArrayList;
import java.util.List;

public class Antibiotic extends Medicine{
     private String name;

     public Antibiotic(String type, String dosage, String name) {
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
        side_effects.add("Midede yanma ve ağrı hali");
        side_effects.add("Alerjik reaksiyon");

        return side_effects;
    }

}
