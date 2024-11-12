package chat;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

public class ChatClientUI extends Application {

    private Chat chatService;
    private String token;
    private ComboBox<String> clientList; // Dropdown for connected clients
    private TextArea chatArea; // Display area for message history
    private TextField messageField; // Input field for sending messages
    private Timer messagePollingTimer; // Polls server for new messages

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        connectToServer();

        primaryStage.setTitle("Chat Client");

        // Login interface
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        Label statusLabel = new Label();

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            handleLogin(username, password, statusLabel);
        });

        registerButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            handleRegister(username, password, statusLabel);
        });

        VBox vbox = new VBox(10, usernameField, passwordField, loginButton, registerButton, statusLabel);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void connectToServer() {
        try {
            chatService = (Chat) Naming.lookup("rmi://localhost:1099/Chat");
            System.out.println("Connected to chat server successfully.");
        } catch (Exception e) {
            System.out.println("Failed to connect to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLogin(String username, String password, Label statusLabel) {
        if (chatService == null) {
            statusLabel.setText("Server connection failed. Please restart the client.");
            return;
        }
        try {
            String token = chatService.login(username, password);
            if (token != null) {
                this.token = token;
                chatService.connect(token);
                statusLabel.setText("Logged in successfully as " + username);
                showChatInterface();
            } else {
                statusLabel.setText("Invalid credentials. Try again.");
            }
        } catch (RemoteException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void handleRegister(String username, String password, Label statusLabel) {
        if (chatService == null) {
            statusLabel.setText("Server connection failed. Please restart the client.");
            return;
        }
        try {
            String result = chatService.register(username, password);
            statusLabel.setText(result != null ? result : "User already exists.");
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
        chatArea.setPrefHeight(300);

        messageField = new TextField();
        messageField.setPromptText("Enter your message");
        Button sendButton = new Button("Send");

        refreshButton.setOnAction(event -> refreshClientList());
        sendButton.setOnAction(event -> sendMessage());

        HBox topBox = new HBox(10, clientList, refreshButton);
        topBox.setAlignment(Pos.CENTER);

        HBox messageBox = new HBox(10, messageField, sendButton);
        messageBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(10, topBox, chatArea, messageBox);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);

        Scene chatScene = new Scene(vbox, 400, 400);
        chatStage.setScene(chatScene);
        chatStage.show();

        refreshClientList();
        startMessagePolling();
    }

    private void refreshClientList() {
        try {
            String[] clients = chatService.getClients(token);
            clientList.getItems().setAll(clients);
        } catch (RemoteException e) {
            System.out.println("Failed to retrieve client list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String recipient = clientList.getValue();
        String message = messageField.getText();
        if (recipient == null || message.isEmpty()) {
            return;
        }

        try {
            chatService.sendMessage(token, recipient, message);
            chatArea.appendText("You: " + message + "\n");
            messageField.clear();
        } catch (RemoteException e) {
            System.out.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startMessagePolling() {
        messagePollingTimer = new Timer(true);
        messagePollingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String[] messages = chatService.getMessages(token);
                    if (messages != null) {
                        for (String message : messages) {
                            chatArea.appendText(message + "\n");
                        }
                    }
                } catch (RemoteException e) {
                    System.out.println("Error polling messages: " + e.getMessage());
                }
            }
        }, 0, 2000);
    }

    @Override
    public void stop() {
        if (messagePollingTimer != null) {
            messagePollingTimer.cancel();
        }
        try {
            if (chatService != null) {
                chatService.disconnect(token);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
