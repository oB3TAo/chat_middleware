package peertopeer.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import peertopeer.ClientConnectionImpl;
import peertopeer.ClientManagerImpl;
import peertopeer.ServerConnection;

import java.rmi.RemoteException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private ServerConnection serverConnection;

    public void initialize(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password are required!");
            return;
        }

        try {
            boolean success = serverConnection.login(username, password, new ClientConnectionImpl(), new ClientManagerImpl());
            if (success) {
                showChatWindow(username);
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } catch (RemoteException e) {
            errorLabel.setText("Connection error: " + e.getMessage());
        }
    }

    @FXML
    private void register() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password are required!");
            return;
        }

        try {
            serverConnection.register(username, password);
            errorLabel.setText("Registration successful! Please log in.");
        } catch (RemoteException e) {
            errorLabel.setText("Registration failed: " + e.getMessage());
        }
    }

    private void showChatWindow(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/peertopeer/ui/view/ChatView.fxml"));
            Stage chatStage = new Stage();
            chatStage.setTitle("Chat - " + username);
            chatStage.setScene(new Scene(loader.load()));

            ChatController controller = loader.getController();
            controller.initialize(username, serverConnection);

            chatStage.show();
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (Exception e) {
            errorLabel.setText("Failed to load chat window: " + e.getMessage());
        }
    }
}
