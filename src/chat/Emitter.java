package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Emitter extends Remote {
    void sendMessage(String recipient, String message) throws RemoteException;
}
