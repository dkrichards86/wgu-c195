package SchedulingApp.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

import SchedulingApp.SchedulingApp;
import static SchedulingApp.controllers.CustomerController.getModifiedCustomer;
import SchedulingApp.daos.AddressDAOMySQL;
import SchedulingApp.daos.CountryDAOMySQL;
import SchedulingApp.daos.CustomerDAOMySQL;
import SchedulingApp.daos.CountryDAOInterface;
import SchedulingApp.daos.CityDAOMySQL;
import SchedulingApp.daos.CityDAOInterface;
import SchedulingApp.daos.AddressDAOInterface;
import SchedulingApp.daos.CustomerDAOInterface;
import SchedulingApp.exceptions.InvalidAddressException;
import SchedulingApp.models.*;

/**
 * Manage a customer
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class ManageCustomerController implements Initializable {

    // Main stage element
    private Stage stage;

    // GUI Elements
    @FXML
    private TextField CustomerNameField;
    @FXML
    private TextField CustomerAddress1Field;
    @FXML
    private TextField CustomerAddress2Field;
    @FXML
    private TextField CustomerAddressPhoneField;
    @FXML
    private TextField CustomerAddressPostalCodeField;
    @FXML
    private TextField CustomerAddressCityField;
    @FXML
    private TextField CustomerAddressCountryField;

    // Selected customer
    private final Customer modifiedCustomer;

    /**
     * Constructor
     */
    public ManageCustomerController() {
        this.modifiedCustomer = getModifiedCustomer();
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
     * Add an address
     * 
     * @param address1
     * @param address2
     * @param postalCode
     * @param phone
     * @param city
     * @return 
     */
    private Address handleAddAddress(String address1, String address2, String postalCode, String phone, City city) {
        Address address = new Address();
        
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setPostalCode(postalCode);
        address.setPhone(phone);
        address.setCity(city);

        AddressDAOInterface addressDAO = new AddressDAOMySQL();
        int newId = addressDAO.addAddress(address);
        
        address.setAddressId(newId);
        return address;
    }

    /**
     * Add a city
     * 
     * @param cityName
     * @param country
     * @return 
     */
    private City handleAddCity(String cityName, Country country) {
        City city = new City();
        city.setCityName(cityName);
        city.setCountry(country);
        
        CityDAOInterface cityDAO = new CityDAOMySQL();
        int newId = cityDAO.addCity(city);
        
        city.setCityId(newId);
        return city;
    }
    
    /**
     * Add a country
     * 
     * @param countryName
     * @return 
     */
    private Country handleAddCountry(String countryName) {
        Country country = new Country();
        country.setCountryName(countryName);
        
        CountryDAOInterface countryDAO = new CountryDAOMySQL();
        int newId = countryDAO.addCountry(country);
        
        country.setCountryId(newId);
        return country;
    }
    
    /**
     * Add a customer
     * 
     * @param customerName
     * @param address
     * @return 
     */
    private Customer handleAddCustomer(String customerName, Address address) {
        Customer customer = new Customer();
        customer.setCustomerName(customerName);
        customer.setAddress(address);
        
        CustomerDAOInterface customerDAO = new CustomerDAOMySQL();
        int newId = customerDAO.addCustomer(customer);
        
        customer.setCustomerId(newId);
        return customer;
    }
    
    /**
     * Cancel editing.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Cancel Modification");
        alert.setHeaderText("Cancel Modification");
        alert.setContentText("Are you sure you want to cancel?");
        alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> showCustomerScreen());
    }
    
    /**
     * Edit the address element
     * 
     * @param addressId
     * @param address1
     * @param address2
     * @param postalCode
     * @param phone
     * @param city
     * @return 
     */
    private Address handleEditAddress(int addressId, String address1, String address2, String postalCode, String phone, City city) {
        Address address = new Address();
        
        address.setAddressId(addressId);
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setPostalCode(postalCode);
        address.setPhone(phone);
        address.setCity(city);

        AddressDAOInterface addressDAO = new AddressDAOMySQL();
        addressDAO.updateAddress(address);

        return address;
    }

    /**
     * Edit the city element
     * 
     * @param cityId
     * @param cityName
     * @param country
     * @return 
     */
    private City handleEditCity(int cityId, String cityName, Country country) {
        City city = new City();
        city.setCityId(cityId);
        city.setCityName(cityName);
        city.setCountry(country);
        
        CityDAOInterface cityDAO = new CityDAOMySQL();
        cityDAO.updateCity(city);

        return city;
    }
    
    /**
     * Edit the country element
     * 
     * @param countryId
     * @param countryName
     * @return 
     */
    private Country handleEditCountry(int countryId, String countryName) {
        Country country = new Country();
        country.setCountryId(countryId);
        country.setCountryName(countryName);
        CountryDAOInterface countryDAO = new CountryDAOMySQL();
        countryDAO.updateCountry(country);
        
        return country;
    }
    
    /**
     * Edit a customer
     * 
     * @param customerId
     * @param customerName
     * @param address
     * @return 
     */
    private Customer handleEditCustomer(int customerId, String customerName, Address address) {
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setCustomerName(customerName);
        customer.setAddress(address);
        
        CustomerDAOInterface customerDAO = new CustomerDAOMySQL();
        customerDAO.updateCustomer(customer);
        
        return customer;
    }
    
    /**
     * Save a customer
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleSave(ActionEvent event) throws IOException {
        // Get values from the GUI
        String customerName = CustomerNameField.getText();
        String address1 = CustomerAddress1Field.getText();
        String address2 = CustomerAddress2Field.getText();
        String addressPhone = CustomerAddressPhoneField.getText();
        String addressPostalCode = CustomerAddressPostalCodeField.getText();
        String addressCity = CustomerAddressCityField.getText();
        String addressCountry = CustomerAddressCountryField.getText();
        
        // If there is a modified customer, edit. Otherwise add anew.
        if (modifiedCustomer == null) {
            Country country = handleAddCountry(addressCountry);
            City city = handleAddCity(addressCity, country);
            Address address = handleAddAddress(address1, address2, addressPostalCode, addressPhone, city);
            
            try {
                address.validate();
                handleAddCustomer(customerName, address);
            } catch (InvalidAddressException e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            Address modifiedCustomerAddress = modifiedCustomer.getAddress();
            City modifiedCustomerCity = modifiedCustomerAddress.getCity();
            Country modifiedCustomerCountry = modifiedCustomerCity.getCountry();
            
            Country country = handleEditCountry(modifiedCustomerCountry.getCountryId(), addressCountry);
            City city = handleEditCity(modifiedCustomerCity.getCityId(), addressCity, country);
            Address address = handleEditAddress(modifiedCustomerAddress.getAddressId(), address1, address2, addressPostalCode, addressPhone, city);
            
            try {
                address.validate();
                handleEditCustomer(modifiedCustomer.getCustomerId(), customerName, address);
            } catch (InvalidAddressException e) {
                System.out.println(e.getMessage());
            }
        }

        showCustomerScreen();
    }
       
    /**
     * Initialize
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (modifiedCustomer != null) {
            CustomerNameField.setText(modifiedCustomer.getCustomerName());
            CustomerAddress1Field.setText(modifiedCustomer.getAddress().getAddress1());
            CustomerAddress2Field.setText(modifiedCustomer.getAddress().getAddress2());
            CustomerAddressPhoneField.setText(modifiedCustomer.getAddress().getPhone());
            CustomerAddressPostalCodeField.setText(modifiedCustomer.getAddress().getPostalCode());
            CustomerAddressCityField.setText(modifiedCustomer.getAddress().getCity().getCityName());
            CustomerAddressCountryField.setText(modifiedCustomer.getAddress().getCity().getCountry().getCountryName());
        }
    }
    
    /**
     * Show the main customer screen
     */
    @FXML
    private void showCustomerScreen() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/Customer.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            CustomerController controller = loader.getController();
            controller.bind(stage);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
