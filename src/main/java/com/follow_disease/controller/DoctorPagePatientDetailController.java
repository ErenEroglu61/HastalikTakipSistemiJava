package com.follow_disease.controller;

import com.follow_disease.Feedback;
import com.follow_disease.Patient;
import com.follow_disease.VitalSign;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.follow_disease.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.util.List;

public class DoctorPagePatientDetailController implements Feedback,Notification {

  // FXML Bağlantıları
  @FXML private Label lblFullName, lblBloodType, lblActiveDisease, lblAppointmentDate, lblCurrentMedicine, lblOtherMedicines;
  @FXML private TableView<VitalSign> tblVitalSigns;
  @FXML private TableColumn<VitalSign, String> colDate, colBloodPressure;
  @FXML private TableColumn<VitalSign, Double> colSugar, colFever;
  @FXML private TableColumn<VitalSign, Integer> colPulse;

  @FXML private FlowPane flowSymptoms;
  @FXML private TextArea txtAdditionalComplaints;
  @FXML private TextField txtMedicineName, txtPrescriptionCode, txtCustomCourse, txtSideEffect;
  @FXML private ComboBox<String> cbDiseaseCourse;

  private Patient currentPatient;


  @FXML
  public void initialize() {
    // Tablo Sütunlarını VitalSign sınıfındaki değişkenlerle bağlıyoruz
    colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    colSugar.setCellValueFactory(new PropertyValueFactory<>("sugar"));
    colBloodPressure.setCellValueFactory(new PropertyValueFactory<>("bloodPressure"));
    colPulse.setCellValueFactory(new PropertyValueFactory<>("pulse"));
    colFever.setCellValueFactory(new PropertyValueFactory<>("fever"));
  }

  public String getFeedbackNote() {
      return txtSideEffect.getText();}
  public void setFeedbackNote(String note){
      txtSideEffect.setText(safe(note));}
  @Override
  public List<String> getNotifications() {return null;}
  @Override
  public void setNotifications(List<String> notifications) {}
  @Override
  public void updateNotificationUI() {}

