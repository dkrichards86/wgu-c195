package SchedulingApp.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import SchedulingApp.SchedulingApp;
import SchedulingApp.models.User;

/**
 * MySQL User data access object
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class UserDAOMySQL implements UserDAOInterface {
    
    // DB Connection, established in main file.
    private final static Connection conn = SchedulingApp.conn;
    
    /**
     * Constructor
     */
    public UserDAOMySQL() {
    }
    
    /**
     * Get all active users from the database
     * 
     * @return Observable list of User
     */
    @Override
    public ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String getCustomerQuery = "SELECT * FROM user WHERE active = 1"; 

        try {
            PreparedStatement stmt = conn.prepareStatement(getCustomerQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User fetchedUser = new User();
                fetchedUser.setUsername(rs.getString("userName"));
                fetchedUser.setPassword(rs.getString("password"));
                fetchedUser.setUserId(rs.getInt("userId"));
                fetchedUser.setIsActive(rs.getInt("active"));

                users.add(fetchedUser);
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return users;
    }
 
    /**
     * Get a user from the database
     * 
     * @param username
     * @param password
     * @return User
     */
    @Override
    public User login(String username, String password) {
        String getUserQuery = "SELECT * FROM user WHERE userName = ? AND password = ?"; 
        User fetchedUser = new User();

        try{
            PreparedStatement stmt = conn.prepareStatement(getUserQuery);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()){
                fetchedUser.setUsername(rs.getString("userName"));
                fetchedUser.setPassword(rs.getString("password"));
                fetchedUser.setUserId(rs.getInt("userId"));
                fetchedUser.setIsActive(rs.getInt("active"));
            } else {
                return null;
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return fetchedUser;
    }
}
