package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Chat extends Remote {
    String register(String username, String password) throws RemoteException;

    String login(String username, String password) throws RemoteException;

    Emitter connect(String token, Receiver receiver) throws RemoteException;

    String[] getClients(String token) throws RemoteException;

    void sendMessage(String senderToken, String recipient, String message) throws RemoteException;
}
