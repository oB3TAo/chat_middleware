package peertopeer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MessageBoxImpl extends UnicastRemoteObject implements MessageBox {

    private final String nickname;
    private final MessageBox receiver;

    public MessageBoxImpl(String nickname, MessageBox receiver) throws RemoteException {
        super();
        this.nickname = nickname;
        this.receiver = receiver;
    }

    @Override
    public void receive(String message) throws RemoteException {
        System.out.println("[" + nickname + "] " + message);
        if (receiver != null) {
            receiver.receive(message);
        }
    }
}
