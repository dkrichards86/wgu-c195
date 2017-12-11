package SchedulingApp.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

import SchedulingApp.daos.CustomerDAOMySQL;
import SchedulingApp.daos.CustomerDAOInterface;
import SchedulingApp.models.*;
import SchedulingApp.SchedulingApp;

/**
 * Customer controller
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class CustomerController implements Initializable {

    // Main stage element
    private Stage stage;

    // GUI elements
    @FXML
    private TableView<Customer> CustomerTable;
    @FXML
    private TableColumn<Customer, String> CustomerNameCol;
    @FXML
    private TableColumn<Customer, String> CustomerAddress1Col;
    @FXML
    private TableColumn<Customer, String> CustomerAddress2Col;
    @FXML
    private TableColumn<Customer, String> CustomerPostalCodeCol;
    @FXML
    private TableColumn<Customer, String> CustomerPhoneCol;
    @FXML
    private TableColumn<Customer, String> CustomerCityCol;
    @FXML
    private TableColumn<Customer, String> CustomerCountryCol;

    // Modified customer, if applicable
    private static Customer modifiedCustomer;
    
    /**
     * Constructor
     */
    public CustomerController() {
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
     * Get the current modified customer.
     * 
     * @return 
     */
    public static Customer getModifiedCustomer() {
        return modifiedCustomer;
    }
    
    /**
     * Add a new customer.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleAddCustomer(ActionEvent event) throws IOException {        
        showManageScreen(event);
    }
    
    /**
     * Go back to main.
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
     * Delete the modified customer.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleDeleteCustomer(ActionEvent event) throws IOException {
        Customer customer = CustomerTable.getSelectionModel().getSelectedItem();
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete Customer");
        alert.setHeaderText("Delete Customer");
        alert.setContentText("Are you sure you want to delete " + customer.getCustomerName() + "?");
        alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> {
                CustomerDAOInterface customerDAO = new CustomerDAOMySQL();
                customerDAO.removeCustomer(customer);
                populateCustomerTable();
            });
    }
    
    /**
     * Edit a customer.
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleModifyCustomer(ActionEvent event) throws IOException {
        modifiedCustomer = CustomerTable.getSelectionModel().getSelectedItem();

        showManageScreen(event);
    }
    
    /**
     * Initialize
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CustomerNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        CustomerAddress1Col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress().getAddress1()));
        CustomerAddress2Col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress().getAddress2()));
        CustomerPostalCodeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress().getPostalCode()));
        CustomerPhoneCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress().getPhone()));
        CustomerCityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress().getCity().getCityName()));
        CustomerCountryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress().getCity().getCountry().getCountryName()));
    
        populateCustomerTable();
    }
    
    /**
     * Populate the table with customers.
     */
    public void populateCustomerTable() {
        CustomerDAOInterface customerDAO = new CustomerDAOMySQL();
        CustomerTable.setItems(customerDAO.getAllCustomers());
    }
    
    /**
     * Display the add/edit screen.
     * @param event
     * @throws IOException 
     */
    @FXML
    private void showManageScreen(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/ManageCustomer.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        ManageCustomerController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }
}
