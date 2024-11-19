package chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionImpl extends UnicastRemoteObject implements Connection {

    private final Map<String, String> userPasswords = new ConcurrentHashMap<>();
    private final Map<String, String> tokenToUsernameMap = new ConcurrentHashMap<>();
    private final Map<String, Receiver> connectedClients = new ConcurrentHashMap<>();

    public ConnectionImpl() throws RemoteException {
        super();
    }

    @Override
    public String register(String username, String password) throws RemoteException {
        if (userPasswords.containsKey(username)) {
            return "User already exists.";
        }
        userPasswords.put(username, password); // Simplified; use hashing in production
        return "User registered successfully.";
    }

    @Override
    public String login(String username, String password) throws RemoteException {
        String storedPassword = userPasswords.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            String token = UUID.randomUUID().toString();
            tokenToUsernameMap.put(token, username);
            return token;
        }
        return null;
    }

    @Override
    public Emitter connect(String token, Receiver receiver) throws RemoteException {
        String username = tokenToUsernameMap.get(token);
        if (username == null) {
            throw new RemoteException("Invalid token.");
        }

        connectedClients.put(username, receiver);
        broadcastClientListUpdate();

        return new EmitterImpl(token, this);
    }

    @Override
    public void disconnect(String token) throws RemoteException {
        String username = tokenToUsernameMap.get(token);
        if (username != null) {
            connectedClients.remove(username);
            tokenToUsernameMap.remove(token);
            broadcastClientListUpdate();
        } else {
            throw new RemoteException("Invalid token.");
        }
    }

    @Override
    public String[] getClients(String token) throws RemoteException {
        String username = tokenToUsernameMap.get(token);
        if (username == null) {
            throw new RemoteException("Invalid token.");
        }
        return connectedClients.keySet().toArray(new String[0]);
    }

    public void sendMessage(String senderToken, String recipient, String message) throws RemoteException {
        String sender = tokenToUsernameMap.get(senderToken);
        if (sender == null) {
            throw new RemoteException("Invalid sender token.");
        }

        Receiver receiver = connectedClients.get(recipient);
        if (receiver != null) {
            receiver.receiveMessage(sender + ": " + message);
        } else {
            throw new RemoteException("Recipient not found.");
        }
    }

    private void broadcastClientListUpdate() {
        String[] clients = connectedClients.keySet().toArray(new String[0]);
        for (Receiver receiver : connectedClients.values()) {
            try {
                receiver.initClients(clients);
            } catch (RemoteException e) {
                System.out.println("Failed to update client list: " + e.getMessage());
            }
        }
    }
}
