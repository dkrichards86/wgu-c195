package SchedulingApp.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;

import SchedulingApp.SchedulingApp;

public class ReportController implements Initializable {
    
    // Main stage element
    private Stage stage;
    
    public ReportController() {
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
     * Return to the main screen
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
     * Show the consultant schedule pane.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleConsultantSchedules(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/ReportConsultantSchedule.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        ReportConsultantScheduleController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }
    
    /**
     * Show the appointment type by month pane.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleMonthlyAppointments(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/ReportAppointmentTypes.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        ReportAppointmentTypesController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }
    
    /**
     * Show the appointment type by user pane.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleUserAppointments(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/ReportAppointmentConsultant.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        ReportAppointmentConsultantController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }
    
    /**
     * Initialize
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
