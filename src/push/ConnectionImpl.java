package push;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionImpl extends UnicastRemoteObject implements Connection {

    private final Map<String, String> userPasswords = new ConcurrentHashMap<>(); // Stores hashed passwords
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

        try {
            String hashedPassword = hashPassword(password);
            userPasswords.put(username, hashedPassword);
            return "User registered successfully.";
        } catch (NoSuchAlgorithmException e) {
            throw new RemoteException("Error while hashing password.", e);
        }
    }

    @Override
    public String login(String username, String password) throws RemoteException {
        String storedHashedPassword = userPasswords.get(username);
        try {
            if (storedHashedPassword != null && storedHashedPassword.equals(hashPassword(password))) {
                String token = UUID.randomUUID().toString();
                tokenToUsernameMap.put(token, username);
                return token;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RemoteException("Error while hashing password.", e);
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

        for (Map.Entry<String, Receiver> entry : connectedClients.entrySet()) {
            try {
                if (!entry.getKey().equals(username)) {
                    entry.getValue().addClient(username);
                }
            } catch (RemoteException e) {
                System.out.println("Failed to notify client about new connection: " + e.getMessage());
            }
        }

        receiver.initClients(connectedClients.keySet().toArray(new String[0]));

        return new EmitterImpl(token, connectedClients, tokenToUsernameMap);
    }

    @Override
    public void disconnect(String token) throws RemoteException {
        String username = tokenToUsernameMap.get(token);
        if (username != null) {
            connectedClients.remove(username);
            tokenToUsernameMap.remove(token);

            for (Receiver receiver : connectedClients.values()) {
                try {
                    receiver.remClient(username);
                } catch (RemoteException e) {
                    System.out.println("Failed to notify client about disconnection: " + e.getMessage());
                }
            }
        } else {
            throw new RemoteException("Invalid token.");
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
