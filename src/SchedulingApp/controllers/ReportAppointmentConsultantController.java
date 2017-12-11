package SchedulingApp.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
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
 * Appointment types by Consultant Report page
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class ReportAppointmentConsultantController implements Initializable {

    // Main stage element
    private Stage stage;

    // GUI Elements
    @FXML
    private ComboBox<User> ReportUserField;
    @FXML
    private TableView<ReportCountItem> ReportTable;
    @FXML
    private TableColumn<ReportCountItem, String> ReportTypeCol;
    @FXML
    private TableColumn<ReportCountItem, Integer> ReportCountCol;
    
    // List of users
    private final ObservableList<User> users;
    
    // Chosen user
    private static User selectedUser;
    
    // List of count objects
    private ReportCountList counts;
    
    /**
     * Constructor
     */
    public ReportAppointmentConsultantController() {
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
        User chosenUser = ReportUserField.getValue();
        
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
        ReportUserField.setItems(users);
        
        ReportTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        ReportCountCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCount()).asObject());
    }
    
    /**
     * Populate the table with report counts.
     */
    public void populateTable() {
        AppointmentDAOInterface appointmentDAO = new AppointmentDAOMySQL();
        
        if (selectedUser != null) {
            ObservableList<Appointment> appointments = appointmentDAO.getAppointmentsByUser(selectedUser);
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