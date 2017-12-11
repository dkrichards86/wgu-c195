package SchedulingApp.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import SchedulingApp.SchedulingApp;
import SchedulingApp.daos.UserDAOInterface;
import SchedulingApp.daos.UserDAOMySQL;
import SchedulingApp.exceptions.InvalidLoginException;
import SchedulingApp.models.User;

public class UserLoginController implements Initializable {
    Locale userLocale;
    ResourceBundle rb;
    
    private SchedulingApp mainApp;
    
    @FXML
    private TextField LoginUsernameField;
    
    @FXML
    private PasswordField LoginPasswordField;
    
    @FXML
    private Label LoginUsernameLabel;
    
    @FXML
    private Label LoginPasswordLabel;
    
    @FXML
    private DialogPane MessageBox;

    public UserLoginController(){
    }
    
    /**
     * Bind the current controller to the main view
     * 
     * @param app 
     */
    public void bind(SchedulingApp app) {
        this.mainApp = app;
    }

    @FXML
    void handleCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(this.rb.getString("confirm_title"));
        alert.setHeaderText(this.rb.getString("confirm_title"));
        alert.setContentText(this.rb.getString("confirm_text"));
        alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> System.exit(0));
    }
    
    @FXML
    void handleLogin(ActionEvent event) throws IOException {
        String loginUsername = LoginUsernameField.getText();
        String loginPassword = LoginPasswordField.getText();
        
        try {        
            User loginUser = new User();
            loginUser.setUsername(loginUsername);
            loginUser.setPassword(loginPassword);
            
            loginUser.validate();
            
            UserDAOInterface userDAO = new UserDAOMySQL();
            User user = userDAO.login(loginUsername, loginPassword);

            if (user == null) {
                throw new InvalidLoginException("Incorrect username or password.");
            }
            
            this.mainApp.setUser(user);
        } catch (InvalidLoginException | AssertionError e) {
            MessageBox.setContentText(e.getMessage());
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.userLocale = Locale.getDefault();
        this.rb = ResourceBundle.getBundle("LoginFields", this.userLocale);
        
        LoginUsernameLabel.setText(this.rb.getString("username"));
        LoginPasswordLabel.setText(this.rb.getString("password"));
    }
}
