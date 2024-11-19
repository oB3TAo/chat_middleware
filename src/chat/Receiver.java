package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Receiver extends Remote {
    void receiveMessage(String message) throws RemoteException;

    void initClients(String[] clients) throws RemoteException;

    void addClient(String client) throws RemoteException;

    void remClient(String client) throws RemoteException;
}
