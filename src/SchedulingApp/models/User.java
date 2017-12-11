package SchedulingApp.models;

/**
 * User. The user represents an agent in the system. The user can authenticate,
 * create customers and appointments, and set reminders.
 * 
 * @see Customer, Appointment, Reminder 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class User {
    
    // user activity status (active or inactive)
    public int isActive;
    
    // Password
    public String password;
    
    // Unique ID
    public int userId;
    
    // Username
    public String username;

    /**
     * Constructor
     */
    public User() {
        // seed values for validation
        seedUser();
    }
    
    /** 
     * Get activity status
     * 
     * @return active status
     */
    public int getIsActive() {
        return this.isActive;
    }
    
    /**
     * Get the user's password
     * 
     * @return password
     */
    public String getPassword() {
        return this.password;
    }
    
    /**
     * Get user's username
     * 
     * @return username
     */
    public String getUsername() {
        return this.username;
    }
    
    /**
     * Get the unique id.
     * 
     * @return unique identifier
     */
    public int getUserId() {
        return this.userId;
    }
    
    /**
     * Seed values for validation.
     */
    private void seedUser() {
        setUsername("");
        setPassword("");
    }
    
    /**
     * Set user activity
     * 
     * @param isActive 
     */
    public void setIsActive (int isActive) {
        this.isActive = isActive;
    }
    
    /**
     * Set password 
     * 
     * @param password 
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Set username
     * 
     * @param username 
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Set the unique id
     * 
     * @param userId unique id 
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    /**
     * Create a string representation
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return getUsername();
    }
    
    /**
     * Validate the field
     * 
     * @return true if valid
     * @throws AssertionError 
     */
    public boolean validate() throws AssertionError {
        assert !this.username.equals("");
        assert !this.password.equals("");
        
        return true;
    }
}