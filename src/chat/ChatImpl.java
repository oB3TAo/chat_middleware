package chat;

import chat.component.ConnectionComponent;
import chat.component.DialogComponent;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatImpl extends UnicastRemoteObject implements Chat {

    private final HashMap<String, String> userPasswords = new HashMap<>();
    private final HashMap<String, String> activeTokens = new HashMap<>();
    private final ConnectionComponent connectionComponent = new ConnectionComponent();

    public ChatImpl() throws RemoteException {
        super();
    }

    @Override
    public String register(String pseudo, String password) throws RemoteException {
        if (userPasswords.containsKey(pseudo)) {
            return "User already exists";
        }
        userPasswords.put(pseudo, hashPassword(password));
        return "User registered successfully";
    }

    @Override
    public String login(String pseudo, String password) throws RemoteException {
        String storedHash = userPasswords.get(pseudo);
        if (storedHash != null && storedHash.equals(hashPassword(password))) {
            String token = UUID.randomUUID().toString();
            activeTokens.put(token, pseudo);
            connectionComponent.createDialog(token, pseudo); // Create a new DialogComponent
            return token;
        }
        return null;
    }

    @Override
    public void connect(String token) throws RemoteException {
        if (!activeTokens.containsKey(token)) {
            throw new RemoteException("Invalid token");
        }
    }

    @Override
    public void disconnect(String token) throws RemoteException {
        activeTokens.remove(token);
        connectionComponent.removeDialog(token); // Remove dialog component on disconnect
    }

    @Override
    public String[] getClients(String token) throws RemoteException {
        return activeTokens.values().toArray(new String[0]);
    }

    public void sendMessage(String token, String recipientUsername, String message) throws RemoteException {
        String sender = activeTokens.get(token);
        if (sender == null) {
            throw new RemoteException("Invalid sender token");
        }

        // Find recipient's dialog by username
        DialogComponent receiverDialog = connectionComponent.getDialogByUsername(recipientUsername);
        if (receiverDialog == null) {
            throw new RemoteException("Could not find recipient's dialog.");
        }

        String formattedMessage = sender + ": " + message;
        receiverDialog.addMessage(formattedMessage);
    }

    @Override
    public String[] getMessages(String token) throws RemoteException {
        DialogComponent dialog = connectionComponent.getDialog(token);
        if (dialog == null) {
            throw new RemoteException("Invalid token");
        }

        List<String> messages = dialog.getMessageHistory();
        String[] messageArray = messages.toArray(new String[0]);
        System.out.println("Messages retrieved for token " + token + ": " + messageArray.length);
        return messageArray;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing error", e);
        }
    }
}
