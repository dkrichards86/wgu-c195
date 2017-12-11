package SchedulingApp.models;

import SchedulingApp.exceptions.InvalidAddressException;
import SchedulingApp.exceptions.InvalidCityException;

/**
 * Address is a component of a customer record. It is composed of City.
 * 
 * @see City
 * @author Dale Richards <dric123@wgu.edu>
 */
public class Address {
    // Unique ID
    public int addressId;
    
    // Primary address field
    public String address1;
    
    // Extra address field (unit, apt, etc)
    public String address2;
    
    // Address city
    public City city;
    
    // Address zip code
    public String postalCode;
    
    // Address phone number
    public String phone;

    /**
     * Constructor
     */
    public Address() {
        // seed values for validation
        seedAddress();
    }
    
    /**
     * Get the unique id.
     * 
     * @return unique identifier
     */
    public int getAddressId() {
        return this.addressId;
    }
 
    /**
     * Get the primary address field
     * 
     * @return primary address field
     */
    public String getAddress1() {
        return this.address1;
    }
 
    /**
     * Get the secondary address field
     * 
     * @return secondary address
     */
    public String getAddress2() {
        return this.address2;
    }
    
    /**
     * Get the address city object
     * 
     * @return City
     */
    public City getCity() {
        return this.city;
    }
    
    /**
     * Get the address phone number
     * 
     * @return phone number
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     * Get address postal code
     * 
     * @return postal code
     */
    public String getPostalCode() {
        return this.postalCode;
    }
    
    /**
     * Seed values for validation.
     */
    private void seedAddress() {
        setAddress1("");
        setAddress2("");
        setPostalCode("");
        setPhone("");
    }

    /**
     * Set the unique id
     * 
     * @param addressId unique id 
     */
    public void setAddressId (int addressId) {
        this.addressId = addressId;
    }
    
    /**
     * Set the primary address field
     * 
     * @param address1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    
    /** 
     * Set secondary address
     * 
     * @param address2 
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    
    /**
     * Set city
     * 
     * @param city 
     */
    public void setCity (City city) {
        this.city = city;
    }
    
    /**
     * Set phone number
     * 
     * @param phone 
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * Set postal code
     * 
     * @param postalCode 
     */
    public void setPostalCode (String postalCode) {
        this.postalCode = postalCode;
    }
    
    /**
     * Create a string representation
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        String fullAddress = String.join(" ",
            getAddress1(),
            getAddress2(),
            getCity().getCityName(),
            getPostalCode()
        );

        return fullAddress;
    }
    
    /**
     * Validate the field
     * 
     * @return true if valid
     * @throws InvalidAddressException 
     */
    public boolean validate() throws InvalidAddressException {
        if (this.address1.equals("")) {
            throw new InvalidAddressException("Address is a required field.");
        }
        
        if (this.postalCode.equals("")) {
            throw new InvalidAddressException("Postal code is a required field.");
        }
        
        if (this.phone.equals("")) {
            throw new InvalidAddressException("Phone number is a required field.");
        }
        
        try {
            this.city.validate();
        }
        catch (InvalidCityException e) {
            throw new InvalidAddressException(e.getMessage());
        }

        return true;
    }
}