
/**
 * Name - Bhagyesh Salvi
 * Number - 041103856
 * Java Application Programming UI Assignment
 * 
 * This class represents the client in a networked game.
 * It handles server connections, listens for updates, and synchronizes the game state with the host.
 */

import java.io.*;
import java.net.Socket;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Client {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int assignedPlayerIndex; // Player index assigned by the host
    private MainGame view;

    /**
     * Constructs a new Client instance for connecting to a game host.
     * 
     * @param host the IP address or hostname of the server
     * @param port the port number of the server
     * @param view the {@link MainGame} instance representing the game view
     * @throws IOException if an I/O error occurs during connection setup
     */
    public Client(String host, int port, MainGame view) throws IOException {
        this.clientSocket = new Socket(host, port);
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(clientSocket.getInputStream());
        this.view = view;
        System.out.println("Client: Connected to server at " + host + ":" + port);
    }

    /**
     * Receives the initial game state from the server.
     * 
     * @return the {@link GameState} received from the host, or {@code null} if an
     *         error occurs
     */
    public GameState receiveGameState() {
        try {
            System.out.println("Client: Waiting to receive GameState...");
            Object message = in.readObject();
            if (message instanceof GameState) {
                GameState state = (GameState) message;
                System.out
                        .println("Client: GameState received. Current Player Index: " + state.getCurrentPlayerIndex());
                return state;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Client: Error receiving GameState - " + e.getMessage());
        }
        return null;
    }

    /**
     * Listens for updates from the host, including game state synchronization,
     * chat messages, and tile insert actions.
     * 
     * @param gameController the {@link GameController} managing game logic
     * @param chatArea       the {@link JTextArea} for displaying chat messages
     */
    public void listenForUpdates(GameController gameController, JTextArea chatArea) {
        new Thread(() -> {
            try {
                while (true) {
                    Object message = in.readObject();

                    // Handle GameState updates
                    if (message instanceof GameState) {
                        GameState state = (GameState) message;

                        // Initialize GameBoard if it is null
                        if (gameController.getGameBoard() == null) {
                            System.out.println("Client: Initializing GameBoard from received GameState.");
                            MainGame mainGame = gameController.getView();
                            mainGame.initializeFromGameState(state); // Ensure GameBoard and GameController are properly
                                                                     // set up
                        }

                        gameController.updateGameState(state);
                        System.out.println("Client: Received GameState update.");
                    }

                    // Handle chat messages
                    if (message instanceof String) {
                        String msg = (String) message;

                        // Ignore self-broadcasted messages
                        if (msg.contains("[FROM:CLIENT]")) {
                            System.out.println("Client: Ignoring own broadcasted message.");
                            continue;
                        }

                        if (msg.startsWith("[ID]#CHAT#")) {
                            String chatMessage = msg.substring(10); // Extract message content
                            SwingUtilities.invokeLater(() -> chatArea.append("Player " + chatMessage + "\n")); // Update
                                                                                                               // UI
                            System.out.println("Client: Received chat message: " + chatMessage);
                            chatArea.revalidate();
                            chatArea.repaint();
                        } else {
                            System.out.println("Client: Received unknown string message: " + msg);
                        }

                        if (msg.startsWith("[ID]#TILEINSERT#")) {
                            System.out.println("Client: Received TILEINSERT message: " + msg);

                            // Debugging: Extract and log the substring
                            String content = msg.substring(15);
                            System.out.println("TILEINSERT content: " + content);

                            try {
                                // Sanitize the content by removing any leading/trailing '#' or spaces
                                content = content.replaceAll("^#+", ""); // Remove leading '#'
                                String[] parts = content.split(":");

                                int row = Integer.parseInt(parts[0].trim());
                                int col = Integer.parseInt(parts[1].trim());
                                String direction = parts[2].trim();

                                System.out.println("Client: Parsed tile insertion: Row=" + row + ", Col=" + col
                                        + ", Direction=" + direction);
                                if (view.getGameBoard() == null) {
                                    System.err.println(
                                            "Error: GameBoard is null in Client. Tile insertion cannot proceed.");
                                    return;
                                }

                                // Perform the tile insertion on the client UI
                                SwingUtilities.invokeLater(() -> {
                                    view.getGameBoard().handleTileInsert(row, col, direction, view.getGridPanel(),
                                            view.getInsertPanel());
                                });
                            } catch (Exception e) {
                                System.err.println("Error parsing TILEINSERT message: " + e.getMessage());
                            }
                        }

                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Client: Error listening for updates: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Sends a move action to the host.
     * 
     * @param playerIndex the index of the player making the move
     * @param direction   the direction of the move (e.g., "up", "down", "left",
     *                    "right")
     */
    public void sendPlayerMove(int playerIndex, String direction) {
        try {
            out.writeObject("MOVE:" + playerIndex + ":" + direction);
            out.flush();
            System.out.println("Client: Sent move - Player " + (playerIndex + 1) + ", Direction: " + direction);
        } catch (IOException e) {
            System.err.println("Client: Error sending move - " + e.getMessage());
        }
    }

    /**
     * Returns the player index assigned by the host.
     * 
     * @return the assigned player index
     */
    public int getAssignedPlayerIndex() {
        return assignedPlayerIndex;
    }

    /**
     * Sends a message to the host.
     * 
     * @param message the message to be sent
     * @throws IOException if an I/O error occurs while sending the message
     */
    public void sendMessage(String message) throws IOException {
        if (out != null) {
            String modifiedMessage = message + " [FROM:CLIENT]";
            out.writeObject(modifiedMessage);
            out.flush();
            System.out.println("Client: Message sent - " + modifiedMessage);
        } else {
            System.err.println("Client: Output stream is not initialized.");
        }
    }

    /**
     * Closes the client connection to the host.
     */

    public void close() {
        try {
            clientSocket.close();
            System.out.println("Client: Connection closed.");
        } catch (IOException e) {
            System.err.println("Client: Error while closing - " + e.getMessage());
        }
    }
}
