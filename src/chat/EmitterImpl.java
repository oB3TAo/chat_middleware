package chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class EmitterImpl extends UnicastRemoteObject implements Emitter {

    private final String senderToken;
    private final Map<String, Receiver> activeClients;
    private final Map<String, String> tokenToUsernameMap;

    public EmitterImpl(String senderToken, Map<String, Receiver> activeClients, Map<String, String> tokenToUsernameMap) throws RemoteException {
        super();
        this.senderToken = senderToken;
        this.activeClients = activeClients;
        this.tokenToUsernameMap = tokenToUsernameMap;
    }

    @Override
    public void sendMessage(String recipient, String message) throws RemoteException {
        // Retrieve sender's username from their token
        String senderUsername = tokenToUsernameMap.get(senderToken);
        if (senderUsername == null) {
            throw new RemoteException("Invalid sender token.");
        }

        // Ensure the recipient exists
        Receiver recipientReceiver = activeClients.get(recipient);
        if (recipientReceiver != null) {
            // Format and send the message
            String formattedMessage = String.format("[%s]: %s", senderUsername, message);
            recipientReceiver.receiveMessage(formattedMessage);
        } else {
            throw new RemoteException("Recipient not found or unavailable.");
        }
    }
}
