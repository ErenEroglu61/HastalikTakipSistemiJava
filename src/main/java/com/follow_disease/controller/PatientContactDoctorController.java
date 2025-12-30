package com.follow_disease.controller;

import com.follow_disease.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PatientContactDoctorController implements Feedback,Notification {

    @FXML private TextField txtMedicineName, txtPrescriptionCode, txtCustomCourse, txtSideEffect;
    @FXML private Label lblDoctorName, lblDoctorTitle, lblActiveDisease, lblDiagnosisDate;
    @FXML private TableView<VitalSign> tblVitalSigns;
    @FXML private TableColumn<VitalSign, String> colDate, colBloodPressure;
    @FXML private TableColumn<VitalSign, Double> colSugar, colFever;
    @FXML private TableColumn<VitalSign, Integer> colPulse;
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

    @Override
    public String getFeedbackNote() {
        return txtPatientNote.getText();}
    @Override
    public void setFeedbackNote(String note) {
        txtPatientNote.setText(safe(note));}
    @Override
    public List<String> getNotifications() {return null;}
    @Override
    public void setNotifications(List<String> notifications) {}
    @Override
    public void updateNotificationUI() {}

    private void fillUIFields() {
        Doctor assignedDoctor = findDoctorById(this.assignedDoctorId);

        if (assignedDoctor != null) {
            lblDoctorName.setText(safe(assignedDoctor.getName() + " " + assignedDoctor.getSurname()));
            lblDoctorTitle.setText(assignedDoctor.getRoleDescription());
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
        setFeedbackNote(currentPatient.getAdditionalPatientNote());
    }

    private void setupVitalSignsTable() {
        tblVitalSigns.setEditable(true);

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colSugar.setCellValueFactory(new PropertyValueFactory<>("sugar"));
        colBloodPressure.setCellValueFactory(new PropertyValueFactory<>("bloodPressure"));
        colPulse.setCellValueFactory(new PropertyValueFactory<>("pulse"));
        colFever.setCellValueFactory(new PropertyValueFactory<>("fever"));


        colDate.setCellFactory(tc -> createAutoCommitCell(new DefaultStringConverter()));
        colSugar.setCellFactory(tc -> createAutoCommitCell(new DoubleStringConverter()));
        colBloodPressure.setCellFactory(tc -> createAutoCommitCell(new DefaultStringConverter()));
        colPulse.setCellFactory(tc -> createAutoCommitCell(new IntegerStringConverter()));
        colFever.setCellFactory(tc -> createAutoCommitCell(new DoubleStringConverter()));

        colDate.setOnEditCommit(e -> e.getRowValue().setDate(e.getNewValue()));
        colSugar.setOnEditCommit(e -> e.getRowValue().setSugar(e.getNewValue()));
        colBloodPressure.setOnEditCommit(e -> e.getRowValue().setBloodPressure(e.getNewValue()));
        colPulse.setOnEditCommit(e -> e.getRowValue().setPulse(e.getNewValue()));
        colFever.setOnEditCommit(e -> e.getRowValue().setFever(e.getNewValue()));

        vitalSignsData.clear();

        if (currentPatient.getVitalSignsHistory() != null) {
            vitalSignsData.addAll(currentPatient.getVitalSignsHistory());
        }

        while (vitalSignsData.size() < 10) {
            vitalSignsData.add(new VitalSign(null, null, null, null, null));
        }
        tblVitalSigns.setItems(vitalSignsData);
    }

    private <T> TableCell<VitalSign, T> createAutoCommitCell(StringConverter<T> converter) {
        return new TableCell<>() {
            private final TextField textField = new TextField();
            {
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused && isEditing()) {
                        try {
                            commitEdit(converter.fromString(textField.getText()));
                        } catch (Exception e) {
                            cancelEdit();
                        }
                    }
                });
                textField.setOnAction(e -> commitEdit(converter.fromString(textField.getText())));
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if (isEditing()) {
                        textField.setText(item != null ? item.toString() : "");
                        setGraphic(textField);
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText(item != null ? item.toString() : null);
                    }
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                textField.setText(getItem() != null ? getItem().toString() : "");
                setGraphic(textField);
                setText(null);
                textField.requestFocus();
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setGraphic(null);
                setText(getItem() != null ? getItem().toString() : null);
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
                        // Eğer hasta daha önce bunu seçtiyse işaretli getir
                        if (currentPatient.getSelectedSymptoms() != null &&
                                currentPatient.getSelectedSymptoms().contains(s)) {
                            cb.setSelected(true);
                        }
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
            Patient found = list.stream().filter(p -> p.getTc().equals(tc)).findFirst().orElse(null);
            if (found != null) this.assignedDoctorId = found.getDoctor_id();
            return found;
        } catch (Exception e) { return null; }
    }

    private Doctor findDoctorById(int doctorId) {
        try (FileReader reader = new FileReader("database/doctors.json")) {
            Type listType = new TypeToken<List<Doctor>>(){}.getType();
            List<Doctor> doctors = new Gson().fromJson(reader, listType);
            return doctors.stream().filter(d -> d.getId() == doctorId).findFirst().orElse(null);
        } catch (Exception e) { return null; }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            // Semptomları topla
            List<String> selectedSymptoms = new ArrayList<>();
            for (Node node : flowSymptoms.getChildren()) {
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    selectedSymptoms.add(cb.getText());
                }
            }

            // Tablodaki verileri filtrele (Sadece en az bir alanı dolu olanları al)
            List<VitalSign> filledSigns = vitalSignsData.stream()
                    .filter(vs -> vs.getDate() != null || vs.getSugar() != null ||
                            vs.getBloodPressure() != null || vs.getPulse() != null ||
                            vs.getFever() != null)
                    .toList();

            // Modeli güncelle
            currentPatient.setVitalSignsHistory(new ArrayList<>(filledSigns));
            currentPatient.setSelectedSymptoms(selectedSymptoms);
            currentPatient.setAdditionalPatientNote(getFeedbackNote());

            // JSON'a Yaz
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            List<Patient> allPatients;
            try (FileReader reader = new FileReader("database/patients.json")) {
                allPatients = gson.fromJson(reader, new TypeToken<List<Patient>>(){}.getType());
            }

            for (int i = 0; i < allPatients.size(); i++) {
                if (allPatients.get(i).getTc().equals(currentPatient.getTc())) {
                    allPatients.set(i, currentPatient);
                    break;
                }
            }

            try (FileWriter writer = new FileWriter("database/patients.json")) {
                gson.toJson(allPatients, writer);
            }

            Doctor assignedDoctor = findDoctorById(this.assignedDoctorId);
            if (assignedDoctor != null) {
                User me = Session.getCurrentUser();
                String message = "Hastanız " + me.getName() + " " + me.getSurname() + " gelen güncellemeleriniz var. ";
                sendNotification(assignedDoctor.getTc(), message, true);
            }

            new Alert(Alert.AlertType.INFORMATION, "Yapmış olduğunuz güncellemeler doktorunuza iletildi.").showAndWait();
            handleClose(event);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Hata oluştu: " + e.getMessage()).show();
        }
    }
    //İptal et butonu
    @FXML
    private void handleClose(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private String safe(String s) { return s == null ? "" : s; }
}