package push;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {
	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(1099);

			ConnectionImpl connectionService = new ConnectionImpl();
			Naming.rebind("Connection", connectionService);

			System.out.println("Chat server is running...");
		} catch (RemoteException | MalformedURLException e) {
			System.err.println("Error starting the server: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
