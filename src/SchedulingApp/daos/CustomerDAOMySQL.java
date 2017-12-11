package SchedulingApp.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import SchedulingApp.SchedulingApp;
import SchedulingApp.models.Customer;

/**
 * MySQL Customer data access object
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class CustomerDAOMySQL implements CustomerDAOInterface {
    
    // DB Connection, established in main file.
    private final static Connection conn = SchedulingApp.conn;
    
    /**
     * Constructor
     */
    public CustomerDAOMySQL() {
    }
    
    /**
     * Get the current max table ID.
     * 
     * @return maxID
     */
    private int getMaxId() {
        int maxID = 0;

        String maxIdQuery = "SELECT MAX(customerId) FROM customer";
        try {
            Statement stmt = conn.createStatement(); 
            ResultSet result = stmt.executeQuery(maxIdQuery);
            
            if(result.next()) {
                maxID = result.getInt(1);
            }
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return maxID + 1;
    }
    
    /**
     * Add a new customer
     * 
     * @param customer
     * @return customerID
     */
    @Override
    public int addCustomer(Customer customer) {
        String addCustomerQuery = String.join(" ",
            "INSERT INTO customer (customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)",
            "VALUES (?, ?, ?, 1, NOW(), ?, NOW(), ?)"
        );
                
        
        int customerId = getMaxId();
        try {
            PreparedStatement stmt = conn.prepareStatement(addCustomerQuery);
            stmt.setInt(1, customerId);
            stmt.setString(2, customer.getCustomerName());
            stmt.setInt(3, customer.getAddress().getAddressId());
            stmt.setString(4, SchedulingApp.user.username);
            stmt.setString(5, SchedulingApp.user.username);
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return customerId;
    }
    
    /**
     * Get all customers
     * 
     * @return ObservableList of Customer objects
     */
    @Override
    public ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        String getCustomerQuery = "SELECT * FROM customer WHERE active = 1"; 

        try {
            PreparedStatement stmt = conn.prepareStatement(getCustomerQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Customer fetchedCustomer = new Customer();
                fetchedCustomer.setCustomerName(rs.getString("customerName"));
                fetchedCustomer.setCustomerId(rs.getInt("customerId"));
                fetchedCustomer.setIsActive(rs.getInt("active"));

                AddressDAOInterface address = new AddressDAOMySQL();
                fetchedCustomer.setAddress(address.getAddress(rs.getInt("addressId")));
                
                customers.add(fetchedCustomer);
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return customers;
    }
 
    /**
     * Get a customer by ID
     * 
     * @param customerId
     * @return 
     */
    @Override
    public Customer getCustomer(int customerId) {
        String getCustomerQuery = "SELECT * FROM customer WHERE customerId = ?"; 
        Customer fetchedCustomer = new Customer();

        try {
            PreparedStatement stmt = conn.prepareStatement(getCustomerQuery);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()){
                fetchedCustomer.setCustomerName(rs.getString("customerName"));
                fetchedCustomer.setCustomerId(rs.getInt("customerId"));
                fetchedCustomer.setIsActive(rs.getInt("active"));

                AddressDAOInterface address = new AddressDAOMySQL();
                fetchedCustomer.setAddress(address.getAddress(rs.getInt("addressId")));
            } else {
                return null;
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return fetchedCustomer;
    }
    
    /**
     * Remove a customer.
     * 
     * @param customer 
     */
    @Override
    public void removeCustomer(Customer customer) {
        String removeCustomerQuery = "UPDATE customer SET active=0 WHERE customerId = ?";
    
        try {
            PreparedStatement stmt = conn.prepareStatement(removeCustomerQuery);
            stmt.setInt(1, customer.getCustomerId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Update a customer
     * 
     * @param customer 
     */
    @Override
    public void updateCustomer(Customer customer) {
        String updateCustomerQuery = String.join(" ",
            "UPDATE customer",
            "SET customerName=?, addressId=?, lastUpdate=NOW(), lastUpdateBy=?",
            "WHERE customerId = ?"
        );
    
        try {
            PreparedStatement stmt = conn.prepareStatement(updateCustomerQuery);
            stmt.setString(1, customer.getCustomerName());
            stmt.setInt(2, customer.getAddress().getAddressId());
            stmt.setString(3, SchedulingApp.user.username);
            stmt.setInt(4, customer.getCustomerId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
