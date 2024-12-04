import java.io.IOException;
import java.io.ObjectOutputStream;

public class NetworkManager {
    private Host host;
    private Client client;

    public NetworkManager(Host host, Client client) {
        this.host = host;
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public Host getHost() {
        return host;
    }
    // Broadcast GameState to all clients (host only)
    public void broadcastGameState(GameState gameState) {
        if (host != null) {
            for (ObjectOutputStream out : host.getOutputStreams()) {
                host.sendGameState(out, gameState); // Using the now-public method
            }
        } else {
            System.err.println("NetworkManager: Cannot broadcast without a host.");
        }
    }

    // Fetch GameState from server (client only)
    public GameState fetchGameStateFromServer() {
        if (client != null) {
            return client.receiveGameState();
        } else {
            System.err.println("NetworkManager: No client connection.");
        }
        return null;
    }

    public void sendPlayerMove(int playerIndex, String direction) throws IOException {
        if (client != null) {
            client.sendMessage("MOVE:" + playerIndex + ":" + direction);
        } else {
            System.err.println("sendPlayerMove can only be used by the client.");
        }
    }

    // Close all network connections
    public void closeConnections() {
        if (host != null) {
            host.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
