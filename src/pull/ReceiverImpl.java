package pull;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ReceiverImpl extends UnicastRemoteObject implements Receiver {

    private final ChatClientUI clientUI;

    public ReceiverImpl(ChatClientUI clientUI) throws RemoteException {
        super();
        this.clientUI = clientUI;
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        clientUI.displayMessage(message);
    }

    @Override
    public void initClients(String[] clients) throws RemoteException {
        clientUI.setClientList(clients);
    }

    @Override
    public void addClient(String client) throws RemoteException {
        clientUI.addClient(client);
    }

    @Override
    public void remClient(String client) throws RemoteException {
        clientUI.removeClient(client);
    }
}
