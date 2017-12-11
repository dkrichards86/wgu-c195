package SchedulingApp.controllers;

import SchedulingApp.SchedulingApp;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import SchedulingApp.models.Appointment;
import java.net.URL;
import java.time.Duration;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * Calendar Weekly View Controller.
 * 
 * @See AbstractCalendarController
 * @author Dale Richards <dric123@wgu.edu>
 */
public class CalendarWeeklyViewController extends AbstractCalendarController implements Initializable {

    // FXML Day labels
    private List<Label> dateLabels;
    @FXML
    private Label CalendarSundayLabel;
    @FXML
    private Label CalendarMondayLabel;
    @FXML
    private Label CalendarTuesdayLabel;
    @FXML
    private Label CalendarWednesdayLabel;
    @FXML
    private Label CalendarThursdayLabel;
    @FXML
    private Label CalendarFridayLabel;
    @FXML
    private Label CalendarSaturdayLabel;

    // Appointments to show
    private ObservableList<Appointment> calendarAppointments = FXCollections.observableArrayList();
    
    // The selected week
    private LocalDate selectedWeek;

    // Formatters
    DateTimeFormatter weekFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    DateTimeFormatter shortTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    /**
     * Add the day label
     */
    private void applyDateLabels() {
        for (int i = 0; i < dateLabels.size(); i++) {
            Label dateLabel = dateLabels.get(i);

            LocalDate setDate = selectedWeek.plusDays(i);
            dateLabel.setText(setDate.format(weekFormat));
        }
    }
    
    /**
     * Add the hourly labels
     */
    private void applyHourLabels() {
        LocalTime time = LocalTime.MIDNIGHT.plusHours(9);
        for (int i = 0; i < 18; i++) {
            Label timeLabel = new Label(time.format(shortTime));
            CalendarPane.add(timeLabel, 0, i);

            time = time.plusMinutes(30);
        }
    }
    
    /**
     * Display an appointment block
     * 
     * @param appointment
     * @return 
     */
    private TextArea getTimeBlock(Appointment appointment) {
        TextArea apptBlock = new TextArea(appointment.getTitle());
        apptBlock.setEditable(false);
        apptBlock.setWrapText(true);
        
        apptBlock.focusedProperty()
            .addListener((obs, oldVal, newVal) -> modifiedAppointment = appointment);

        return apptBlock;
    }
    
    /**
     * Switch to the monthly view
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    private void handleMonthly(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/CalendarMonthlyView.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        CalendarMonthlyViewController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }
    
    /**
     * Show the next week
     */
    @FXML
    void handleNextWeek() {
        selectedWeek = selectedWeek.plusWeeks(1);
        setCalendarView();
    }
  
    /**
     * Show the previous week
     */
    @FXML
    void handlePreviousWeek() {
        selectedWeek = selectedWeek.minusWeeks(1);
        setCalendarView();
    }
    
    /**
     * Initialize
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dateLabels = new ArrayList<>();
        dateLabels.add(CalendarSundayLabel);
        dateLabels.add(CalendarMondayLabel);
        dateLabels.add(CalendarTuesdayLabel);
        dateLabels.add(CalendarWednesdayLabel);
        dateLabels.add(CalendarThursdayLabel);
        dateLabels.add(CalendarFridayLabel);
        dateLabels.add(CalendarSaturdayLabel);

        LocalDate today = LocalDate.now();
        int offset = today.getDayOfWeek().getValue() % 7;
	selectedWeek = today.minusDays(offset);
        setCalendarView();
    }

    /**
     * Display the calendar
     */
    @Override
    public void setCalendarView() {
        resetCalendar();

        CalendarLabel.setText("Week of " + selectedWeek.format(weekFormat));

        LocalDate lastDate = selectedWeek.plusWeeks(1);
        LocalDateTime startDatetime = LocalDateTime.of(selectedWeek, LocalTime.MIDNIGHT);
        LocalDateTime endDatetime = LocalDateTime.of(lastDate, LocalTime.MIDNIGHT);
        calendarAppointments = appointmentDAO.getAppointmentsInRange(startDatetime, endDatetime);

        applyDateLabels();
        applyHourLabels();

        LocalDate date = selectedWeek;
        for (int colIndex = 0; colIndex < 7; colIndex++) {
            LocalDate currDate = date;
            ObservableList<Appointment> filteredAppointments = calendarAppointments.stream()
                .filter(a -> a.getStart().toLocalDate().equals(currDate))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

            for (Appointment appointment : filteredAppointments) {
                LocalTime startTime = appointment.getStart().toLocalTime();

                int hourChunk = startTime.getHour();
                
                int minuteChunk = startTime.getMinute();
                int minuteOffset = 0;
                if (minuteChunk == 30) {
                    minuteOffset = 1;
                }
                
                int rowIndex = ((hourChunk - 9) * 2) + minuteOffset;

                long duration = Duration.between(startTime, appointment.getEnd().toLocalTime()).toMinutes();
                int rowSpan = (int) (duration / 30);
                
                TextArea apptBlock = getTimeBlock(appointment);
                CalendarPane.add(apptBlock, colIndex + 1, rowIndex, 1, rowSpan);
            }
            date = date.plusDays(1);
        }
    }
}
