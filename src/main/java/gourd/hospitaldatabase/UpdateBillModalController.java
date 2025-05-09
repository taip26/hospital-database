package gourd.hospitaldatabase;

    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.collections.transformation.FilteredList;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.scene.Node;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.stage.Stage;

    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;
    import java.util.List;
    import java.util.regex.Pattern;

    public class UpdateBillModalController {
        @FXML
        private ComboBox<String> adminDropdown;

        @FXML
        public ComboBox<String> statusDropdown;

        @FXML
        private TextField dateField;

        @FXML
        private TextField reasonField;

        @FXML
        private TextField moneySumField;

        @FXML
        private TextField insuranceDeductibleField;

        @FXML
        private TextField patientInfoField;

        @FXML
        private TextField doctorInfoField;

        @FXML
        private TextField searchField;

        @FXML
        private TableView<MedicalBillModel> billsTableView;

        @FXML
        private TableColumn<MedicalBillModel, Integer> billIdColumn;

        @FXML
        private TableColumn<MedicalBillModel, String> billDateColumn;

        @FXML
        private TableColumn<MedicalBillModel, String> billReasonColumn;

        @FXML
        private TableColumn<MedicalBillModel, String> billStatusColumn;

        private static final Pattern DATE_PATTERN = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");

        private MedicalBillModel currentBill;
        private ObservableList<MedicalBillModel> billsList;
        private FilteredList<MedicalBillModel> filteredBills;

        @FXML
        private void initialize() {
            statusDropdown.getItems().addAll("Pending", "Paid", "Void");
            List<String> admins = SQL_Manager.getAllAdminsString();
            adminDropdown.getItems().addAll(admins);

            setupBillsTable();
            setupSearchField();
        }

        private void setupBillsTable() {
            billIdColumn.setCellValueFactory(new PropertyValueFactory<>("billID"));
            billDateColumn.setCellValueFactory(new PropertyValueFactory<>("billDate"));
            billReasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
            billStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

            // Load all bills
            List<MedicalBillModel> allBills = SQL_Manager.getAllMedicalBills();
            billsList = FXCollections.observableArrayList(allBills);
            filteredBills = new FilteredList<>(billsList, p -> true);
            billsTableView.setItems(filteredBills);

            // Set up selection listener
            billsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    loadBillData(newSelection);
                }
            });
        }

        private void setupSearchField() {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredBills.setPredicate(bill -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (String.valueOf(bill.getBillID()).contains(lowerCaseFilter)) {
                        return true;
                    } else if (bill.getReason().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (bill.getPaymentStatus().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (bill.getBillDate().toString().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
        }

        private void loadBillData(MedicalBillModel bill) {
            currentBill = bill;

            // Format the date from YYYY-MM-DD to MM/DD/YYYY
            LocalDate billDate = currentBill.getBillDate();
            dateField.setText(billDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

            reasonField.setText(currentBill.getReason());
            moneySumField.setText(String.valueOf(currentBill.getPaymentAmount()));
            insuranceDeductibleField.setText(String.valueOf(currentBill.getInsuranceDeductible()));
            statusDropdown.setValue(currentBill.getPaymentStatus());

            // Set admin dropdown
            for (String admin : adminDropdown.getItems()) {
                if (admin.startsWith(currentBill.getAdminID() + " ")) {
                    adminDropdown.setValue(admin);
                    break;
                }
            }

            // Display patient and doctor information
            String patientName = SQL_Manager.getPatientNameById(currentBill.getPatientID());
            patientInfoField.setText(currentBill.getPatientID() + " - " + patientName);

            String doctorName = SQL_Manager.getStaffNameById(currentBill.getStaffID());
            doctorInfoField.setText(currentBill.getStaffID() + " - " + doctorName);
        }

        @FXML
        private void handleUpdate() {
            if (currentBill == null) {
                showAlert("No Selection", "Please select a bill to update.", Alert.AlertType.WARNING);
                return;
            }

            String selectedAdmin = adminDropdown.getValue();
            if (selectedAdmin == null || selectedAdmin.isEmpty()) {
                showAlert("Invalid Admin", "Please select an admin.", Alert.AlertType.WARNING);
                return;
            }

            // Extract admin ID from the selected admin string
            int adminID = Integer.parseInt(selectedAdmin.split(" ")[0]);
            String billDate = dateField.getText();
            String reason = reasonField.getText();
            String paymentAmount = moneySumField.getText();
            String insuranceDeductible = insuranceDeductibleField.getText();
            String paymentStatus = statusDropdown.getValue();

            if (!DATE_PATTERN.matcher(billDate).matches()) {
                showAlert("Invalid Date", "Please enter a valid date in MM/DD/YYYY format.", Alert.AlertType.WARNING);
                return;
            }

            if (reason == null || reason.trim().isEmpty()) {
                showAlert("Invalid Reason", "Please enter a reason.", Alert.AlertType.WARNING);
                return;
            }

            double paymentAmountValue;
            double insuranceDeductibleValue;
            try {
                paymentAmountValue = Double.parseDouble(paymentAmount);
            } catch (NumberFormatException e) {
                showAlert("Invalid Payment Amount", "Please enter a valid number for Payment Amount.", Alert.AlertType.WARNING);
                return;
            }

            try {
                insuranceDeductibleValue = Double.parseDouble(insuranceDeductible);
            } catch (NumberFormatException e) {
                showAlert("Invalid Insurance Deductible", "Please enter a valid number for Insurance Deductible.", Alert.AlertType.WARNING);
                return;
            }

            String formattedDate;
            try {
                LocalDate parsedDate = LocalDate.parse(billDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                formattedDate = parsedDate.toString(); // Converts to YYYY-MM-DD
            } catch (Exception e) {
                showAlert("Invalid Date", "Failed to parse date. Ensure it is in MM/DD/YYYY format.", Alert.AlertType.WARNING);
                return;
            }

            // Update the medical bill in the database
            try {
                String result = SQL_Manager.updateMedicalBill(
                        currentBill.getBillID(),
                        formattedDate,
                        reason,
                        paymentAmountValue,
                        insuranceDeductibleValue,
                        paymentStatus,
                        adminID
                );
                showAlert("Success", result, Alert.AlertType.INFORMATION);
                handleCancel(null); // Close the modal after successful update
            } catch (Exception e) {
                showAlert("Error", "Failed to update medical bill: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }

        @FXML
        private void handleDelete() {
            if (currentBill == null) {
                showAlert("No Selection", "Please select a bill to delete.", Alert.AlertType.WARNING);
                return;
            }

            // Delete the medical bill from the database
            try {
                String result = SQL_Manager.deleteMedicalBill(currentBill.getBillID());
                showAlert("Success", result, Alert.AlertType.INFORMATION);
                handleCancel(null); // Close the modal after successful deletion
            } catch (Exception e) {
                showAlert("Error", "Failed to delete medical bill: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }

        private void showAlert(String title, String message, Alert.AlertType alertType) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        public void handleCancel(ActionEvent actionEvent) {
            Stage stage;
            if (actionEvent != null) {
                stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) dateField.getScene().getWindow(); // Fallback to the current window
            }
            stage.close();
        }
    }