
/**
 * Name - Bhagyesh Salvi
 * Number - 041103856
 * Java Application Programming UI Assignment
 * 
 * This class manages the networking aspects of the game.
 * It bridges communication between the host and clients and handles message broadcasting.
 */

import java.io.IOException;
import java.io.ObjectOutputStream;

public class NetworkManager {
    private Host host;
    private Client client;

    /**
     * Constructs a NetworkManager to manage communication between the host and
     * clients.
     * 
     * @param host   the {@link Host} instance for managing server-side operations,
     *               or {@code null} if the instance is a client
     * @param client the {@link Client} instance for connecting to the host, or
     *               {@code null} if the instance is a host
     */
    public NetworkManager(Host host, Client client) {
        this.host = host;
        this.client = client;
    }

    /**
     * Retrieves the {@link Client} instance managed by this NetworkManager.
     * 
     * @return the {@link Client} instance, or {@code null} if the instance is a
     *         host
     */
    public Client getClient() {
        return client;
    }

    /**
     * Retrieves the {@link Host} instance managed by this NetworkManager.
     * 
     * @return the {@link Host} instance, or {@code null} if the instance is a
     *         client
     */
    public Host getHost() {
        return host;
    }

    /**
     * Broadcasts the given {@link GameState} to all connected clients (host only).
     * 
     * @param gameState the {@link GameState} to be broadcasted
     */
    public void broadcastGameState(GameState gameState) {
        if (host != null) {
            for (ObjectOutputStream out : host.getOutputStreams()) {
                host.sendGameState(out, gameState); // Using the now-public method
            }
        } else {
            System.err.println("NetworkManager: Cannot broadcast without a host.");
        }
    }

    /**
     * Fetches the {@link GameState} from the host (client only).
     * 
     * @return the {@link GameState} fetched from the host, or {@code null} if an
     *         error occurs
     */
    public GameState fetchGameStateFromServer() {
        if (client != null) {
            return client.receiveGameState();
        } else {
            System.err.println("NetworkManager: No client connection.");
        }
        return null;
    }

    /**
     * Sends a player movement action to the host (client only).
     * 
     * @param playerIndex the index of the player making the move
     * @param direction   the direction of the move (e.g., "up", "down", "left",
     *                    "right")
     * @throws IOException if an I/O error occurs during message transmission
     */
    public void sendPlayerMove(int playerIndex, String direction) throws IOException {
        if (client != null) {
            client.sendMessage("MOVE:" + playerIndex + ":" + direction);
        } else {
            System.err.println("sendPlayerMove can only be used by the client.");
        }
    }

    /**
     * Broadcasts a chat message to all connected clients (host only).
     * 
     * @param message the chat message to be broadcasted
     */
    public void broadcastChatMessage(String message) {
        if (host != null) {
            for (ObjectOutputStream out : host.getOutputStreams()) {
                try {
                    out.writeObject(message);
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Error broadcasting chat message: " + e.getMessage());
                }
            }
            System.out.println("Host: Broadcasted chat message: " + message);
        } else {
            System.err.println("NetworkManager: Cannot broadcast chat without a host.");
        }
    }

    /**
     * Closes all network connections, including the host or client connection.
     */
    public void closeConnections() {
        if (host != null) {
            host.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
