package SchedulingApp.daos;

import SchedulingApp.models.Reminder;
import SchedulingApp.models.User;
import java.time.LocalDateTime;
import javafx.collections.ObservableList;

/**
 * City data access object interface
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public interface ReminderDAOInterface {
    public int addReminder(Reminder reminder);
    
    public ObservableList<Reminder> getReminders(User user, LocalDateTime start, LocalDateTime end);
    
    public void removeReminder(Reminder reminder);
    
    public void updateReminder(Reminder reminder);
}
