package SchedulingApp.daos;

import SchedulingApp.models.Customer;
import javafx.collections.ObservableList;

/**
 * Customer data access object interface
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public interface CustomerDAOInterface {

    public int addCustomer(Customer customer);
    
    public ObservableList<Customer> getAllCustomers();
    
    public Customer getCustomer(int customerId);
    
    public void removeCustomer(Customer customer);
    
    public void updateCustomer(Customer customer);
}
