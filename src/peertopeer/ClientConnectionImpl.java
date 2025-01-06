package peertopeer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientConnectionImpl extends UnicastRemoteObject implements ClientConnection {

    public ClientConnectionImpl() throws RemoteException {
        super();
    }

    @Override
    public MessageBox connect(String nickname, MessageBox receiver) throws RemoteException {
        return new MessageBoxImpl(nickname, receiver);
    }
}
