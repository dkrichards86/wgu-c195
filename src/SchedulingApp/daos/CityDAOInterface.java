package SchedulingApp.daos;

import SchedulingApp.models.City;

/**
 * City data access object interface
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public interface CityDAOInterface {
    public int addCity(City city);
    
    public City getCity(int cityId);
    
    public void removeCity(City city);
    
    public void updateCity(City city);
}
