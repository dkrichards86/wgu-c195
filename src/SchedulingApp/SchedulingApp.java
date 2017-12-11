package SchedulingApp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import SchedulingApp.daos.UserDAOInterface;
import SchedulingApp.daos.UserDAOMySQL;
import SchedulingApp.models.User;
import SchedulingApp.controllers.UserLoginController;
import SchedulingApp.controllers.MainController;
import SchedulingApp.daos.ReminderDAOInterface;
import SchedulingApp.daos.ReminderDAOMySQL;
import SchedulingApp.exceptions.InvalidLoginException;
import SchedulingApp.models.Appointment;
import SchedulingApp.models.Reminder;

/**
 * <h1>Scheduling Application for C195</h1>
 * This application is a CRUD scheduling application, providing a simple JavaFX 
 * GUI for maintaining a consultant schedules.
 * 
 * @author Dale Richards <dric123@wgu.edu>
 */
public class SchedulingApp extends Application {

    // FXML Stage
    private Stage stage;
    
    // FXML root anchor pane
    private AnchorPane root;
    
    // Database driver
    private static String DRIVER;
    
    // Database host
    private static String URL;
    
    // Database username
    private static String USERNAME;
    
    // Database password
    private static String PASSWORD;

    // Database connection
    public static Connection conn = null;
    
    // current user
    public static User user;
    
    // User DAO
    static private UserDAOInterface userDAO;
    
    // Appointment DAO
    static private ReminderDAOInterface reminderDAO;
    
    // Logging
    private final static Logger logger = Logger.getLogger("SchedulingLog");
    
    /**
     * Open or create a logger.
     */
    private static void createLogFile() {
        FileHandler fh;  
        try {  
            fh = new FileHandler("scheduling_app_log.txt", true);  
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
    
    /**
     * Get a database connection
     */
    private static void getDbConnection() {
        getDbCredentials();
        
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch(ClassNotFoundException | SQLException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Get DB credentials from the properties file
     */
    private static void getDbCredentials() {
        Properties prop = new Properties();
	InputStream propFile = null;

	try {
            propFile = new FileInputStream("database.properties");
            prop.load(propFile);
            DRIVER = prop.getProperty("dbdriver");
            URL = prop.getProperty("dburl");
            USERNAME = prop.getProperty("dbuser");
            PASSWORD = prop.getProperty("dbpassword");
	} catch (IOException ex) {
            ex.printStackTrace();
	} finally {
            if (propFile != null) {
                try {
                    propFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}
    }
    
    /**
     * Set the logged in user
     * 
     * @param loggedInUser
     * @throws IOException
     * @throws InvalidLoginException 
     */
    public void setUser(User loggedInUser) throws IOException, InvalidLoginException {
        user = loggedInUser;
        
        if (user != null) {
            logger.log(Level.INFO, "LOGIN: User ''{0}'' logged in.", user.getUsername()); 
            showMainScreen();
        }
        else {
            logger.log(Level.INFO, "UNSUCCESSFUL LOGIN."); 
            showLoginScreen();
        }
    }
    
    /**
     * Main
     * 
     * @param args 
     */
    public static void main(String[] args) {
        // Database prep
        getDbConnection();
        
        userDAO = new UserDAOMySQL();
        reminderDAO = new ReminderDAOMySQL();
        
        // Logging prep
        createLogFile();
        
        // Set up async reminders on another thread
        ScheduledExecutorService reminderService = null;
        
        try {
            Runnable remindersTask = () -> showRemindersTaskRunner();
            
            reminderService = Executors.newScheduledThreadPool(2);            
            reminderService.scheduleWithFixedDelay(remindersTask, 0, 15, TimeUnit.SECONDS);
    
            launch(args);
        } finally {
            // Clean up a reminder task runner on exit.
            if(reminderService != null) {
                reminderService.shutdown();
            }
        }
    }
    
    /**
     * Display the login screen
     * 
     * @throws IOException 
     */
    public void showLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/UserLogin.fxml"));
        Parent root = loader.load();
    
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        UserLoginController controller = loader.getController();     
        controller.bind(this);
        
        stage.show();  
    }
    
    /**
     * Display the main screen
     * 
     * @throws IOException 
     */
    private void showMainScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulingApp.class.getResource("/SchedulingApp/views/Main.fxml"));
        root = (AnchorPane)loader.load();
    
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        MainController controller = loader.getController();
        controller.bind(stage);
        
        stage.show();
    }
    
    /**
     * Display the reminder popup. Clicking 'ok' dismisses the alert.
     * 
     * @param reminder 
     */
    private static void showReminder(Reminder reminder) {
        Appointment appointment = reminder.getAppointment();
        String title = appointment.getTitle();
        LocalDateTime time = appointment.getStart();
        
        Duration timeUntil = Duration.between(LocalDateTime.now(), time);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Reminder");
        alert.setHeaderText("Reminder");
        alert.setContentText("Appointment " + title + " is in  " + (timeUntil.toMinutes() + 1) + " minutes.");
        alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> {
                reminderDAO.removeReminder(reminder);
            });
    }
    
    /**
     * Asynchronous task runner
     */
    private static void showRemindersTaskRunner() {
        try {
            // If there is no logged in user, we bail.
            if (user == null) {
                return;
            }
            
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusMinutes(15);
            
            ObservableList<Reminder> reminders = reminderDAO.getReminders(user, startTime, endTime);
            
            for (Reminder reminder : reminders) {
                Platform.runLater(() -> showReminder(reminder));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Start the FXML application
     * 
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        showLoginScreen();
    }
}
