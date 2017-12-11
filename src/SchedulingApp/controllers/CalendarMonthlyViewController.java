package SchedulingApp.controllers;

import SchedulingApp.SchedulingApp;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import SchedulingApp.models.Appointment;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

/**
 * Calendar Monthly View Controller.
 * 
 * @See AbstractCalendarController
 * @author Dale Richards <dric123@wgu.edu>
 */
public class CalendarMonthlyViewController extends AbstractCalendarController implements Initializable {

    // Chosen month
    private YearMonth selectedMonth;

    // Appointments
    private ObservableList<Appointment> calendarAppointments = FXCollections.observableArrayList();

    // Formatter
    DateTimeFormatter yearMonthFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());

    /**
     * Show the day pane
     * 
     * @param dayOfMonth
     * @param currDate
     * @return 
     */
    private BorderPane getDatePane(int dayOfMonth, LocalDate currDate) {
        BorderPane datePane = new BorderPane();

        Label dateLabel = new Label();
        dateLabel.setText(Integer.toString(dayOfMonth));
        datePane.setTop(dateLabel);

        BorderPane.setAlignment(dateLabel, Pos.TOP_RIGHT);

        ObservableList<Appointment> filteredAppointments = calendarAppointments.stream()
            .filter(a -> a.getStart().toLocalDate().equals(currDate))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        ListView<Appointment> listView = new ListView<>(filteredAppointments); 
        
        listView.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> modifiedAppointment = newVal);
        
        datePane.setCenter(listView);
        
        return datePane;
    }

    /**
     * Get the month offset. Shifts the month an appropriate amount since not
     * every month starts on a Sunday. That is, a month that starts on Tuesday
     * will have the first element start at column 3 of the first row.
     * 
     * @return 
     */
    private int getMonthOffset() {
        LocalDate firstDay = selectedMonth.atDay(1);
        DayOfWeek day = firstDay.getDayOfWeek();
        int offset = (day.getValue() - 1) % 7;

        LocalDate firstSunday = firstDay.minusDays(offset);
        Period span = Period.between(firstDay, firstSunday);

        return span.getDays();
    }
    
    /**
     * Go to the next month
     */
    @FXML
    void handleNextMonth() {
        selectedMonth = selectedMonth.plusMonths(1);
        setCalendarView();
    }
  
    /**
     * Go to the previous month
     */
    @FXML
    void handlePreviousMonth() {
        selectedMonth = selectedMonth.minusMonths(1);
        setCalendarView();
    }
    
    /**
     * Switch to the weekly view
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    private void handleWeekly(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/CalendarWeeklyView.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        CalendarWeeklyViewController controller = loader.getController();
        controller.bind(stage);

        stage.show();
    }
    
    /**
     * Initialize
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedMonth = YearMonth.now();
        setCalendarView();
    }

    /**
     * Display the calendar
     */
    @Override
    public void setCalendarView() {
        resetCalendar();

        CalendarLabel.setText(selectedMonth.format(yearMonthFormat));
        
        LocalDate firstDate = selectedMonth.atDay(1);
        LocalDate lastDate = selectedMonth.atEndOfMonth();
        LocalDateTime startDatetime = LocalDateTime.of(firstDate, LocalTime.MIDNIGHT);
        LocalDateTime endDatetime = LocalDateTime.of(lastDate, LocalTime.MIDNIGHT);
        
        calendarAppointments = appointmentDAO.getAppointmentsInRange(startDatetime, endDatetime);

        int monthlyOffset = getMonthOffset();
        for (LocalDate date = firstDate; date.isBefore(lastDate.plusDays(1)); date = date.plusDays(1)) {
            int dayOfMonth = date.getDayOfMonth();
            int idx = dayOfMonth - monthlyOffset;

            BorderPane datePane = getDatePane(dayOfMonth, date);
            CalendarPane.add(datePane, idx % 7, idx / 7);
        }
    }
}
