package SchedulingApp.daos;

import SchedulingApp.models.Address;

/**
 * Address data access object interface
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public interface AddressDAOInterface {
    public int addAddress(Address address);
    
    public Address getAddress(int addressId);
    
    public void removeAddress(Address address);
    
    public void updateAddress(Address address);
}