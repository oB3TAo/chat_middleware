package chat;

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
import java.util.Timer;

public class ChatClientUI extends Application {

    private Chat chatService;
    private Emitter emitter;
    private String token;
    private ComboBox<String> clientList;
    private TextArea chatArea;
    private TextField messageField;
    private Timer messagePollingTimer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat Client");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        Label statusLabel = new Label();

        loginButton.setOnAction(event -> handleLogin(usernameField.getText(), passwordField.getText(), statusLabel));
        registerButton.setOnAction(event -> handleRegister(usernameField.getText(), passwordField.getText(), statusLabel));

        VBox loginLayout = new VBox(10, usernameField, passwordField, loginButton, registerButton, statusLabel);
        loginLayout.setAlignment(Pos.CENTER);

        Scene loginScene = new Scene(loginLayout, 300, 200);
        primaryStage.setScene(loginScene);
        primaryStage.show();

        connectToServer();
    }

    private void connectToServer() {
        try {
            chatService = (Chat) Naming.lookup("rmi://localhost:1099/Chat");
        } catch (Exception e) {
            System.out.println("Failed to connect to server: " + e.getMessage());
        }
    }

    private void handleLogin(String username, String password, Label statusLabel) {
        try {
            token = chatService.login(username, password);
            if (token != null) {
                statusLabel.setText("Logged in successfully.");
                ReceiverImpl receiver = new ReceiverImpl(this);
                emitter = chatService.connect(token, receiver);
                showChatInterface();
            } else {
                statusLabel.setText("Invalid credentials.");
            }
        } catch (RemoteException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void handleRegister(String username, String password, Label statusLabel) {
        try {
            String response = chatService.register(username, password);
            statusLabel.setText(response);
        } catch (RemoteException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void showChatInterface() {
        Stage chatStage = new Stage();
        chatStage.setTitle("Chat Interface");

        clientList = new ComboBox<>();
        Button refreshButton = new Button("Refresh Users");
        chatArea = new TextArea();
        chatArea.setEditable(false);
        messageField = new TextField();
        messageField.setPromptText("Enter your message");
        Button sendButton = new Button("Send");

        refreshButton.setOnAction(event -> refreshClientList());
        sendButton.setOnAction(event -> sendMessage());

        VBox layout = new VBox(10, new HBox(10, clientList, refreshButton), chatArea, new HBox(10, messageField, sendButton));
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        chatStage.setScene(new Scene(layout, 400, 400));
        chatStage.show();

        refreshClientList();
    }

    private void refreshClientList() {
        try {
            clientList.getItems().setAll(chatService.getClients(token));
        } catch (RemoteException e) {
            System.out.println("Error refreshing client list: " + e.getMessage());
        }
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
                System.out.println("Error sending message: " + e.getMessage());
            }
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
}
