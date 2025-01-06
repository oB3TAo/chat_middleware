package peertopeer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ChatServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);

            ServerConnectionImpl serverConnection = new ServerConnectionImpl();
            Naming.rebind("Server", serverConnection);

            System.out.println("Chat server is running...");
        } catch (RemoteException e) {
            System.err.println("RemoteException occurred while starting the server: " + e.getMessage());
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.err.println("MalformedURLException occurred: Invalid binding URL.");
            e.printStackTrace();
        }
    }
}
