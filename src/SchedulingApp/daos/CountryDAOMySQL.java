package SchedulingApp.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import SchedulingApp.SchedulingApp;
import SchedulingApp.models.Country;

/**
 * MySQL Country data access object
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class CountryDAOMySQL implements CountryDAOInterface {
    
    // DB Connection, established in main file.
    private final static Connection conn = SchedulingApp.conn;
    
    /**
     * Constructor
     */
    public CountryDAOMySQL() {
    }
 
    /**
     * Get the current max table ID.
     * 
     * @return maxID
     */
    private int getMaxId() {
        int maxID = 0;

        String maxIdQuery = "SELECT MAX(countryId) FROM country";
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
     * Add a new country. There should probably be some deduplication here, but
     * that's beyond the scope of this exercise.
     * 
     * @param country
     * @return ccountryId
     */
    @Override
    public int addCountry(Country country) {
        String addCountryQuery = String.join(" ",
            "INSERT INTO country (countryId, country, createDate, createdBy, lastUpdate, lastUpdateBy)",
            "VALUES (?, ?, NOW(), ?, NOW(), ?)"
        );
    
        int countryId = getMaxId();
        try {
            PreparedStatement stmt = conn.prepareStatement(addCountryQuery);
            stmt.setInt(1, countryId);
            stmt.setString(2, country.getCountryName());
            stmt.setString(3, SchedulingApp.user.username);
            stmt.setString(4, SchedulingApp.user.username);
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return countryId;
    }
    
    /**
     * Get all countries
     * 
     * @return ObservableList of Country objects
     */
    @Override
    public ObservableList<Country> getAllCountries() {
        ObservableList<Country> countries = FXCollections.observableArrayList();
        String getCustomerQuery = "SELECT * FROM country"; 

        try {
            PreparedStatement stmt = conn.prepareStatement(getCustomerQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Country fetchedCountry = new Country();
                fetchedCountry.setCountryName(rs.getString("country"));
                fetchedCountry.setCountryId(rs.getInt("countryId"));
                countries.add(fetchedCountry);
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return countries;
    }
    
    /**
     * Get a specific country by ID
     * 
     * @param countryId
     * @return Country
     */
    @Override
    public Country getCountry(int countryId) {
        String getCountryQuery = "SELECT * FROM country WHERE countryId = ?"; 
        Country fetchedCountry = new Country();

        try{
            PreparedStatement stmt = conn.prepareStatement(getCountryQuery);
            stmt.setInt(1, countryId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()){
                fetchedCountry.setCountryName(rs.getString("country"));
                fetchedCountry.setCountryId(rs.getInt("countryId"));
            } else {
                return null;
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return fetchedCountry;
    }
    
    /**
     * Remove a specific country by ID
     * 
     * @param country
     */
    @Override
    public void removeCountry(Country country) {
        String removeCountryQuery = "DELETE FROM country WHERE countryId = ?";
    
        try {
            PreparedStatement stmt = conn.prepareStatement(removeCountryQuery);
            stmt.setInt(1, country.getCountryId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Update a specific country by ID
     * 
     * @param country
     */
    @Override
    public void updateCountry(Country country) {
        String updateCountryQuery = String.join(" ",
            "UPDATE country SET country=?, lastUpdate=NOW(), lastUpdateBy=?",
            "WHERE countryId = ?"
        );
        
        try {
            PreparedStatement stmt = conn.prepareStatement(updateCountryQuery);
            stmt.setString(1, country.getCountryName());
            stmt.setString(2, SchedulingApp.user.getUsername());
            stmt.setInt(3, country.getCountryId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
