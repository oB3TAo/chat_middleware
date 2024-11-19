package chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class EmitterImpl extends UnicastRemoteObject implements Emitter {
    private final String senderToken;
    private final ChatImpl chatService;

    public EmitterImpl(String senderToken, ChatImpl chatService) throws RemoteException {
        this.senderToken = senderToken;
        this.chatService = chatService;
    }

    @Override
    public void sendMessage(String recipient, String message) throws RemoteException {
        chatService.sendMessage(senderToken, recipient, message);
    }
}
