package peertopeer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientManagerImpl extends UnicastRemoteObject implements ClientManager {

    private final List<String> onlineClients = new ArrayList<>();

    public ClientManagerImpl() throws RemoteException {
        super();
    }

    @Override
    public void initClients(String[] clients) throws RemoteException {
        onlineClients.clear();
        Collections.addAll(onlineClients, clients);
    }

    @Override
    public void addClient(String nickname) throws RemoteException {
        onlineClients.add(nickname);
    }

    @Override
    public void removeClient(String nickname) throws RemoteException {
        onlineClients.remove(nickname);
    }

    @Override
    public List<String> getOnlineClients() throws RemoteException {
        return new ArrayList<>(onlineClients);
    }
}
