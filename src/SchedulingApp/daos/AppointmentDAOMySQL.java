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
import SchedulingApp.models.User;

/**
 * MySQL Appointment data access object
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class AppointmentDAOMySQL implements AppointmentDAOInterface {
    
    // DB Connection, established in main file.
    private final static Connection conn = SchedulingApp.conn;
    
    /**
     * Constructor
     */
    public AppointmentDAOMySQL() {
    }
    
    /**
     * Get the current max table ID.
     * 
     * @return maxID
     */
    private int getMaxId() {
        int maxID = 0;

        String maxIdQuery = "SELECT MAX(appointmentId) FROM appointment";
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
     * Add a new appointment
     * 
     * @param appointment
     * @return appointmentID
     */
    @Override
    public int addAppointment(Appointment appointment) {
        String addAppointmentQuery = String.join(" ",
            "INSERT INTO appointment (appointmentId, customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)",
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)"
        );
    
        int appointmentId = getMaxId();
        try {
            PreparedStatement stmt = conn.prepareStatement(addAppointmentQuery);
            stmt.setInt(1, appointmentId);
            stmt.setInt(2, appointment.getCustomer().getCustomerId());
            stmt.setString(3, appointment.getTitle());
            stmt.setString(4, appointment.getDescription());
            stmt.setString(5, appointment.getLocation());
            stmt.setString(6, appointment.getContact());
            stmt.setString(7, appointment.getUrl());

            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime startUTC = appointment.getStart().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime endUTC = appointment.getEnd().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            stmt.setTimestamp(8, Timestamp.valueOf(startUTC));
            stmt.setTimestamp(9, Timestamp.valueOf(endUTC));
            
            stmt.setString(10, SchedulingApp.user.username);
            stmt.setString(11, SchedulingApp.user.username);
            
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return appointmentId;
    }

    /**
     * Get all appointments
     * 
     * @return ObservableList of Appointment
     */
    @Override
    public ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String getAppointmentQuery = String.join(" ",
            "SELECT * FROM appointment AS a",
            "JOIN customer AS c",
            "ON a.customerId = c.customerId",
            "WHERE c.active = 1"
        ); 

        try{
            PreparedStatement stmt = conn.prepareStatement(getAppointmentQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment fetchedAppointment = new Appointment();
                fetchedAppointment.setAppointmentId(rs.getInt("appointmentId"));
                fetchedAppointment.setTitle(rs.getString("title"));
                fetchedAppointment.setDescription(rs.getString("description"));
                fetchedAppointment.setLocation(rs.getString("location"));
                fetchedAppointment.setContact(rs.getString("contact"));
                fetchedAppointment.setUrl(rs.getString("url"));
                
                ZoneId zone = ZoneId.systemDefault();
                LocalDateTime startUTC = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime endUTC = rs.getTimestamp("end").toLocalDateTime();
                LocalDateTime startLocal = startUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(zone).toLocalDateTime();
                LocalDateTime endLocal = endUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(zone).toLocalDateTime();
                
                fetchedAppointment.setStart(startLocal);
                fetchedAppointment.setEnd(endLocal);
                
                CustomerDAOInterface customer = new CustomerDAOMySQL();
                fetchedAppointment.setCustomer(customer.getCustomer(rs.getInt("customerId")));
                
                appointments.add(fetchedAppointment);
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return appointments;
    }
    
    /**
     * Get a specific appointment
     * 
     * @param appointmentId
     * @return 
     */
    @Override
    public Appointment getAppointment(int appointmentId) {
        String getAppointmentQuery = String.join(" ",
            "SELECT * FROM appointment AS a",
            "JOIN customer AS c",
            "ON a.customerId = c.customerId",
            "WHERE a.appointmentId = ? AND c.active = 1"
        ); 
        
        Appointment fetchedAppointment = new Appointment();

        try{
            PreparedStatement stmt = conn.prepareStatement(getAppointmentQuery);
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()){
                
                fetchedAppointment.setAppointmentId(rs.getInt("appointmentId"));
                fetchedAppointment.setTitle(rs.getString("title"));
                fetchedAppointment.setDescription(rs.getString("description"));
                fetchedAppointment.setLocation(rs.getString("location"));
                fetchedAppointment.setContact(rs.getString("contact"));
                fetchedAppointment.setUrl(rs.getString("url"));
                
                ZoneId zone = ZoneId.systemDefault();
                LocalDateTime startUTC = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime endUTC = rs.getTimestamp("end").toLocalDateTime();
                LocalDateTime startLocal = startUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(zone).toLocalDateTime();
                LocalDateTime endLocal = endUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(zone).toLocalDateTime();
                
                fetchedAppointment.setStart(startLocal);
                fetchedAppointment.setEnd(endLocal);
                
                CustomerDAOInterface customer = new CustomerDAOMySQL();
                fetchedAppointment.setCustomer(customer.getCustomer(rs.getInt("customerId")));
                
            } else {
                return null;
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return fetchedAppointment;
    }
    
    /**
     * Get appointments associated with a particular user
     * 
     * @param user
     * @return ObservableList of Appointment objects
     */
    @Override
    public ObservableList<Appointment> getAppointmentsByUser(User user) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        
        String getAppointmentQuery = String.join(" ",
            "SELECT * FROM appointment AS a",
            "JOIN customer AS c",
            "ON a.customerId = c.customerId",
            "WHERE c.active = 1",
            "AND a.createdBy = ?"
        ); 

        try {
            PreparedStatement stmt = conn.prepareStatement(getAppointmentQuery);
            stmt.setString(1, user.getUsername());
            
            ResultSet rs = stmt.executeQuery();
            
            ZoneId zone = ZoneId.systemDefault();
            
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
                
                CustomerDAOInterface customer = new CustomerDAOMySQL();
                fetchedAppointment.setCustomer(customer.getCustomer(rs.getInt("customerId")));
                
                appointments.add(fetchedAppointment);
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }

        return appointments;
    }
    
    /**
     * Get appointments in a given range
     * 
     * @param start
     * @param end
     * @return ObservableList of Appointment objects
     */
    @Override
    public ObservableList<Appointment> getAppointmentsInRange(LocalDateTime start, LocalDateTime end) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        
        String getAppointmentQuery = String.join(" ",
            "SELECT * FROM appointment AS a",
            "JOIN customer AS c",
            "ON a.customerId = c.customerId",
            "WHERE a.start >= ? AND a.end <= ?",
            "AND c.active = 1"
        );

        try {
            PreparedStatement stmt = conn.prepareStatement(getAppointmentQuery);
            
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime startDatetimeParam = start.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime endDatetimeParam = end.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            stmt.setTimestamp(1, Timestamp.valueOf(startDatetimeParam));
            stmt.setTimestamp(2, Timestamp.valueOf(endDatetimeParam));
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
                
                CustomerDAOInterface customer = new CustomerDAOMySQL();
                fetchedAppointment.setCustomer(customer.getCustomer(rs.getInt("customerId")));
                
                appointments.add(fetchedAppointment);
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return appointments;
    }
    
    /**
     * Get overlapping appointments
     * 
     * @param start
     * @param end
     * @return ObservableList of Appointment objects
     */
    @Override
    public ObservableList<Appointment> getOverlappingAppointments(LocalDateTime start, LocalDateTime end) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        
        String getAppointmentQuery = String.join(" ",
            "SELECT * FROM appointment AS a",
            "JOIN customer AS c",
            "ON a.customerId = c.customerId",
            "WHERE (a.start >= ? AND a.end <= ?)",
            "OR (a.start <= ? AND a.end >= ?)",
            "OR (a.start BETWEEN ? AND ? OR a.end BETWEEN ? AND ?)",
            "AND c.active = 1"
        ); 

        try{
            PreparedStatement stmt = conn.prepareStatement(getAppointmentQuery);
            
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime startDatetimeParam = start.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime endDatetimeParam = end.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            stmt.setTimestamp(1, Timestamp.valueOf(startDatetimeParam));
            stmt.setTimestamp(2, Timestamp.valueOf(endDatetimeParam));
            stmt.setTimestamp(3, Timestamp.valueOf(startDatetimeParam));
            stmt.setTimestamp(4, Timestamp.valueOf(endDatetimeParam));
            stmt.setTimestamp(5, Timestamp.valueOf(startDatetimeParam));
            stmt.setTimestamp(6, Timestamp.valueOf(endDatetimeParam));
            stmt.setTimestamp(7, Timestamp.valueOf(startDatetimeParam));
            stmt.setTimestamp(8, Timestamp.valueOf(endDatetimeParam));
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
                
                CustomerDAOInterface customer = new CustomerDAOMySQL();
                fetchedAppointment.setCustomer(customer.getCustomer(rs.getInt("customerId")));
                
                appointments.add(fetchedAppointment);
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return appointments;
    }
    
    /**
     * Remove a specific appointment
     * 
     * @param appointment 
     */
    @Override
    public void removeAppointment(Appointment appointment) {
        String removeAppointmentQuery = "DELETE FROM appointment WHERE appointmentId = ?";
    
        try {
            PreparedStatement stmt = conn.prepareStatement(removeAppointmentQuery);
            stmt.setInt(1, appointment.getAppointmentId());
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Update a specific appointment
     * 
     * @param appointment 
     */
    @Override
    public void updateAppointment(Appointment appointment) {
        String updateAppointmentQuery = String.join(" ",
            "UPDATE appointment",
            "SET customerId=?, title=?, description=?, location=?, contact=?, url=?, start=?, end=?, lastUpdate=NOW(), lastUpdateBy=?",
            "WHERE appointmentId = ?"
        );
    
        try {
            PreparedStatement stmt = conn.prepareStatement(updateAppointmentQuery);
            stmt.setInt(1, appointment.getCustomer().getCustomerId());
            stmt.setString(2, appointment.getTitle());
            stmt.setString(3, appointment.getDescription());
            stmt.setString(4, appointment.getLocation());
            stmt.setString(5, appointment.getContact());
            stmt.setString(6, appointment.getUrl());
            
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime startUTC = appointment.getStart().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime endUTC = appointment.getEnd().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            stmt.setTimestamp(7, Timestamp.valueOf(startUTC));
            stmt.setTimestamp(8, Timestamp.valueOf(endUTC));
            
            stmt.setString(9, SchedulingApp.user.username);
            stmt.setInt(10, appointment.getAppointmentId());
            
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
