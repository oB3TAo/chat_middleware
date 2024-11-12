package chat.component;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionComponent {

    // Map to store each client token and their associated dialog component
    private final Map<String, DialogComponent> dialogs = new HashMap<>();

    /**
     * Called when a new client connects. Creates a DialogComponent for the client.
     *
     * @param token Unique token for the client session
     * @param username Username of the client
     * @return The DialogComponent for the client
     */
    public DialogComponent createDialog(String token, String username) {
        DialogComponent dialog = new DialogComponent(token, username);
        dialogs.put(token, dialog); // Store dialog in map by token
        System.out.println("Dialog component created for user: " + username);
        return dialog;
    }

    /**
     * Retrieve an existing DialogComponent for a connected client by their token.
     *
     * @param token The client's unique token
     * @return The DialogComponent instance, or null if not found
     */
    public DialogComponent getDialog(String token) {
        return dialogs.get(token);
    }

    /**
     * Disconnect a client by removing their DialogComponent.
     *
     * @param token The client's unique token
     */
    public void removeDialog(String token) {
        dialogs.remove(token);
        System.out.println("Dialog component removed for token: " + token);
    }
}
