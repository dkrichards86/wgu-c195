package SchedulingApp.daos;

import SchedulingApp.models.User;
import javafx.collections.ObservableList;

/**
 * User data access object interface
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public interface UserDAOInterface {
    
    public ObservableList<User> getAllUsers();
    
    public User login(String username, String password);
}