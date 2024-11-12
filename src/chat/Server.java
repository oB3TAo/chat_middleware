package chat;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {
	public static void main(String[] args) {
		try {
			// Create and start the RMI registry
			LocateRegistry.createRegistry(1099);

			// Create the ChatImpl object and bind it to the registry
			ChatImpl chat = new ChatImpl();
			Naming.rebind("Chat", chat);

			System.out.println("Chat server is running...");
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
	}
}

