package peertopeer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageBox extends Remote {
    void receive(String message) throws RemoteException;
}
