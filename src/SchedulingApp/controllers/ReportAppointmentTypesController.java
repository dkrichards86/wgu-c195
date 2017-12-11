package SchedulingApp.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

import SchedulingApp.SchedulingApp;
import SchedulingApp.daos.AppointmentDAOInterface;
import SchedulingApp.daos.AppointmentDAOMySQL;
import SchedulingApp.models.*;

/**
 * Appointment types by month Report page
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class ReportAppointmentTypesController implements Initializable {

    // Main stage element
    private Stage stage;

    // GUI elements
    @FXML
    private ComboBox<YearMonth> ReportMonthField;
    @FXML
    private TableView<ReportCountItem> ReportTable;
    @FXML
    private TableColumn<ReportCountItem, String> ReportTypeCol;
    @FXML
    private TableColumn<ReportCountItem, Integer> ReportCountCol;
    
    // List of months
    private final ObservableList<YearMonth> months = FXCollections.observableArrayList();
    
    // Chosen month.
    private YearMonth selectedMonth;
    
    // List of count objects
    private ReportCountList counts;
    
    //Formatter
    DateTimeFormatter yearMonthFormat = DateTimeFormatter.ofPattern("MMM", Locale.getDefault());
    
    /**
     * Constructor
     */
    public ReportAppointmentTypesController() {
        // populate a list of months
        YearMonth currYM = YearMonth.now();
        int currYear = currYM.getYear();
        
        for (int i = 1; i <= 12; i++) {
            months.add(YearMonth.of(currYear, i));
        }
    }

    /**
     * Bind the current controller to the main view
     * 
     * @param stage 
     */
    public void bind(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Go back to reports.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/Report.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        ReportController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }
    
    /**
     * Rerender the screen, following an element update.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleUpdate(ActionEvent event) throws IOException {
        YearMonth chosenMonth = ReportMonthField.getValue();
        
        if (chosenMonth != null) {
            selectedMonth = chosenMonth;
            populateTable();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("Select a Month");
            alert.setHeaderText("Select a Month");
            alert.setContentText("Please select a month to view.");
            alert.showAndWait()
                .filter(response -> response == ButtonType.OK);
        }
    }
    
    /**
     * Initialize
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedMonth = YearMonth.now();
        
        ReportMonthField.setItems(months);
        ReportMonthField.getSelectionModel().select(selectedMonth);
        
        ReportTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        ReportCountCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCount()).asObject());
        
        populateTable();
    }
    
    /**
     * Populate the table with report counts.
     */
    public void populateTable() {
        AppointmentDAOInterface appointmentDAO = new AppointmentDAOMySQL();
        
        if (selectedMonth != null) {
            LocalDate firstDate = selectedMonth.atDay(1);
            LocalDate lastDate = selectedMonth.atEndOfMonth();
            LocalDateTime startDatetime = LocalDateTime.of(firstDate, LocalTime.MIDNIGHT);
            LocalDateTime endDatetime = LocalDateTime.of(lastDate, LocalTime.MIDNIGHT);
            
            ObservableList<Appointment> appointments = appointmentDAO.getAppointmentsInRange(startDatetime, endDatetime);

            counts = new ReportCountList();
            
            for (Appointment appointment : appointments) {
                String type = appointment.getDescription();

                int index = counts.indexOf(type);
                if (index == -1) {
                    ReportCountItem item = new ReportCountItem(type);
                    counts.add(item);
                }
                else {
                    ReportCountItem item = counts.get(index);
                    item.increment();
                }
            }

            ReportTable.setItems(counts.list());
        }
    }
}