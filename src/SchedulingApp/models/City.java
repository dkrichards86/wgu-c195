package SchedulingApp.models;

import SchedulingApp.exceptions.InvalidCityException;
import SchedulingApp.exceptions.InvalidCountryException;

/**
 * City. City is an element of Address. It is composed of Country.
 * 
 * @see Address, Country
 * @author Dale Richards <dric123@wgu.edu>
 */
public class City {
    
    // Unique ID
    public int cityId;
    
    // City name
    public String cityName;
    
    // City country
    public Country country;

    /**
     * Constructor
     */
    public City() {
        // seed values for validation
        seedCity();
    }
    
    /**
     * Get the unique id.
     * 
     * @return unique identifier
     */
    public int getCityId() {
        return this.cityId;
    }
    
    /**
     * Get the city name
     * 
     * @return city name
     */
    public String getCityName() {
        return this.cityName;
    }
    
    /**
     * Get the country object
     * 
     * @return country
     */
    public Country getCountry() {
        return this.country;
    }
    
    /**
     * Seed values for validation.
     */
    private void seedCity() {
        setCityName("");
    }
    
    /**
     * Set the unique id
     * 
     * @param cityId unique id 
     */
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
    
    /**
     * Set the city name
     * 
     * @param cityName 
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    
    /**
     * Set the country
     * 
     * @param country 
     */
    public void setCountry(Country country) {
        this.country = country;
    }
    
    /**
     * Validate the field
     * 
     * @return true if valid
     * @throws InvalidCityException 
     */
    public boolean validate() throws InvalidCityException {
        if (this.cityName.equals("")) {
            throw new InvalidCityException("City name is required.");
        }
        
        try {
            this.country.validate();
        }
        catch (InvalidCountryException e) {
            throw new InvalidCityException(e.getMessage());
        }

        return true;
    }
}