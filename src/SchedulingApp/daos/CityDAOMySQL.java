package SchedulingApp.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import SchedulingApp.SchedulingApp;
import SchedulingApp.models.City;

/**
 * MySQL City data access object
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class CityDAOMySQL implements CityDAOInterface {
    
    // DB Connection, established in main file.
    private final static Connection conn = SchedulingApp.conn;
       
    /**
     * Constructor
     */
    public CityDAOMySQL() {
    }
    
    /**
     * Get the current max table ID.
     * 
     * @return maxID
     */
    private int getMaxId() {
        int maxID = 0;

        String maxIdQuery = "SELECT MAX(cityId) FROM city";
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
     * Add a new city. There should probably be some deduplication here, but
     * that's beyond the scope of this exercise.
     * 
     * @param city
     * @return cityId
     */
    @Override
    public int addCity(City city) {
        String addCityQuery = String.join(" ",
            "INSERT INTO city (cityId, city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy)",
            "VALUES (?, ?, ?, NOW(), ?, NOW(), ?)"
        );
    
        int cityId = getMaxId();
        try {
            PreparedStatement stmt = conn.prepareStatement(addCityQuery);
            stmt.setInt(1, cityId);
            stmt.setString(2, city.getCityName());
            stmt.setInt(3, city.getCountry().getCountryId());
            stmt.setString(4, SchedulingApp.user.username);
            stmt.setString(5, SchedulingApp.user.username);
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return cityId;
    }
 
    /**
     * Get a specific city by ID
     * 
     * @param cityId
     * @return City
     */
    @Override
    public City getCity(int cityId) {
        String getCityQuery = "SELECT * FROM city WHERE cityId = ?"; 
        City fetchedCity = new City();

        try{
            PreparedStatement stmt = conn.prepareStatement(getCityQuery);
            stmt.setInt(1, cityId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()){
                fetchedCity.setCityName(rs.getString("city"));
                fetchedCity.setCityId(rs.getInt("cityId"));
                
                CountryDAOInterface country = new CountryDAOMySQL();
                fetchedCity.setCountry(country.getCountry(rs.getInt("countryId")));
            } else {
                return null;
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return fetchedCity;
    }
    
    /**
     * Remove a specific city by ID
     * 
     * @param city
     */
    @Override
    public void removeCity(City city) {
        String removeCityQuery = "DELETE FROM city WHERE cityId = ?";
    
        try {
            PreparedStatement stmt = conn.prepareStatement(removeCityQuery);
            stmt.setInt(1, city.getCityId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Update a specific city by ID
     * 
     * @param city
     */
    @Override
    public void updateCity(City city) {
        String updateCityQuery = String.join(" ",
            "UPDATE city SET city=?, countryId=?, lastUpdate=NOW(), lastUpdateBy=?",
            "WHERE cityId = ?"
        );
    
        try {
            PreparedStatement stmt = conn.prepareStatement(updateCityQuery);
            stmt.setString(1, city.getCityName());
            stmt.setInt(2, city.getCountry().getCountryId());
            stmt.setString(3, SchedulingApp.user.username);
            stmt.setInt(4, city.getCityId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
