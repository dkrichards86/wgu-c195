package SchedulingApp.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import SchedulingApp.SchedulingApp;
import SchedulingApp.models.Appointment;
import SchedulingApp.models.Reminder;
import SchedulingApp.models.User;

/**
 * MySQL Reminder data access object
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class ReminderDAOMySQL implements ReminderDAOInterface {
    
    // DB Connection, established in main file.
    private final static Connection conn = SchedulingApp.conn;
       
    /**
     * Constructor
     */
    public ReminderDAOMySQL() {
    }
    
    /**
     * Get the current max table ID.
     * 
     * @return maxID
     */
    private int getMaxId() {
        int maxID = 0;

        String maxIdQuery = "SELECT MAX(reminderId) FROM reminder";
        try {
            Statement stmt = conn.createStatement(); 
            ResultSet result = stmt.executeQuery(maxIdQuery);
            
            if(result.next()) {
                maxID = result.getInt(1);
            }
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return maxID + 1;
    }

    /**
     * Add a new reminder.
     * 
     * @param reminder
     * @return cityId
     */
    @Override
    public int addReminder(Reminder reminder) {
        String addQuery = String.join(" ",
            "INSERT INTO reminder (reminderId, reminderDate, snoozeIncrement, snoozeIncrementTypeId, appointmentId, createdBy, createdDate, remindercol)",
            "VALUES (?, ?, 0, 0, ?, ?, NOW(), '')"
        );
    
        int reminderId = getMaxId();
        try {
            PreparedStatement stmt = conn.prepareStatement(addQuery);
            
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime reminderDate = reminder.getReminderDate().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            
            stmt.setInt(1, reminderId);
            stmt.setTimestamp(2, Timestamp.valueOf(reminderDate));
            stmt.setInt(3, reminder.getAppointment().getAppointmentId());
            stmt.setString(4, SchedulingApp.user.username);
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return reminderId;
    }
    
    /**
     * Get a list of approaching reminders. 
     * 
     * @param user
     * @param start
     * @param end
     * @return observable list of reminders
     */
    @Override
    public ObservableList<Reminder> getReminders(User user, LocalDateTime start, LocalDateTime end) {
        ObservableList<Reminder> reminders = FXCollections.observableArrayList();
        
        String getQuery = String.join(" ",
            "SELECT r.reminderId, a.* FROM reminder AS r",
            "JOIN appointment AS a",
            "ON r.appointmentId = a.appointmentId",
            "JOIN customer AS c",
            "ON a.customerId = c.customerId",
            "WHERE r.reminderDate BETWEEN ? AND ?",
            "AND a.createdBy = ?",
            "AND c.active = 1"
        ); 

        try{
            PreparedStatement stmt = conn.prepareStatement(getQuery);
        
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime startDatetimeParam = start.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime endDatetimeParam = end.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            stmt.setTimestamp(1, Timestamp.valueOf(startDatetimeParam));
            stmt.setTimestamp(2, Timestamp.valueOf(endDatetimeParam));
            stmt.setString(3, user.getUsername());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment fetchedAppointment = new Appointment();
                
                fetchedAppointment.setAppointmentId(rs.getInt("appointmentId"));
                fetchedAppointment.setTitle(rs.getString("title"));
                fetchedAppointment.setDescription(rs.getString("description"));
                fetchedAppointment.setLocation(rs.getString("location"));
                fetchedAppointment.setContact(rs.getString("contact"));
                fetchedAppointment.setUrl(rs.getString("url"));
                
                LocalDateTime startUTC = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime endUTC = rs.getTimestamp("end").toLocalDateTime();
                LocalDateTime startLocal = startUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(zone).toLocalDateTime();
                LocalDateTime endLocal = endUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(zone).toLocalDateTime();
                
                fetchedAppointment.setStart(startLocal);
                fetchedAppointment.setEnd(endLocal);
                
                Reminder reminder = new Reminder(fetchedAppointment);
                reminder.setReminderId(rs.getInt("reminderId"));
                
                reminders.add(reminder);
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return reminders;
    }
    
    /**
     * Remove a specific city by ID
     * 
     * @param reminder
     */
    @Override
    public void removeReminder(Reminder reminder) {
        String removeQuery = "DELETE FROM reminder WHERE reminderId = ?";
    
        try {
            PreparedStatement stmt = conn.prepareStatement(removeQuery);
            stmt.setInt(1, reminder.getReminderId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Update a specific city by ID
     * 
     * @param reminder
     */
    @Override
    public void updateReminder(Reminder reminder) {
        String updateQuery = String.join(" ",
            "UPDATE reminder SET reminderDate=?",
            "WHERE reminderId = ?"
        );
    
        try {
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime reminderDate = reminder.getReminderDate().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            
            stmt.setTimestamp(1, Timestamp.valueOf(reminderDate));
            stmt.setInt(2, reminder.getReminderId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
