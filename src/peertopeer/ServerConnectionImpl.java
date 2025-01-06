package peertopeer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerConnectionImpl extends UnicastRemoteObject implements ServerConnection {

    private final Map<String, String> users = new HashMap<>(); // Stores username -> hashedPassword
    private final Map<String, ClientConnection> onlineClients = new HashMap<>(); // Stores username -> ClientConnection
    private final Map<String, ClientManager> clientManagers = new HashMap<>(); // Stores username -> ClientManager

    public ServerConnectionImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized void register(String username, String hashedPassword) throws RemoteException {
        if (users.containsKey(username)) {
            throw new RemoteException("Username already exists.");
        }
        users.put(username, hashedPassword);
        System.out.println("User registered: " + username);
    }

    @Override
    public synchronized boolean login(String username, String hashedPassword, ClientConnection clientConnection, ClientManager clientManager) throws RemoteException {
        if (!users.containsKey(username) || !users.get(username).equals(hashedPassword)) {
            return false;
        }
        connect(username, clientConnection, clientManager);
        return true;
    }

    @Override
    public synchronized void connect(String username, ClientConnection clientConnection, ClientManager clientManager) throws RemoteException {
        if (onlineClients.containsKey(username)) {
            throw new RemoteException("User already connected.");
        }

        // Add the client to the lists
        onlineClients.put(username, clientConnection);
        clientManagers.put(username, clientManager);

        // Notify all clients of the new user
        for (Map.Entry<String, ClientManager> entry : clientManagers.entrySet()) {
            if (!entry.getKey().equals(username)) {
                entry.getValue().addClient(username);
            }
        }

        // Provide the newly connected client with the list of online users
        clientManager.initClients(onlineClients.keySet().toArray(new String[0]));

        System.out.println("Client connected: " + username);
    }

    @Override
    public synchronized void disconnect(String username) throws RemoteException {
        if (!onlineClients.containsKey(username)) {
            throw new RemoteException("User is not connected.");
        }

        onlineClients.remove(username);
        clientManagers.remove(username);

        // Notify all clients of the disconnection
        for (Map.Entry<String, ClientManager> entry : clientManagers.entrySet()) {
            entry.getValue().removeClient(username);
        }

        System.out.println("Client disconnected: " + username);
    }

    @Override
    public synchronized ClientConnection getClient(String username) throws RemoteException {
        if (!onlineClients.containsKey(username)) {
            throw new RemoteException("User not found or offline.");
        }
        return onlineClients.get(username);
    }

    // Optional: Add a method to fetch all online users
    @Override
    public synchronized List<String> getOnlineClients() throws RemoteException {
        return new ArrayList<>(onlineClients.keySet());
    }

}
