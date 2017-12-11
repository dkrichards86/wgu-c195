package SchedulingApp.exceptions;

/**
 * Overlapping appointment exception.
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class OverlappingAppointmentException extends Exception {
    public OverlappingAppointmentException(String message) {
        super(message);
    }
}
