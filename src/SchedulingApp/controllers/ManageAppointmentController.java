package SchedulingApp.controllers;

import SchedulingApp.SchedulingApp;
import SchedulingApp.daos.AppointmentDAOInterface;
import SchedulingApp.daos.AppointmentDAOMySQL;
import SchedulingApp.daos.CustomerDAOInterface;
import SchedulingApp.daos.CustomerDAOMySQL;
import SchedulingApp.models.Appointment;
import SchedulingApp.models.Customer;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static SchedulingApp.controllers.CalendarMonthlyViewController.getModifiedAppointment;
import SchedulingApp.daos.ReminderDAOInterface;
import SchedulingApp.daos.ReminderDAOMySQL;
import SchedulingApp.exceptions.InvalidAppointmentException;
import SchedulingApp.exceptions.OverlappingAppointmentException;
import SchedulingApp.models.Reminder;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

public class ManageAppointmentController implements Initializable {
    
    // Main stage element
    private Stage stage;
    
    // GUI data objects
    private final ObservableList<Customer> customers;
    private final ObservableList<String> dropdownTimes;
    private final ObservableList<String> types;

    // GUI fields
    @FXML
    private DatePicker AppointmentDateField;
    @FXML
    private ComboBox<Customer> AppointmentCustomerField;
    @FXML
    private ComboBox<String> AppointmentStartField;
    @FXML
    private ComboBox<String> AppointmentEndField;
    @FXML
    private TextField AppointmentTitleField;
    @FXML
    private TextField AppointmentLocationField;
    @FXML
    private TextField AppointmentContactField;
    @FXML
    private TextField AppointmentURLField;
    @FXML
    private ComboBox<String> AppointmentDescField;

    // Formatter
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
 
    // If applicable, appointment to modify
    private final Appointment modifiedAppointment;

    /**
     * Constructor
     */
    public ManageAppointmentController() {
        modifiedAppointment = getModifiedAppointment();
        
        // Make a list of times. Start at 9am and go 5pm, adding an element for 
        // every half-hour.
        dropdownTimes = FXCollections.observableArrayList();
        LocalTime time = LocalTime.MIDNIGHT.plusHours(9);
        for (int i = 0; i < 17; i++) {
            dropdownTimes.add(time.format(timeFormat));
            time = time.plusMinutes(30);
        }
        
        // Get customers, sorted by name.
        CustomerDAOInterface customerDAO = new CustomerDAOMySQL();
        customers = customerDAO.getAllCustomers();
        customers.sort((a, b) -> a.getCustomerName().compareTo(b.getCustomerName()));
        
        // Make a list of appointment types. Ideally this would be in a database
        // for less data dependency.
        types = FXCollections.observableArrayList(
            "New Client",
            "Customer Request",
            "Follow Up",
            "Emergency",
            "Cancellation"
        );
    }
    
