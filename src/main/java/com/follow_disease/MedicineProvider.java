package com.follow_disease;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class MedicineProvider {

    private static final String FILE_PATH = "database/medicines.json";

    public static Medicine getMedicineDetails(String medicineName) {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> medicineList = gson.fromJson(reader, listType);

            for (Map<String, Object> data : medicineList) {
                String name = (String) data.get("medicine_name");

                if (name != null && name.equalsIgnoreCase(medicineName)) {
                    String type = (String) data.get("medicine_type");
                    String dosage = (String) data.get("dosage");
                    List<String> extraEffects = (List<String>) data.get("additional_side_effects");

                    // Tipe göre doğru nesneyi oluşturuyoruz (Polymorphism)
                    Medicine medicine;
                    switch (type.toLowerCase()) {
                        case "antibiyotik":
                            medicine = new Antibiotic(type, dosage, name);
                            break;
                        case "ağrı kesici":
                        case "agri kesici":
                            medicine = new Painkiller(type, dosage, name);
                            break;
                        case "soğuk algınlığı":
                        case "soguk alginligi":

                            medicine = new ColdMedicine(type, dosage, name);
                            break;
                        default:
                            return null; // Tanımlı olmayan bir tip ise
                    }

                    // JSON'dan gelen ek yan etkileri nesneye ekliyoruz
                    if (extraEffects != null) {
                        for (String effect : extraEffects) {
                            medicine.addSideEffect(effect);
                        }
                    }
                    return medicine;
                }
            }
        } catch (Exception e) {
            System.err.println("İlaç verisi okunurken hata: " + e.getMessage());
        }
        return null;
    }
}
