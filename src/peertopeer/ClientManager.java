package peertopeer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientManager extends Remote {
    void initClients(String[] clients) throws RemoteException;
    void addClient(String nickname) throws RemoteException;
    void removeClient(String nickname) throws RemoteException;
    List<String> getOnlineClients() throws RemoteException;
}
