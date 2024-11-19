package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Connection extends Remote {
    Emitter connect(String token, Receiver receiver) throws RemoteException;

    void disconnect(String token) throws RemoteException;

    String register(String username, String password) throws RemoteException;

    String login(String username, String password) throws RemoteException;

    String[] getClients(String token) throws RemoteException;
}
