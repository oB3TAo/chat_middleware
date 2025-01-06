package peertopeer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerConnection extends Remote {
    void register(String username, String hashedPassword) throws RemoteException;
    boolean login(String username, String hashedPassword, ClientConnection clientConnection, ClientManager clientManager) throws RemoteException;
    void connect(String username, ClientConnection clientConnection, ClientManager clientManager) throws RemoteException;
    void disconnect(String username) throws RemoteException;
    ClientConnection getClient(String username) throws RemoteException;
    List<String> getOnlineClients() throws RemoteException;
}
