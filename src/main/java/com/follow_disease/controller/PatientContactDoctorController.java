package com.follow_disease.controller;

import com.follow_disease.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PatientContactDoctorController {

    @FXML private TextField txtMedicineName, txtPrescriptionCode, txtCustomCourse, txtSideEffect;

    @FXML private Label lblDoctorName, lblDoctorTitle;
    @FXML private TableView<VitalSign> tblVitalSigns;
    @FXML private TableColumn<VitalSign, String> colDate, colBloodPressure;
    @FXML private TableColumn<VitalSign, Double> colSugar, colFever;
    @FXML private TableColumn<VitalSign, Integer> colPulse;
    @FXML private Label lblActiveDisease, lblDiagnosisDate;
    @FXML private FlowPane flowSymptoms;
    @FXML private TextArea txtPatientNote;

    private Patient currentPatient;
    private int assignedDoctorId;
    private final ObservableList<VitalSign> vitalSignsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        User u = Session.getCurrentUser();
        if (u != null) {
            this.currentPatient = findPatientByTc(u.getTc());
            if (currentPatient != null) {
                fillUIFields();
                loadSymptomsFromJSON();
                setupVitalSignsTable();
            }
        }
    }

    private void fillUIFields() {

        Doctor assignedDoctor = findDoctorById(Integer.valueOf(this.assignedDoctorId));

        if (assignedDoctor != null) {
            lblDoctorName.setText(safe(assignedDoctor.getName() + " " + assignedDoctor.getSurname()));
            lblDoctorTitle.setText(assignedDoctor.getRoleDescription()); // Senin metodun :)
        } else {
            lblDoctorName.setText("Atanmış Doktor Yok");
            lblDoctorTitle.setText("-");
        }
        lblActiveDisease.setText(safe(currentPatient.getCurrent_disease()).toUpperCase());
        lblDiagnosisDate.setText("Teşhis: " + safe(currentPatient.getAppointmentDate()));

        if (currentPatient.getAdditional_medicines() != null && !currentPatient.getAdditional_medicines().isEmpty()) {
            txtMedicineName.setText(String.join(", ", currentPatient.getAdditional_medicines()));
        } else {
            txtMedicineName.setText("Yeni ilaç önerilmedi");
        }

        if (currentPatient.getPrescriptions() != null && !currentPatient.getPrescriptions().isEmpty()) {
            txtPrescriptionCode.setText(String.join(", ", currentPatient.getPrescriptions()));
        } else {
            txtPrescriptionCode.setText("-");
        }
        txtCustomCourse.setText(safe(currentPatient.getAdditional_disease_course()));
        txtSideEffect.setText(safe(currentPatient.getAdditionalDoctorNote()));
    }

    private Doctor findDoctorById(int doctorId) {
        try (FileReader reader = new FileReader("database/doctors.json")) {
            Type listType = new TypeToken<List<Doctor>>(){}.getType();
            List<Doctor> doctors = new Gson().fromJson(reader, listType);
            return doctors.stream().filter(d -> d.getId() == doctorId).findFirst().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void setupVitalSignsTable() {
        tblVitalSigns.setEditable(true);

        colDate.setPrefWidth(100);
        colSugar.setPrefWidth(60);
        colBloodPressure.setPrefWidth(80);
        colPulse.setPrefWidth(60);
        colFever.setPrefWidth(60);

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colSugar.setCellValueFactory(new PropertyValueFactory<>("sugar"));
        colBloodPressure.setCellValueFactory(new PropertyValueFactory<>("bloodPressure"));
        colPulse.setCellValueFactory(new PropertyValueFactory<>("pulse"));
        colFever.setCellValueFactory(new PropertyValueFactory<>("fever"));

        colDate.setCellFactory(tc -> createRestrictedStringCell("[0-9/.-]*"));
        colBloodPressure.setCellFactory(tc -> createRestrictedStringCell("[0-9/]*"));

        colSugar.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colPulse.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colFever.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        vitalSignsData.clear();
        for (int i = 0; i < 10; i++) {
            vitalSignsData.add(new VitalSign("", 0.0, "", 0, 0.0));
        }
        tblVitalSigns.setItems(vitalSignsData);
    }

    private TableCell<VitalSign, String> createRestrictedStringCell(String regex) {
        return new TableCell<>() {
            private final TextField textField = new TextField();
            {
                textField.textProperty().addListener((obs, old, newVal) -> {
                    if (!newVal.matches(regex)) textField.setText(old);
                });
                textField.focusedProperty().addListener((obs, old, isFocused) -> {
                    if (!isFocused) commitEdit(textField.getText());
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    textField.setText(item != null ? item : "");
                    setGraphic(textField);
                }
            }
        };
    }

    private void loadSymptomsFromJSON() {
        String diseaseName = currentPatient.getCurrent_disease();
        if (diseaseName == null) return;

        try (FileReader reader = new FileReader("database/diseases.json")) {
            Type listType = new TypeToken<List<Disease>>(){}.getType();
            List<Disease> diseases = new Gson().fromJson(reader, listType);

            for (Disease d : diseases) {
                if (d.getDisease_name().equalsIgnoreCase(diseaseName)) {
                    flowSymptoms.getChildren().clear();
                    for (String s : d.getDisease_symptoms()) {
                        CheckBox cb = new CheckBox(s);
                        cb.setStyle("-fx-text-fill: #1565C0; -fx-font-weight: bold;");
                        flowSymptoms.getChildren().add(cb);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Patient findPatientByTc(String tc) {

        try (FileReader reader = new FileReader("database/patients.json")) {
            Type listType = new TypeToken<List<Patient>>(){}.getType();
            List<Patient> list = new Gson().fromJson(reader, listType);

            Patient found = list.stream()
                    .filter(p -> p.getTc().equals(tc))
                    .findFirst()
                    .orElse(null);

            if (found != null) {
                this.assignedDoctorId = found.getDoctor_id();
            }

            return found;
        } catch (Exception e) {
            System.err.println("Hasta okunurken hata: " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            List<String> selectedSymptoms = new ArrayList<>();
            for (Node node : flowSymptoms.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox cb = (CheckBox) node;
                    if (cb.isSelected()) {
                        selectedSymptoms.add(cb.getText());
                    }
                }
            }
            List<VitalSign> updatedVitalSigns = new ArrayList<>(vitalSignsData);

            currentPatient.setSelectedSymptoms(selectedSymptoms);
            currentPatient.setVitalSignsHistory(updatedVitalSigns);
            currentPatient.setAdditionalPatientNote(txtPatientNote.getText());

            List<Patient> allPatients;
            Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();

            try (FileReader reader = new FileReader("database/patients.json")) {
                Type listType = new TypeToken<List<Patient>>(){}.getType();
                allPatients = gson.fromJson(reader, listType);
            }

            for (int i = 0; i < allPatients.size(); i++) {
                if (allPatients.get(i).getTc().equals(currentPatient.getTc())) {
                    allPatients.set(i, currentPatient);
                    break;
                }
            }
            try (java.io.FileWriter writer = new java.io.FileWriter("database/patients.json")) {
                gson.toJson(allPatients, writer);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Başarılı");
            alert.setHeaderText(null);
            alert.setContentText("Bilgileriniz başarıyla güncellendi ve doktora iletildi.");
            alert.showAndWait();

            handleClose(event);

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setContentText("Veriler kaydedilirken bir sorun oluştu: " + e.getMessage());
            alert.show();
        }
    }
    private String safe(String s) { return s == null ? "" : s; }
}