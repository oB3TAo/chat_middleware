package chat.component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DialogComponent {

    private final String token;
    private final String username;
    private final List<String> messageHistory;

    public DialogComponent(String token, String username) {
        this.token = token;
        this.username = username;
        this.messageHistory = new CopyOnWriteArrayList<>();
    }

    /**
     * Adds a message to the user's message history.
     *
     * @param message The message to add
     */
    public void addMessage(String message) {
        messageHistory.add(message);
    }

    /**
     * Returns the message history for this user.
     *
     * @return List of messages
     */
    public List<String> getMessageHistory() {
        return new CopyOnWriteArrayList<>(messageHistory); // Return a copy for safety
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }
}