  public void initData(Patient patient) {
    this.currentPatient = patient;

    // Kimlik Bilgileri
    lblFullName.setText(patient.getName() + " " + patient.getSurname());
    lblBloodType.setText(patient.getBloodType().isEmpty() ? "Bilinmiyor" : patient.getBloodType());
    lblActiveDisease.setText(patient.getCurrent_disease());
    lblAppointmentDate.setText("Teşhis Tarihi: " + patient.getAppointmentDate());

    // Vital bulgular tablosu
    if (patient.getVitalSignsHistory() != null && !patient.getVitalSignsHistory().isEmpty()) {
      tblVitalSigns.setItems(FXCollections.observableArrayList(patient.getVitalSignsHistory()));
    } else {
      tblVitalSigns.setItems(FXCollections.observableArrayList());
      tblVitalSigns.setPlaceholder(new Label("Hastaya ait ölçüm kaydı bulunmamaktadır."));
    }

    // Aktif İlaç
    List<String> activeMedList = patient.getCurrent_medicine();

    if (activeMedList == null || activeMedList.isEmpty()) {
      lblCurrentMedicine.setText("Yok");
    } else {
      String text = String.join(", ", activeMedList);
      lblCurrentMedicine.setText(text);
    }

    // Diğer İlaçlar Listesi
    List<String> otherMeds = patient.getMedicines();
    if (otherMeds != null && !otherMeds.isEmpty()) {
      String medsText = String.join("\n• ", otherMeds);
      lblOtherMedicines.setText("• " + medsText);
    }

    // Semptomlar ve Notlar
    displaySelectedSymptoms(patient.getSelectedSymptoms());
    setFeedbackNote(patient.getAdditionalDoctorNote());
    txtAdditionalComplaints.setText(safe(patient.getAdditionalPatientNote()));

    // Doktorun yazdığı ilaç
    if (patient.getAdditional_medicines() != null && !patient.getAdditional_medicines().isEmpty()) {
      String sonIlac = patient.getAdditional_medicines().get(patient.getAdditional_medicines().size() - 1);
      txtMedicineName.setText(sonIlac);
    }

    // Yazılan reçete kodu
    if (patient.getPrescriptions() != null && !patient.getPrescriptions().isEmpty()) {
      String sonRecete = patient.getPrescriptions().get(patient.getPrescriptions().size() - 1);
      txtPrescriptionCode.setText(sonRecete);
    }

    // Dinamik ComboBox Yükleme
    loadCourseOptions(patient.getCurrent_disease());

    // Eğer daha önce bir gidişat kaydedilmişse onu ComboBox'ta göster
    if (patient.getAdditional_disease_course() != null && !patient.getAdditional_disease_course().isEmpty()) {
      cbDiseaseCourse.setValue(patient.getAdditional_disease_course());
    }
    txtSideEffect.setText(patient.getAdditionalDoctorNote());
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

  private void displaySelectedSymptoms(List<String> selectedSymptoms) {
    flowSymptoms.getChildren().clear();

    if (selectedSymptoms == null || selectedSymptoms.isEmpty()) {
      Label emptyLabel = new Label("Hasta herhangi bir semptom seçmedi.");
      emptyLabel.setStyle("-fx-text-fill: #757575; -fx-font-style: italic;");
      flowSymptoms.getChildren().add(emptyLabel);
      return;
    }

    for (String symptom : selectedSymptoms) {
      Label item = new Label("• " + symptom);
      item.setStyle("-fx-text-fill: #333333; -fx-font-size: 11px;");
      item.setMinWidth(90);
      item.setPrefWidth(90);
      item.setWrapText(true);
      flowSymptoms.getChildren().add(item);
    }
  }

  @FXML
  private void handleSave() {
    String newMedicine = txtMedicineName.getText();
    String newPrescription = txtPrescriptionCode.getText();
    String sideEffects = txtSideEffect.getText();
    String feedback = txtCustomCourse.getText();
    String courseFromCombo = cbDiseaseCourse.getValue();

    // Öncelik doktorun yazdığında
    String finalCourse = (feedback != null && !feedback.isEmpty()) ? feedback : courseFromCombo;
    currentPatient.setAdditional_disease_course(finalCourse);

    currentPatient.setAdditionalDoctorNote(getFeedbackNote());
    processMedicineAndPrescription(newMedicine, newPrescription);
    savePatientDataToJson(currentPatient);

    if (currentPatient != null) {
          // targetTc: Hastanın TC'si
          // message: İsteğine göre özelleştirilmiş mesaj
          // isTargetDoctor: false (Çünkü bildirim hastaya gidiyor)
          sendNotification(currentPatient.getTc(), "Doktorunuzdan gelen bir güncellemeniz var.", false);

      }

    handleClose();
  }

  private void processMedicineAndPrescription(String med, String pres) {
        if (med != null && !med.isEmpty()) {
            List<String> meds = currentPatient.getAdditional_medicines();
            if (meds.isEmpty() || !meds.get(meds.size() - 1).equalsIgnoreCase(med.trim())) {
                meds.add(med.trim());
            }
        }
        if (pres != null && !pres.isEmpty()) {
            List<String> prescriptions = currentPatient.getPrescriptions();
            if (prescriptions.isEmpty() || !prescriptions.get(prescriptions.size() - 1).equalsIgnoreCase(pres.trim())) {
                prescriptions.add(pres.trim());
            }
        }
  }

  private void savePatientDataToJson(Patient patient) {
    try {
      String pathString = "database/patients.json";
      java.nio.file.Path filePath = java.nio.file.Paths.get(pathString);
      String content = new String(java.nio.file.Files.readAllBytes(filePath));
      org.json.JSONArray patientsArray = new org.json.JSONArray(content);

      for (int i = 0; i < patientsArray.length(); i++) {
        org.json.JSONObject pJson = patientsArray.getJSONObject(i);

        if (pJson.getString("tc").equals(patient.getTc())) {
          pJson.put("additional_disease_course", patient.getAdditional_disease_course());
          pJson.put("additionalPatientNote", patient.getAdditionalPatientNote());
          pJson.put("additional_medicines", patient.getAdditional_medicines());
          pJson.put("prescriptions", patient.getPrescriptions());
          pJson.put("additionalDoctorNote", patient.getAdditionalDoctorNote());
          break;
        }
      }
      java.nio.file.Files.write(filePath, patientsArray.toString(4).getBytes());

      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Başarılı");
      alert.setHeaderText(null);
      alert.setContentText("Yaptığınız işlemler hastanıza iletildi.");
      alert.showAndWait();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleClose() {
    Stage stage = (Stage) lblFullName.getScene().getWindow();
    stage.close();
  }

  private String safe(String s) { return s == null ? "" : s; }
}