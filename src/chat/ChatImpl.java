package chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatImpl extends UnicastRemoteObject implements Chat {
    private List<String> clients = new ArrayList<>();
    private Map<String, List<String>> messages = new HashMap<>();

    protected ChatImpl() throws RemoteException {
        super();
    }

    @Override
    public void connect(String pseudo) throws RemoteException {
        if (!clients.contains(pseudo)) {
            clients.add(pseudo);
            messages.put(pseudo, new ArrayList<>());
            System.out.println(pseudo + " has connected.");
        }
    }

    @Override
    public void disconnect(String pseudo) throws RemoteException {
        clients.remove(pseudo);
        messages.remove(pseudo);
        System.out.println(pseudo + " has disconnected.");
    }

    @Override
    public String[] getClients() throws RemoteException {
        return clients.toArray(new String[0]);
    }

    @Override
    public void sendMessage(String from, String to, String message) throws RemoteException {
        if (messages.containsKey(to)) {
            messages.get(to).add(from + ": " + message);
        }
    }

    @Override
    public String[] getMessages(String pseudo) throws RemoteException {
        List<String> userMessages = messages.get(pseudo);
        if (userMessages != null) {
            String[] messageArray = userMessages.toArray(new String[0]);
            userMessages.clear();
            return messageArray;
        }
        return new String[0];
    }
}

