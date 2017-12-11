package SchedulingApp.controllers;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import SchedulingApp.SchedulingApp;
import SchedulingApp.daos.AppointmentDAOInterface;
import SchedulingApp.daos.AppointmentDAOMySQL;
import SchedulingApp.models.Appointment;

/**
 * Abstract Calendar Controller. This class serves as an abstract for weekly and
 * monthly calendars.
 * 
 * @See CalendarMonthlyViewController, CalendarWeeklyViewController
 * @author Dale Richards <dric123@wgu.edu>
 */
public abstract class AbstractCalendarController implements Initializable {
    
    // Main stage element
    public Stage stage;

    // Calendar header
    @FXML
    public Label CalendarLabel;

    // Calendar element
    @FXML
    public GridPane CalendarPane;
    
    // List of appointments to display
    public ObservableList<Appointment> calendarAppointments = FXCollections.observableArrayList();

    // Appointment DAO
    public final AppointmentDAOInterface appointmentDAO;

    // If applicable, appointment to modify
    public static Appointment modifiedAppointment;

    /**
     * Constructor
     */
    public AbstractCalendarController() {
        appointmentDAO = new AppointmentDAOMySQL();
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
     * Return a selected appointment
     * 
     * @return selected appointment
     */
    public static Appointment getModifiedAppointment() {
        return modifiedAppointment;
    }

    /**
     * Add an appointment
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleAddAppointment(ActionEvent event) throws IOException {  
        modifiedAppointment = null;
        showManageScreen(event);
    }

    /**
     * Go back to the main pane
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/Main.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        MainController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }

    /**
     * Delete the selected appointment
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleDeleteAppointment(ActionEvent event) throws IOException {
        if (modifiedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("No Appointment Selected");
            alert.setHeaderText("No Appointment Selected");
            alert.setContentText("Please selct an appointment");
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("Delete Customer");
            alert.setHeaderText("Delete Customer");
            alert.setContentText("Are you sure you want to delete " + modifiedAppointment.getTitle() + "?");
            alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    appointmentDAO.removeAppointment(modifiedAppointment);
                    setCalendarView();
                });
        }
    }

    /**
     * Edit the modified appointment
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleModifyAppointment(ActionEvent event) throws IOException {
        if (modifiedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("No Appointment Selected");
            alert.setHeaderText("No Appointment Selected");
            alert.setContentText("Please selct an appointment");
        }
        else {
            showManageScreen(event);
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
        setCalendarView();
    }

    /**
     * Reset the calendar pane
     */
    public void resetCalendar() {
        modifiedAppointment = null;
        CalendarPane.getChildren().clear();
    }

    /**
     * Show the calendar. This is meant to be overridden.
     */
    public void setCalendarView() {
    }

    /**
     * Show the add/edit screen.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    private void showManageScreen(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/ManageAppointment.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        ManageAppointmentController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }
}
