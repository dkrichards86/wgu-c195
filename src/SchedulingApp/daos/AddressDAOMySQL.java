package SchedulingApp.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import SchedulingApp.SchedulingApp;
import SchedulingApp.models.Address;

/**
 * MySQL Address data access object
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class AddressDAOMySQL implements AddressDAOInterface {
    
    // DB Connection, established in main file.
    private final static Connection conn = SchedulingApp.conn;
    
    /**
     * Constructor
     */
    public AddressDAOMySQL() {
    }
    
    /**
     * Get the current max table ID.
     * 
     * @return maxID
     */
    private int getMaxId() {
        int maxID = 0;

        String maxIdQuery = "SELECT MAX(addressId) FROM address";
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
     * Add a new address
     * 
     * @param address
     * @return 
     */
    @Override
    public int addAddress(Address address) {
        String addAddressQuery = String.join(" ", 
            "INSERT INTO address (addressId,  address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)",
            "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)"
        );
    
        int addressId = getMaxId();
        try {
            PreparedStatement stmt = conn.prepareStatement(addAddressQuery);
            stmt.setInt(1, addressId);
            stmt.setString(2, address.getAddress1());
            stmt.setString(3, address.getAddress2());
            stmt.setInt(4, address.getCity().getCityId());
            stmt.setString(5, address.getPostalCode());
            stmt.setString(6, address.getPhone());
            stmt.setString(7, SchedulingApp.user.username);
            stmt.setString(8, SchedulingApp.user.username);
            
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return addressId;
    }

    /**
     * Get a specific address by ID
     * 
     * @param addressId
     * @return Address
     */
    @Override
    public Address getAddress(int addressId) {
        String getAddressQuery = "SELECT * FROM address WHERE addressId = ?"; 
        Address fetchedAddress = new Address();

        try{
            PreparedStatement stmt = conn.prepareStatement(getAddressQuery);
            stmt.setInt(1, addressId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()){
                fetchedAddress.setAddressId(rs.getInt("addressId"));
                fetchedAddress.setAddress1(rs.getString("address"));
                fetchedAddress.setAddress2(rs.getString("address2"));
                fetchedAddress.setPostalCode(rs.getString("postalCode"));
                fetchedAddress.setPhone(rs.getString("phone"));
                
                CityDAOInterface city = new CityDAOMySQL();
                fetchedAddress.setCity(city.getCity(rs.getInt("cityId")));
                
            } else {
                return null;
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return fetchedAddress;
    }
    
    /**
     * Remove a specific address
     * 
     * @param address 
     */
    @Override
    public void removeAddress(Address address) {
        String removeAddressQuery = "DELETE FROM address WHERE addressId = ?";
    
        try {
            PreparedStatement stmt = conn.prepareStatement(removeAddressQuery);
            stmt.setInt(1, address.getAddressId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Update a specific address
     * 
     * @param address 
     */
    @Override
    public void updateAddress(Address address) {
        String updateAddressQuery = String.join(" ",
            "UPDATE address",
            "SET address=?, address2=?, cityId=?, postalCode=?, phone=?, lastUpdate=NOW(), lastUpdateBy=?",
            "WHERE addressId = ?"
        );
    
        try {
            PreparedStatement stmt = conn.prepareStatement(updateAddressQuery);
            stmt.setString(1, address.getAddress1());
            stmt.setString(2, address.getAddress2());
            stmt.setInt(3, address.getCity().getCityId());
            stmt.setString(4, address.getPostalCode());
            stmt.setString(5, address.getPhone());
            stmt.setString(6, SchedulingApp.user.username);
            stmt.setInt(7, address.getAddressId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
