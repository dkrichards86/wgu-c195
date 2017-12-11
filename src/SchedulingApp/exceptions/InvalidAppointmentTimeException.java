package SchedulingApp.exceptions;

/**
 * Invalid appointment time exception.
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class InvalidAppointmentTimeException extends Exception {
    public InvalidAppointmentTimeException(String message) {
        super(message);
    }
}
