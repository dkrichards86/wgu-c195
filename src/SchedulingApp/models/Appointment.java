package SchedulingApp.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.collections.ObservableList;

import SchedulingApp.daos.AppointmentDAOInterface;
import SchedulingApp.daos.AppointmentDAOMySQL;
import SchedulingApp.exceptions.InvalidAppointmentTimeException;
import SchedulingApp.exceptions.InvalidAppointmentException;
import SchedulingApp.exceptions.InvalidCustomerException;
import SchedulingApp.exceptions.OverlappingAppointmentException;

/**
 * Appointment. An appointment is scheduled by a User, and includes a Customer.
 * 
 * @see User, Customer
 * @author Dale Richards <dric123@wgu.edu>
 */
public class Appointment {
    
    // Unique ID
    public int appointmentId;
    
    // Contact
    private String contact;
    
    // Customer
    public Customer customer;
    
    // Appointment description
    private String description;
    
    // Appointment end
    private LocalDateTime end;
    
    // Appointment location
    private String location;
    
    // appointment start
    private LocalDateTime start;
    
    // Appointment title
    public String title;
    
    // URL
    private String url;
    
    // midnight today. Maintained for validation purposes.
    private final LocalDateTime midnightToday;

    /**
     * Constructor
     */
    public Appointment() {
        // seed values for validation
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        midnightToday = LocalDateTime.of(today, midnight);
        seedAppointment();
    }
    
    /**
     * Get the unique id.
     * 
     * @return unique identifier
     */
    public int getAppointmentId() {
        return this.appointmentId;
    }
     
    /**
     * Get contact
     * 
     * @return contact
     */
    public String getContact() {
        return this.contact;
    }
    
    /**
     * Get customer
     * 
     * @return customer
     */
    public Customer getCustomer() {
        return this.customer;
    }
    
    /**
     * Get Description
     * 
     * @return description
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Get appointment end
     * 
     * @return end
     */
    public LocalDateTime getEnd() {
        return this.end;
    }
     
    /**
     * Get location
     * 
     * @return location
     */
    public String getLocation() {
        return this.location;
    }
     
    /**
     * Get appointment start
     * 
     * @return start
     */
    public LocalDateTime getStart() {
        return this.start;
    }

    /**
     * Get title
     * 
     * @return title
     */
    public String getTitle() {
        return this.title;
    }
     
    /**
     * Get URL
     * 
     * @return url
     */
    public String getUrl() {
        return this.url;
    }
    
    /**
     * Seed values for validation.
     */
    private void seedAppointment() {
        setTitle("");
        setDescription("");
        setLocation("");
        setContact("");
        setUrl("");
        setStart(midnightToday);
        setEnd(midnightToday.plusMinutes(30));
    }

    /**
     * Set the unique id
     * 
     * @param appointmentId unique id 
     */
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
     
    /**
     * Set contact.
     * 
     * @param contact 
     */
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    /**
     * Set the customer
     * 
     * @param customer 
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    /**
     * Set description
     * 
     * @param description 
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Set the appointment end time
     * 
     * @param end 
     */
    public void setEnd(LocalDateTime end) {
        // If the appointment is set to start after the end, invert the times.
        if (
            this.start != null &&
            !this.start.isEqual(midnightToday) &&
            end.isBefore(this.start)
        ) {
            this.end = this.start;
            this.start = end;
            
        }
        else {
            this.end = end;
        }
    }
     
    /**
     * Set location
     * 
     * @param location 
     */
    public void setLocation(String location) {
        this.location = location;
    }
     
    /**
     * Set the appointment start time
     * 
     * @param start 
     */
    public void setStart(LocalDateTime start) {
        // If the appointment is set to end before the start, invert the times.
        if (
            this.end != null &&
            !this.end.isEqual(midnightToday.plusMinutes(30)) && 
            start.isAfter(this.end)
        ) {
            this.start = this.end;
            this.end = start;
            
        }
        else {
            this.start = start;
        }
    }

    /**
     * Set title
     * 
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }
     
    /**
     * Set URL
     * 
     * @param url 
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * Create a string representation
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return getTitle();
    }
    
    /**
     * Validate availability. Ensure no two appointments overlap.
     * 
     * @return true if valid
     * @throws OverlappingAppointmentException 
     */
    public boolean validateAvailability() throws OverlappingAppointmentException {
        AppointmentDAOInterface appointmentDAO = new AppointmentDAOMySQL();
        
        ObservableList<Appointment> appointments = appointmentDAO.getOverlappingAppointments(this.start, this.end);

        if (appointments.size() > 0) {
            throw new OverlappingAppointmentException("This appointment overlaps an existing appointment.");
        }
        
        return true;
    }
    
    /**
     * Validate time. Ensure the appointment is only one day in duration and 
     * scheduled during business hours. That is, Monday through Friday, 9am to 
     * 5pm local time.
     * 
     * @return true if valid
     * @throws InvalidAppointmentTimeException 
     */
    public boolean validateTime() throws InvalidAppointmentTimeException {
        LocalTime midnight = LocalTime.MIDNIGHT;

        LocalDate startDate = this.start.toLocalDate();
        LocalDate endDate = this.end.toLocalDate();
        int dayOfWeek = startDate.getDayOfWeek().getValue();
        
        LocalTime startTime = this.start.toLocalTime();
        LocalTime endTime = this.end.toLocalTime();
        
        if (!startDate.isEqual(endDate)) {
            throw new InvalidAppointmentTimeException("Appointments cannot span multiple days.");
        }
        
        if (dayOfWeek == 0 || dayOfWeek == 6) {
            throw new InvalidAppointmentTimeException("Appointments cannot be scheduled on the weekend.");
        }
        
        if (startTime.isBefore(midnight.plusHours(9))) {
            throw new InvalidAppointmentTimeException("Appointment is before normal business hours.");
        }
        
        if (endTime.isAfter(midnight.plusHours(17))) {
            throw new InvalidAppointmentTimeException("Appointment is after normal business hours.");
        } 
       
        return true;
    }
    
    /**
     * Validate the field
     * 
     * @return true if valid
     * @throws InvalidAppointmentException 
     */
    public boolean validate() throws InvalidAppointmentException {        
        if (this.customer == null) {
            throw new InvalidAppointmentException("Customer is required.");
        }
        
        if (this.title.equals("")) {
            throw new InvalidAppointmentException("Title is required.");
        }
                
        if (this.contact.equals("")) {
            throw new InvalidAppointmentException("Contact is required.");
        }
        
        if (this.url.equals("")) {
            throw new InvalidAppointmentException("URL is required.");
        }

        if (this.location.equals("")) {
            throw new InvalidAppointmentException("Location is required.");
        }
        
        if (this.start.isEqual(midnightToday)) {
            throw new InvalidAppointmentException("Start time is required.");
        }
        
        if (this.end.isEqual(midnightToday.plusMinutes(30))) {
            throw new InvalidAppointmentException("End time is required.");
        }
        
        if (this.description.equals("")) {
            throw new InvalidAppointmentException("Notes is required.");
        }
        
        try {
            validateTime();
            this.customer.validate();
        }
        catch (InvalidAppointmentTimeException | InvalidCustomerException e) {
            throw new InvalidAppointmentException(e.getMessage());
        }

        return true;
    }
}