package SchedulingApp.controllers;

import SchedulingApp.SchedulingApp;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Main Controller. This is the main screen for an authenticated user. It 
 * displays links to other screens.
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class MainController implements Initializable {
    
    // Main stage element
    private Stage stage;
    
    /**
     * Constructor
     */
    public MainController() {
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
     * Exit the app.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Confirm Exit");
        alert.setContentText("Are you sure you want to exit?");
        alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> System.exit(0));
    }
    
    /**
     * Show the manage appointments pane.
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleManageAppointments(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/CalendarMonthlyView.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        CalendarMonthlyViewController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }

    /**
     * Show the manage customers pane.
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleManageCustomers(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/Customer.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        CustomerController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }
    
    /**
     * Show the reports pane.
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleReports(ActionEvent event) throws IOException {
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
     * Initialize
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