    /**
     * Bind the current controller to the main view
     * 
     * @param stage 
     */
    public void bind(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Cancel modification
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Cancel Modification");
        alert.setHeaderText("Cancel Modification");
        alert.setContentText("Are you sure you want to cancel?");
        alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> showCalendarScreen());
    }
    
    /**
     * Save an appointment
     * @param event
     * @throws IOException 
     */
    @FXML
    void handleSave(ActionEvent event) throws IOException {
        // Get values from the GUI
        LocalDate appointmentDate = AppointmentDateField.getValue();
        String appointmentStart = AppointmentStartField.getValue();
        String appointmentEnd = AppointmentEndField.getValue();
        Customer appointmentCustomer = AppointmentCustomerField.getValue();
        String appointmentLocation = AppointmentLocationField.getText();
        String appointmentTitle = AppointmentTitleField.getText();
        String appointmentUrl = AppointmentURLField.getText();
        String appointmentContact = AppointmentContactField.getText();
        String appointmentNotes = AppointmentDescField.getValue();
        
        try {
            // Throw if there is no chosen date or times. We do this now since
            // we attepmt to convert before creating an Appointment
            if (appointmentDate == null || appointmentStart == null || appointmentEnd == null) {
                throw new InvalidAppointmentException("Dates and times are required.");
            }
            
            // Combine date and times to make datetimes.
            LocalDateTime startDateTime = LocalDateTime.of(appointmentDate, LocalTime.parse(appointmentStart, timeFormat));
            LocalDateTime endDateTime = LocalDateTime.of(appointmentDate, LocalTime.parse(appointmentEnd, timeFormat));

            // Build the appointment object
            Appointment appointment = new Appointment();
            appointment.setCustomer(appointmentCustomer);
            appointment.setLocation(appointmentLocation);
            appointment.setContact(appointmentContact);
            appointment.setDescription(appointmentNotes);
            appointment.setUrl(appointmentUrl);
            appointment.setEnd(endDateTime);
            appointment.setStart(startDateTime);
            appointment.setTitle(appointmentTitle);
        
            // Validate the appointment
            appointment.validate();
            
            // Establish DAOs
            AppointmentDAOInterface appointmentDAO = new AppointmentDAOMySQL();
            ReminderDAOInterface reminderDAO = new ReminderDAOMySQL();

            // If there is no modifiedAppointment, we add. Otherwise, we update.
            if (modifiedAppointment == null) {
                try {
                    appointment.validateAvailability();
                    int newId = appointmentDAO.addAppointment(appointment);
                    appointment.setAppointmentId(newId);
                    
                    // Add a new reminder.
                    Reminder reminder = new Reminder(appointment);
                    int reminderId = reminderDAO.addReminder(reminder);
                }
                catch (OverlappingAppointmentException e) {
                    throw new InvalidAppointmentException(e.getMessage());
                }
            }
            else {
                appointment.setAppointmentId(modifiedAppointment.getAppointmentId());
                appointmentDAO.updateAppointment(appointment);
                
                // Update the reminder associated with this appointment.
                Reminder reminder = new Reminder(appointment);
                reminderDAO.updateReminder(reminder);
            }
            
            showCalendarScreen();
        }
        catch (InvalidAppointmentException e) {
            // If the times or dates aren't set above, we throw. This catches those
            // errors.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("Invalid Appointment");
            alert.setHeaderText("Invalid Appointment");
            alert.setContentText(e.getMessage());
            alert.showAndWait()
                .filter(response -> response == ButtonType.OK);
        }
    }
    
    /**
     * Initialize
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AppointmentDateField.setValue(LocalDate.now());
        AppointmentStartField.setItems(dropdownTimes);
        AppointmentEndField.setItems(dropdownTimes);
        AppointmentCustomerField.setItems(customers);
        AppointmentDescField.setItems(types);
        
        // If we have a modified appointment, seed its values
        if (modifiedAppointment != null) {
            AppointmentDateField.setValue(modifiedAppointment.getStart().toLocalDate());
            AppointmentStartField.getSelectionModel().select(modifiedAppointment.getStart().format(timeFormat));
            AppointmentEndField.getSelectionModel().select(modifiedAppointment.getEnd().format(timeFormat));
            AppointmentTitleField.setText(modifiedAppointment.getTitle());
            AppointmentLocationField.setText(modifiedAppointment.getLocation());
            AppointmentContactField.setText(modifiedAppointment.getContact());
            AppointmentURLField.setText(modifiedAppointment.getUrl());
            
            AppointmentDescField.getSelectionModel().select(modifiedAppointment.getDescription());
            AppointmentCustomerField.getSelectionModel().select(modifiedAppointment.getCustomer());
        }
    }
    
    /**
     * Show the monthly calendar.
     */
    @FXML
    private void showCalendarScreen() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/CalendarMonthlyView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            CalendarMonthlyViewController controller = loader.getController();
            controller.bind(stage);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}