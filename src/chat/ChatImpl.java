package chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatImpl extends UnicastRemoteObject implements Chat {
    private HashMap<String, String> userPasswords = new HashMap<>(); // Store username and hashed password
    private HashMap<String, String> activeTokens = new HashMap<>(); // Store token and corresponding username
    private HashMap<String, List<String>> userMessages = new HashMap<>(); // Store messages for each user
    private List<String> connectedClients = new ArrayList<>(); // Track connected clients

    public ChatImpl() throws RemoteException {
        super();
    }

    @Override
    public String register(String pseudo, String password) throws RemoteException {
        if (userPasswords.containsKey(pseudo)) {
            return "User already exists"; // User already exists
        }
        userPasswords.put(pseudo, hashPassword(password));
        userMessages.put(pseudo, new ArrayList<>()); // Initialize message list for new user
        return "User registered successfully";
    }

    @Override
    public String login(String pseudo, String password) throws RemoteException {
        String storedHash = userPasswords.get(pseudo);
        if (storedHash != null && storedHash.equals(hashPassword(password))) {
            String token = UUID.randomUUID().toString();
            activeTokens.put(token, pseudo);
            return token; // Return the token to the client
        }
        return null; // Return null if login fails
    }


    @Override
    public void connect(String token) throws RemoteException {
        String user = activeTokens.get(token);
        if (user == null) {
            throw new RemoteException("Invalid token");
        }
        if (!connectedClients.contains(user)) {
            connectedClients.add(user); // Add user to connected clients
        }
    }

    @Override
    public void disconnect(String token) throws RemoteException {
        String user = activeTokens.remove(token); // Remove token and retrieve user
        if (user != null) {
            connectedClients.remove(user); // Remove user from connected clients
        }
    }

    @Override
    public String[] getClients(String token) throws RemoteException {
        String user = activeTokens.get(token);
        if (user == null) {
            throw new RemoteException("Invalid token");
        }
        return connectedClients.toArray(new String[0]); // Return list of connected clients
    }

    @Override
    public void sendMessage(String token, String to, String message) throws RemoteException {
        String from = activeTokens.get(token);
        if (from == null) {
            throw new RemoteException("Invalid token");
        }
        if (!userMessages.containsKey(to)) {
            throw new RemoteException("Recipient does not exist");
        }
        String formattedMessage = from + ": " + message;
        userMessages.get(to).add(formattedMessage); // Store the message for the recipient
    }

    @Override
    public String[] getMessages(String token) throws RemoteException {
        String user = activeTokens.get(token);
        if (user == null) {
            throw new RemoteException("Invalid token");
        }
        List<String> messages = userMessages.get(user);
        if (messages == null) {
            return new String[0]; // No messages if user has no message list
        }
        String[] messageArray = messages.toArray(new String[0]);
        messages.clear(); // Clear messages after retrieval
        return messageArray;
    }

    // Hashes the password using SHA-256
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
