package com.follow_disease;

import java.util.ArrayList;
import java.util.List;

public class Painkiller extends Medicine{
    private String name;

    public Painkiller(String type, String dosage, String name){
        super(type,dosage);
        this.name = name;
    }
    //Getter ve Setter
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    @Override
    protected List<String> getCategorySideEffects() {
        List<String> side_effects = new ArrayList<>();
        side_effects.add("Mide bulantısı");
        side_effects.add("Uyku hali");
        side_effects.add("Baş dönmesi");

        return side_effects;
    }
}
