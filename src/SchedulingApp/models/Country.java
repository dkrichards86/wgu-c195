package SchedulingApp.models;

import SchedulingApp.exceptions.InvalidCountryException;

/**
 * Country. Country is an element of City.
 * @see City
 * @author Dale Richards <dric123@wgu.edu>
 */
public class Country {
    
    // Unique ID
    public int countryId;
    
    // Country name
    public String countryName;

    /**
     * Constructor
     */
    public Country() {
        // seed values for validation
        seedCountry();
    }
    
    /**
     * Get the unique id.
     * 
     * @return unique identifier
     */
    public int getCountryId() {
        return this.countryId;
    }
    
    /**
     * Get the country name
     * 
     * @return country name
     */
    public String getCountryName() {
        return this.countryName;
    }
    
    /**
     * Seed values for validation.
     */
    private void seedCountry() {
        setCountryName("");
    }
    
    /**
     * Set the unique id
     * 
     * @param cityId unique id 
     */
    public void setCountryId(int cityId) {
        this.countryId = cityId;
    }
    
    /**
     * Set the country name
     * 
     * @param countryName 
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    
    /**
     * Create a string representation
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return getCountryName();
    }

    /**
     * Validate the field
     * 
     * @return true if valid
     * @throws InvalidCountryException 
     */
    public boolean validate() throws InvalidCountryException {
        if (this.countryName.equals("")) {
            throw new InvalidCountryException("Country name is required.");
        }

        return true;
    }
}