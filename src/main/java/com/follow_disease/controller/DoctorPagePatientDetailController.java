package com.follow_disease.controller;

import com.follow_disease.Patient;
import com.follow_disease.VitalSign;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.util.List;

public class DoctorPagePatientDetailController {

    // FXML Bağlantıları
    @FXML private Label lblFullName, lblBloodType, lblActiveDisease, lblAppointmentDate, lblCurrentMedicine, lblOtherMedicines;
    @FXML private TableView<VitalSign> tblVitalSigns;
    @FXML private TableColumn<VitalSign, String> colDate, colPressure;
    @FXML private TableColumn<VitalSign, Double> colSugar;
    @FXML private TableColumn<VitalSign, Integer> colPulse;
    @FXML private FlowPane flowSymptoms;
    @FXML private TextArea txtAdditionalComplaints;
    @FXML private TextField txtMedicineName, txtPrescriptionCode, txtCustomCourse, txtSideEffect;
    @FXML private ComboBox<String> cbDiseaseCourse;

    private Patient currentPatient;

    @FXML
    public void initialize() {
        // 1. Tablo Sütunlarını VitalSign sınıfındaki değişkenlerle bağlıyoruz
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colSugar.setCellValueFactory(new PropertyValueFactory<>("sugar"));
        colPressure.setCellValueFactory(new PropertyValueFactory<>("bloodPressure"));
        colPulse.setCellValueFactory(new PropertyValueFactory<>("pulse"));
    }

    // Bu metodu ana sayfadan çağıracağız
    public void initData(Patient patient) {
        this.currentPatient = patient;

        // Kimlik Bilgileri
        lblFullName.setText(patient.getName() + " " + patient.getSurname());
        lblBloodType.setText(patient.getBloodType().isEmpty() ? "Bilinmiyor" : patient.getBloodType());
        lblActiveDisease.setText(patient.getCurrent_disease());
        lblAppointmentDate.setText("Teşhis Tarihi: " + patient.getAppointmentDate());

        // --- Vital Bulgular Tablosu ---
        if (patient.getVitalSignsHistory() != null) {
            tblVitalSigns.setItems(FXCollections.observableArrayList(patient.getVitalSignsHistory()));
        }

        // 1. Aktif İlaç (current_medicine)
        // Listeyi virgülle ayrılmış bir String'e çeviriyoruz
        List<String> activeMedList = patient.getCurrent_medicine();

        if (activeMedList == null || activeMedList.isEmpty()) {
            lblCurrentMedicine.setText("Yok");
        } else {
            // Listenin tamamını virgülle ayırıp yazar (Örn: "Parol, Aspirin")
            String text = String.join(", ", activeMedList);
            lblCurrentMedicine.setText(text);
        }

        // 2. Diğer İlaçlar Listesi (medicines [])
        List<String> otherMeds = patient.getMedicines();
        if (otherMeds != null && !otherMeds.isEmpty()) {
            String medsText = String.join("\n• ", otherMeds); // Her ilacı yeni satıra ve madde işaretiyle ekler
            lblOtherMedicines.setText("• " + medsText);
        }

        // --- Semptomlar ve Notlar ---
        loadDiseaseSymptoms(patient.getCurrent_disease());
        txtAdditionalComplaints.setText(patient.getAdditionalNote());

        // --- Dinamik ComboBox Yükleme ---
        loadCourseOptions(patient.getCurrent_disease());

        // Eğer daha önce bir gidişat kaydedilmişse onu ComboBox'ta göster
        if (patient.getAdditional_disease_course() != null && !patient.getAdditional_disease_course().isEmpty()) {
            cbDiseaseCourse.setValue(patient.getAdditional_disease_course());
        }
    }

    private void loadCourseOptions(String diseaseName) {
        cbDiseaseCourse.getItems().clear();

        try {
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("database/diseases.json")));
            org.json.JSONArray diseasesArray = new org.json.JSONArray(content);

            for (int i = 0; i < diseasesArray.length(); i++) {
                org.json.JSONObject disease = diseasesArray.getJSONObject(i);

                if (disease.getString("disease_name").equalsIgnoreCase(diseaseName)) {
                    org.json.JSONArray courseArray = disease.getJSONArray("disease_course");

                    for (int j = 0; j < courseArray.length(); j++) {
                        // Seçenekleri ComboBox'a ekle
                        cbDiseaseCourse.getItems().add(courseArray.getString(j));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("ComboBox Yükleme Hatası: " + e.getMessage());
            cbDiseaseCourse.setPromptText("Seçenekler yüklenemedi");
        }
    }

    private void loadDiseaseSymptoms(String diseaseName) {
        flowSymptoms.getChildren().clear();

        try {
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("database/diseases.json")));
            org.json.JSONArray diseasesArray = new org.json.JSONArray(content);

            for (int i = 0; i < diseasesArray.length(); i++) {
                org.json.JSONObject disease = diseasesArray.getJSONObject(i);

                // Hastalık adını bulduğumuzda semptomları çekiyoruz
                if (disease.getString("disease_name").equalsIgnoreCase(diseaseName)) {
                    org.json.JSONArray symptomsArray = disease.getJSONArray("disease_symptoms");

                    for (int j = 0; j < symptomsArray.length(); j++) {
                        String symptom = symptomsArray.getString(j);

                        Label item = new Label("• " + symptom);
                        item.setStyle("-fx-text-fill: #333333; -fx-font-size: 12px;");
                        item.setMinWidth(90);
                        item.setPrefWidth(90);
                        item.setWrapText(true);

                        flowSymptoms.getChildren().add(item);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("JSON Okuma Hatası: " + e.getMessage());
            flowSymptoms.getChildren().add(new Label("Semptomlar yüklenemedi."));
        }
    }


    @FXML
    private void handleSave() {
        // 1. Doktorun girdiği verileri al
        String medicineName = txtMedicineName.getText();
        String medicineCode = txtPrescriptionCode.getText(); // Kod ayrı alınıyor

        // Custom gidişat alanı doluysa onu kullan, değilse ComboBox'ı kullan
        String finalCourse = (txtCustomCourse.getText() != null && !txtCustomCourse.getText().isEmpty())
                ? txtCustomCourse.getText()
                : cbDiseaseCourse.getValue();

        // 2. Patient nesnesini güncelle (Bellekteki veriyi güncelliyoruz)
        if (medicineName != null && !medicineName.isEmpty()) {
            currentPatient.getMedicines().add(medicineName); // Sadece adı listeye ekle
        }

        if (medicineCode != null && !medicineCode.isEmpty()) {
            currentPatient.setPrescriptionCode(medicineCode); // Kodu ayrı alana set et
        }

        if (finalCourse != null) {
            currentPatient.setAdditional_disease_course(finalCourse);
        }

        System.out.println("Kayıt başarılı: " + currentPatient.getName() + " için veriler güncellendi.");

        // Pencereyi kapat (Kaydetme işlemini ana sayfadaki metodun tetiklemesi daha sağlıklıdır)
        handleClose();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) lblFullName.getScene().getWindow();
        stage.close();
    }
}