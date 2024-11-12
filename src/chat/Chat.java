package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Chat extends Remote {
    String register(String pseudo, String password) throws RemoteException;
    String login(String pseudo, String password) throws RemoteException;
    void connect(String token) throws RemoteException;
    void disconnect(String token) throws RemoteException;
    String[] getClients(String token) throws RemoteException;
    void sendMessage(String token, String to, String message) throws RemoteException;
    String[] getMessages(String token) throws RemoteException;
}

