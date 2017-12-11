package SchedulingApp.models;

import java.time.LocalDateTime;

/**
 * Reminder. Notify the User of an impending appointment.
 * 
 * @see User, Appointment
 * @author Dale Richards <dric123@wgu.edu>
 */
public class Reminder {
    
    // Appointment for reminder
    public Appointment appointment;
    
    // Unique ID
    public int reminderId;
    
    // Reminder datetime
    private LocalDateTime reminderDate;

    /**
     * Constructor
     * 
     * @param appointment
     */
    public Reminder(Appointment appointment) {
        this.appointment = appointment;
        this.reminderDate = appointment.getStart().minusMinutes(15);
    }
         
    /**
     * Get the associated appointment
     * 
     * @return 
     */
    public Appointment getAppointment() {
        return this.appointment;
    }
     
    /**
     * Get the reminder datetime
     * 
     * @return reminder datetime
     */
    public LocalDateTime getReminderDate() {
        return this.reminderDate;
    }
    
    /**
     * Get the unique id.
     * 
     * @return unique identifier
     */
    public int getReminderId() {
        return this.reminderId;
    }
    
    /**
     * Set the appointment
     * 
     * @param appointment 
     */
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
    
    /**
     * Set the reminder date.
     * 
     * @param reminderDate 
     */
    public void setReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }
    
    /**
     * Set the unique id
     * 
     * @param reminderId unique id 
     */
    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }
    
    /**
     * Create a string representation
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return getAppointment().getTitle();
    }
}