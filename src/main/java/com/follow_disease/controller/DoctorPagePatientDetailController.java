package com.follow_disease.controller;

import com.follow_disease.Patient;
import com.follow_disease.VitalSign;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.List;

public class DoctorPagePatientDetailController {

    // FXML Bağlantıları
    @FXML private Label lblFullName, lblBloodType, lblActiveDisease, lblAppointmentDate;
    @FXML private TableView<VitalSign> tblVitalSigns;
    @FXML private TableColumn<VitalSign, String> colDate, colPressure;
    @FXML private TableColumn<VitalSign, Double> colSugar;
    @FXML private TableColumn<VitalSign, Integer> colPulse;
    @FXML private ListView<String> lstSymptoms;
    @FXML private TextArea txtAdditionalComplaints;
    @FXML private TextField txtMedicineName, txtPrescriptionCode, txtCustomCourse;
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

        // --- Semptomlar ve Notlar ---
        lstSymptoms.setItems(FXCollections.observableArrayList(patient.getSelectedSymptoms()));
        txtAdditionalComplaints.setText(patient.getAdditionalNote());

        // --- Dinamik ComboBox Yükleme ---
        loadCourseOptions(patient.getCurrent_disease());

        // Eğer daha önce bir gidişat kaydedilmişse onu ComboBox'ta göster
        if (patient.getAdditional_disease_course() != null && !patient.getAdditional_disease_course().isEmpty()) {
            cbDiseaseCourse.setValue(patient.getAdditional_disease_course());
        }
    }

    private void loadCourseOptions(String diseaseName) {
        // Burada diseases.json dosyasını tarayıp ilgili hastalığın
        // "disease_course" listesini çekip cbDiseaseCourse içine atmalısın.
        // Örn: cbDiseaseCourse.setItems(FXCollections.observableArrayList(jsonVerisi));
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