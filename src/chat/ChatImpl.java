package chat;

import chat.component.ConnectionComponent;
import chat.component.DialogComponent;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Implementation of the Chat interface using RMI.
 */
public class ChatImpl extends UnicastRemoteObject implements Chat {

    private final HashMap<String, String> userPasswords = new HashMap<>();
    private final HashMap<String, String> activeTokens = new HashMap<>();
    private final HashMap<String, Receiver> connectedClients = new HashMap<>(); // Storing connected clients' receivers
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
    public Emitter connect(String token, Receiver receiver) throws RemoteException {
        if (!isValidToken(token)) {
            throw new RemoteException("Invalid token");
        }

        String username = tokenToUsername(token);

        // Store the receiver for pushing updates
        connectedClients.put(username, receiver);

        // Notify all clients about the new connection
        broadcastClientListUpdate();

        // Provide the Emitter implementation for the client
        return new EmitterImpl(token, this);
    }

    @Override
    public String[] getClients(String token) throws RemoteException {
        // Validate token
        if (!isValidToken(token)) {
            throw new RemoteException("Invalid token");
        }
        return connectedClients.keySet().toArray(new String[0]);
    }

    @Override
    public void sendMessage(String token, String recipientUsername, String message) throws RemoteException {
        String sender = tokenToUsername(token);
        if (sender == null) {
            throw new RemoteException("Invalid sender token");
        }

        // Find recipient's receiver and push the message
        Receiver receiver = connectedClients.get(recipientUsername);
        if (receiver == null) {
            throw new RemoteException("Recipient not found");
        }

        String formattedMessage = sender + ": " + message;
        receiver.receiveMessage(formattedMessage); // Push the message to the recipient

        // Add the message to the dialog history
        DialogComponent dialog = connectionComponent.getDialog(token);
        if (dialog != null) {
            dialog.addMessage(formattedMessage);
        }
    }

    /**
     * Broadcast updated client list to all connected clients.
     */
    private void broadcastClientListUpdate() throws RemoteException {
        String[] clients = connectedClients.keySet().toArray(new String[0]);
        for (Receiver receiver : connectedClients.values()) {
            receiver.initClients(clients);
        }
    }

    /**
     * Hashes a password using SHA-256.
     *
     * @param password the password to hash.
     * @return the hashed password.
     */
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

    /**
     * Validates if a token exists in the active tokens.
     *
     * @param token the token to validate.
     * @return true if the token is valid, false otherwise.
     */
    private boolean isValidToken(String token) {
        return activeTokens.containsKey(token);
    }

    /**
     * Retrieves the username associated with a token.
     *
     * @param token the token to look up.
     * @return the username associated with the token.
     */
    private String tokenToUsername(String token) {
        return activeTokens.get(token);
    }
}
