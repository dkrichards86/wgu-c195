package SchedulingApp.daos;

import SchedulingApp.models.Appointment;
import SchedulingApp.models.User;
import java.time.LocalDateTime;
import javafx.collections.ObservableList;

/**
 * Appointment data access object interface
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public interface AppointmentDAOInterface {
    public int addAppointment(Appointment appointment);
    
    public ObservableList<Appointment> getAllAppointments();
    
    public Appointment getAppointment(int appointmentId);
    
    public ObservableList<Appointment> getAppointmentsByUser(User user);
    
    public ObservableList<Appointment> getAppointmentsInRange(LocalDateTime start, LocalDateTime end);
    
    public ObservableList<Appointment> getOverlappingAppointments(LocalDateTime start, LocalDateTime end);
    
    public void removeAppointment(Appointment appointment);
    
    public void updateAppointment(Appointment appointment);
}