package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Chat extends Remote {
    void connect(String pseudo) throws RemoteException;
    void disconnect(String pseudo) throws RemoteException;
    String[] getClients() throws RemoteException;
    void sendMessage(String from, String to, String message) throws RemoteException;
    String[] getMessages(String pseudo) throws RemoteException;
}
