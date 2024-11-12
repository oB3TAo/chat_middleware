package chat;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {
	private static Chat chat;

	public static void main(String[] args) {
		try {
			// Connect to the remote chat server
			chat = (Chat) Naming.lookup("rmi://localhost:1099/Chat");

			Scanner scanner = new Scanner(System.in);

			System.out.print("Enter your nickname: ");
			String pseudo = scanner.nextLine();
			chat.connect(pseudo);
			System.out.println("Connected as " + pseudo);

			// Start a thread to retrieve messages
			new Thread(() -> {
				try {
					while (true) {
						String[] messages = chat.getMessages(pseudo);
						for (String message : messages) {
							System.out.println(message);
						}
						Thread.sleep(1000); // Poll for new messages every second
					}
				} catch (RemoteException | InterruptedException e) {
					e.printStackTrace();
				}
			}).start();

			// Main loop for sending messages
			while (true) {
				System.out.print("To (nickname): ");
				String to = scanner.nextLine();
				System.out.print("Message: ");
				String message = scanner.nextLine();
				chat.sendMessage(pseudo, to, message);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

