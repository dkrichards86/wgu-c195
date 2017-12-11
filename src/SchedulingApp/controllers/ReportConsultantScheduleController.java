package SchedulingApp.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
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
import SchedulingApp.daos.UserDAOInterface;
import SchedulingApp.daos.UserDAOMySQL;
import SchedulingApp.models.*;

/**
 * Consultant Schedule Report page
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class ReportConsultantScheduleController implements Initializable {

    // Main stage element
    private Stage stage;
    
    // List of Users
    private final ObservableList<User> users;

    // GUI elements
    @FXML
    private ComboBox<User> ScheduleUserField;
    @FXML
    private TableView<Appointment> ScheduleTable;
    @FXML
    private TableColumn<Appointment, String> ScheduleCustomerCol;
    @FXML
    private TableColumn<Appointment, String> ScheduleTitleCol;
    @FXML
    private TableColumn<Appointment, String> ScheduleDateCol;
    @FXML
    private TableColumn<Appointment, String> ScheduleStartCol;
    @FXML
    private TableColumn<Appointment, String> ScheduleEndCol;
    
    // Selected user
    private static User selectedUser;
    
    // Formatters
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    /**
     * Constructor
     */
    public ReportConsultantScheduleController() {
        UserDAOInterface userDAO = new UserDAOMySQL();
        users = userDAO.getAllUsers();
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
        User chosenUser = ScheduleUserField.getValue();
        
        if (chosenUser != null) {
            selectedUser = chosenUser;
            populateTable();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("Select a User");
            alert.setHeaderText("Select a User");
            alert.setContentText("Please select a user to view.");
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
        ScheduleUserField.setItems(users);
        
        ScheduleCustomerCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer().getCustomerName()));
        ScheduleTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        ScheduleDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(dateFormat)));
        ScheduleStartCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(timeFormat)));
        ScheduleEndCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(timeFormat)));
    }
    
    /**
     * Populate the table with consultant appointments.
     */
    public void populateTable() {
        AppointmentDAOInterface appointmentDAO = new AppointmentDAOMySQL();
        
        if (selectedUser != null) {
            ObservableList<Appointment> appointments = appointmentDAO.getAppointmentsByUser(selectedUser);
            appointments.sort((a, b) -> a.getStart().toLocalDate().compareTo(b.getStart().toLocalDate()));
            
            ScheduleTable.setItems(appointments);
        }
    }
}