package peertopeer.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import peertopeer.ClientConnection;
import peertopeer.ClientManager;
import peertopeer.ServerConnection;

import java.rmi.RemoteException;
import java.util.List;

public class PeerToPeerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ServerConnection serverConnection = new ServerConnection() {
            @Override
            public void register(String username, String hashedPassword) throws RemoteException {

            }

            @Override
            public boolean login(String username, String hashedPassword, ClientConnection clientConnection, ClientManager clientManager) throws RemoteException {
                return false;
            }

            @Override
            public void connect(String username, ClientConnection clientConnection, ClientManager clientManager) throws RemoteException {

            }

            @Override
            public void disconnect(String username) throws RemoteException {

            }

            @Override
            public ClientConnection getClient(String username) throws RemoteException {
                return null;
            }

            @Override
            public List<String> getOnlineClients() throws RemoteException {
                return List.of();
            }
        };

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/peertopeer/ui/view/LoginView.fxml"));

        primaryStage.setTitle("Peer-to-Peer Chat");
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.initialize(serverConnection);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
