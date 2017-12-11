package SchedulingApp.models;

import SchedulingApp.exceptions.InvalidAddressException;
import SchedulingApp.exceptions.InvalidCustomerException;

/**
 * Customer. A customer is an entity in the system that can consult with users
 * via appointments.
 * 
 * @see Appointment
 * @author Dale Richards <dric123@wgu.edu>
 */
public class Customer {
    
    // Customer's address
    public Address address;
    
    // Unique ID
    public int customerId;
    
    // Customer name
    public String customerName;
    
    // Customer activity
    public int isActive;

    /**
     * Constructor
     */
    public Customer() {
        // seed values for validation
        seedCustomer();
    }
    
    /**
     * Set the customer's address
     * 
     * @return 
     */
    public Address getAddress() {
        return this.address;
    }
    
    /**
     * Get the unique id.
     * 
     * @return unique identifier
     */
    public int getCustomerId() {
        return this.customerId;
    }
    
    /**
     * Set customer name
     * 
     * @return customer name
     */
    public String getCustomerName() {
        return this.customerName;
    }

    /**
     * Set active status
     * 
     * @return status
     */
    public int getIsActive() {
        return this.isActive;
    }
    
    /**
     * Seed values for validation.
     */
    private void seedCustomer() {
        setCustomerName("");
    }
    
    /**
     * Set address
     * 
     * @param address 
     */
    public void setAddress(Address address) {
        this.address = address;
    }
    
    /**
     * Set the unique id
     * 
     * @param customerId unique id 
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    /**
     * Set name
     * 
     * @param customerName 
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Set status
     * 
     * @param isActive 
     */
    public void setIsActive (int isActive) {
        this.isActive = isActive;
    }
    
    /**
     * Create a string representation
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return getCustomerName();
    }
    
    /**
     * Validate the field
     * 
     * @return true if valid
     * @throws InvalidCustomerException 
     */
    public boolean validate() throws InvalidCustomerException {
        if (this.customerName.equals("")) {
            throw new InvalidCustomerException("Customer name is required.");
        }
        
        try {
            this.address.validate();
        }
        catch (InvalidAddressException e) {
            throw new InvalidCustomerException(e.getMessage());
        }

        return true;
    }
    
}
