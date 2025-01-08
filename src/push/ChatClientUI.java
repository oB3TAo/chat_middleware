package push;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class ChatClientUI extends Application {

    private Connection connectionService;
    private Emitter emitter;
    private String token;

    private ComboBox<String> clientList;
    private TextArea chatArea;
    private TextField messageField;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Chat Client");
        showLoginUI();
        connectToServer();
    }

    private void connectToServer() {
        try {
            connectionService = (Connection) Naming.lookup("rmi://localhost:1099/Connection");
        } catch (Exception e) {
            showError("Failed to connect to server: " + e.getMessage());
        }
    }

    private void showLoginUI() {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");

        VBox loginLayout = getVBox(usernameField, passwordField);

        Scene loginScene = new Scene(loginLayout, 300, 200);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private VBox getVBox(TextField usernameField, PasswordField passwordField) {
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Label statusLabel = new Label();

        loginButton.setOnAction(_ -> handleLogin(usernameField.getText(), passwordField.getText(), statusLabel));
        registerButton.setOnAction(_ -> handleRegister(usernameField.getText(), passwordField.getText(), statusLabel));

        VBox loginLayout = new VBox(10, usernameField, passwordField, loginButton, registerButton, statusLabel);
        loginLayout.setAlignment(Pos.CENTER);
        return loginLayout;
    }

    private void handleLogin(String username, String password, Label statusLabel) {
        try {
            token = connectionService.login(username, password);
            if (token != null) {
                statusLabel.setText("Logged in successfully.");
                ReceiverImpl receiver = new ReceiverImpl(this);
                emitter = connectionService.connect(token, receiver);
                showChatUI();
            } else {
                statusLabel.setText("Invalid credentials.");
            }
        } catch (RemoteException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void handleRegister(String username, String password, Label statusLabel) {
        try {
            String response = connectionService.register(username, password);
            statusLabel.setText(response);
        } catch (RemoteException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void showChatUI() {
        clientList = new ComboBox<>();
        chatArea = new TextArea();
        chatArea.setEditable(false);
        messageField = new TextField();
        messageField.setPromptText("Enter your message");
        Button sendButton = new Button("Send");
        Button disconnectButton = new Button("Disconnect");

        sendButton.setOnAction(_ -> sendMessage());
        disconnectButton.setOnAction(_ -> handleDisconnect());

        HBox userControls = new HBox(10, clientList, disconnectButton);
        userControls.setAlignment(Pos.CENTER);

        VBox chatLayout = new VBox(10, userControls, chatArea, new HBox(10, messageField, sendButton));
        chatLayout.setPadding(new Insets(10));
        chatLayout.setAlignment(Pos.CENTER);

        Scene chatScene = new Scene(chatLayout, 400, 500);
        Platform.runLater(() -> {
            primaryStage.setScene(chatScene);
            primaryStage.setResizable(false);
            primaryStage.show();
        });
    }

    private void sendMessage() {
        String recipient = clientList.getValue();
        String message = messageField.getText();
        if (recipient != null && !message.isEmpty()) {
            try {
                emitter.sendMessage(recipient, message);
                chatArea.appendText("You: " + message + "\n");
                messageField.clear();
            } catch (RemoteException e) {
                showError("Error sending message: " + e.getMessage());
            }
        } else {
            showError("Recipient or message is empty.");
        }
    }

    private void handleDisconnect() {
        try {
            connectionService.disconnect(token);
            token = null;
            emitter = null;
            Platform.runLater(this::showLoginUI);
        } catch (RemoteException e) {
            showError("Error disconnecting: " + e.getMessage());
        }
    }

    public void displayMessage(String message) {
        Platform.runLater(() -> chatArea.appendText(message + "\n"));
    }

    public void setClientList(String[] clients) {
        Platform.runLater(() -> clientList.getItems().setAll(clients));
    }

    public void addClient(String client) {
        Platform.runLater(() -> clientList.getItems().add(client));
    }

    public void removeClient(String client) {
        Platform.runLater(() -> clientList.getItems().remove(client));
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
