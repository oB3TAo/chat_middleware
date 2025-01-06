package peertopeer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientConnection extends Remote {
    MessageBox connect(String clientId, MessageBox incomingMessageBox) throws RemoteException;
}
