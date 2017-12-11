package SchedulingApp.daos;

import SchedulingApp.models.Country;
import javafx.collections.ObservableList;

/**
 * Country data access object interface
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public interface CountryDAOInterface {
    public int addCountry(Country country);
    
    public ObservableList<Country> getAllCountries();
    
    public Country getCountry(int countryId);
    
    public void removeCountry(Country country);
    
    public void updateCountry(Country country);
}
