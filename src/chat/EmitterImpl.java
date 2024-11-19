package chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class EmitterImpl extends UnicastRemoteObject implements Emitter {

    private final String senderToken;
    private final ConnectionImpl connectionService;

    public EmitterImpl(String senderToken, ConnectionImpl connectionService) throws RemoteException {
        super();
        this.senderToken = senderToken;
        this.connectionService = connectionService;
    }

    @Override
    public void sendMessage(String recipient, String message) throws RemoteException {
        connectionService.sendMessage(senderToken, recipient, message);
    }
}
