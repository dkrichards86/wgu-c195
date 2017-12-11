package SchedulingApp.exceptions;

/**
 * Invalid appointment exception.
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class InvalidAppointmentException extends Exception {
    public InvalidAppointmentException(String message) {
        super(message);
    }
}
