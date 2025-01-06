package peertopeer.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import peertopeer.ClientConnection;
import peertopeer.MessageBox;
import peertopeer.MessageBoxImpl;
import peertopeer.ServerConnection;

import java.rmi.RemoteException;
import java.util.List;

public class ChatController {

    @FXML
    private ListView<String> onlineUsersList;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    private String username;
    private ServerConnection serverConnection;
    private MessageBox localMessageBox;

    public void initialize(String username, ServerConnection serverConnection) {
        this.username = username;
        this.serverConnection = serverConnection;

        // Initialize the user's message box
        try {
            this.localMessageBox = new MessageBoxImpl(username, message ->
                    Platform.runLater(() -> chatArea.appendText("From " + message + "\n")));
        } catch (RemoteException e) {
            chatArea.appendText("Failed to initialize message box: " + e.getMessage() + "\n");
        }

        loadOnlineUsers();
    }

    private void loadOnlineUsers() {
        try {
            // Fetch online clients from the server
            List<String> onlineUsers = serverConnection.getOnlineClients();
            Platform.runLater(() -> onlineUsersList.getItems().setAll(onlineUsers));
        } catch (RemoteException e) {
            chatArea.appendText("Failed to load online users: " + e.getMessage() + "\n");
        }
    }

    @FXML
    private void disconnect() {
        try {
            // Notify the server about disconnection
            serverConnection.disconnect(username);
            chatArea.appendText("Disconnected from the server.\n");
            Platform.exit(); // Exit the application
        } catch (RemoteException e) {
            chatArea.appendText("Failed to disconnect: " + e.getMessage() + "\n");
        }
    }

    @FXML
    private void sendMessage() {
        String selectedUser = onlineUsersList.getSelectionModel().getSelectedItem();
        String message = messageField.getText();

        if (selectedUser == null || message.isEmpty()) {
            chatArea.appendText("Please select a user and type a message.\n");
            return;
        }

        try {
            // Fetch the recipient's ClientConnection from the server
            ClientConnection recipientConnection = serverConnection.getClient(selectedUser);

            // Establish a message connection
            MessageBox recipientMessageBox = recipientConnection.connect(username, localMessageBox);

            // Send the message
            recipientMessageBox.receive(message);

            chatArea.appendText("To " + selectedUser + ": " + message + "\n");
            messageField.clear();
        } catch (Exception e) {
            chatArea.appendText("Failed to send message: " + e.getMessage() + "\n");
        }
    }
}
