
/**
 * Name - Bhagyesh Salvi
 * Number - 041103856
 * Java Application Programming UI Assignment
 * 
 * This class represents the host in a networked game.
 * It manages server-side operations, including client connections and broadcasting messages.
 */

import java.awt.Point;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

public class Host {
    private Host host;
    private MainGame view;
    private GameBoard gameBoard;
    private ServerSocket serverSocket;
    private GameController gameController;
    private List<Socket> connectedClients = new ArrayList<>();
    private List<ObjectOutputStream> outputStreams = new ArrayList<>();
    private int nextPlayerIndex = 1; // Start with Player 2 (Host is Player 1)

    /**
     * Constructs a new Host instance.
     * 
     * @param port           the port number on which the server will listen for
     *                       connections
     * @param gameController the {@link GameController} managing game logic
     * @param gameBoard      the {@link GameBoard} representing the state of the
     *                       game
     * @param view           the {@link MainGame} view for updating the UI
     * @throws IOException if an I/O error occurs while creating the server socket
     */
    public Host(int port, GameController gameController, GameBoard gameBoard, MainGame view) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setReuseAddress(true);
        this.gameController = gameController;
        this.gameBoard = gameBoard;
        this.view = view;

    }

    /**
     * Returns the list of connected client sockets.
     * 
     * @return a {@link List} of {@link Socket} objects representing connected
     *         clients
     */
    public List<Socket> getConnectedClients() {
        return connectedClients;
    }

    /**
     * Starts the server to listen for incoming client connections.
     * 
     * @param initialState the initial {@link GameState} to send to newly connected
     *                     clients
     */
    public void startServer(GameState initialState) {
        System.out.println("Server: Starting on port " + serverSocket.getLocalPort());
        new Thread(() -> {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Server: Client connected - " + clientSocket.getInetAddress());

                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.flush();
                    connectedClients.add(clientSocket);
                    outputStreams.add(out);

                    GameState clientGameState = new GameState(
                            initialState.getTiles(),
                            initialState.getPlayers(),
                            initialState.getCurrentPlayerIndex(),
                            initialState.getTokenData(),
                            0);

                    clientGameState.setAssignedPlayerIndex(nextPlayerIndex);
                    nextPlayerIndex++; // Increment for the next client
                    sendGameState(out, clientGameState);

                    new Thread(new ClientHandler(clientSocket, this.gameController, this.gameBoard, this.view)).start();
                }
            } catch (IOException e) {
                System.err.println("Server: Error accepting clients - " + e.getMessage());
            }
        }).start();
    }

    /**
     * Sends the current game state to a specific client.
     * 
     * @param out       the {@link ObjectOutputStream} to send data to the client
     * @param gameState the {@link GameState} to send
     */
    public void sendGameState(ObjectOutputStream out, GameState gameState) {
        try {
            System.out.println("Host: Sending GameState...");
            out.reset();
            out.writeObject(gameState);
            out.flush();
            System.out.println("Host: GameState sent successfully.");
        } catch (IOException e) {
            System.err.println("Host: Error sending GameState - " + e.getMessage());
        }
    }

    /**
     * Returns the list of output streams for all connected clients.
     * 
     * @return a {@link List} of {@link ObjectOutputStream} objects for broadcasting
     *         messages
     */
    public List<ObjectOutputStream> getOutputStreams() {
        return outputStreams;
    }

    /**
     * Broadcasts the current game state to all connected clients.
     * 
     * @param gameState the {@link GameState} to broadcast
     */
    public void broadcastGameState(GameState gameState) {
        if (host != null) {
            for (ObjectOutputStream out : host.getOutputStreams()) {
                host.sendGameState(out, gameState);
            }
        } else {
            System.err.println("NetworkManager: Cannot broadcast without a host.");
        }
    }

    /**
     * Closes the server and disconnects all connected clients.
     */
    public void close() {
        try {
            for (Socket client : connectedClients) {
                client.close();
            }
            serverSocket.close();
            System.out.println("Server: Closed.");
        } catch (IOException e) {
            System.err.println("Server: Error while closing - " + e.getMessage());
        }
    }

    /**
     * Handles incoming messages from a client socket.
     */
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private GameController gameController;
        private GameBoard gameBoard;
        private MainGame view;

        /**
         * Constructs a new ClientHandler instance.
         * 
         * @param clientSocket   the {@link Socket} representing the client connection
         * @param gameController the {@link GameController} managing game logic
         * @param gameBoard      the {@link GameBoard} representing the state of the
         *                       game
         * @param view           the {@link MainGame} view for updating the UI
         */
        public ClientHandler(Socket clientSocket, GameController gameController, GameBoard gameBoard, MainGame view) {
            this.clientSocket = clientSocket;
            this.gameController = gameController;
            this.gameBoard = gameBoard;
            this.view = view;
        }

        /**
         * Processes messages from the client socket.
         * Handles game moves, chat messages, and tile insertions.
         */
        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                while (true) {
                    Object message = in.readObject();

                    // Handle move messages
                    if (message instanceof String) {
                        String msg = (String) message;

                        if (msg.startsWith("MOVE:")) {
                            String[] parts = msg.split(":");
                            int playerIndex = Integer.parseInt(parts[1]);
                            String direction = parts[2];

                            System.out.println("Host: Received move from Player " + (playerIndex + 1) +
                                    " in direction: " + direction);

                            // Validate turn
                            if (playerIndex != gameController.getCurrentPlayerIndex()) {
                                System.out.println("Invalid move: Player " + (playerIndex + 1)
                                        + " attempted to move out of turn!");
                                continue; // Ignore the move
                            }

                            // Process the move and check for token collection
                            Point oldPosition = gameBoard.getPlayers()[playerIndex].getPosition();
                            Point newPosition = new Point(oldPosition);
                            switch (direction.toLowerCase()) {
                                case "up" -> newPosition.translate(-1, 0);
                                case "down" -> newPosition.translate(1, 0);
                                case "left" -> newPosition.translate(0, -1);
                                case "right" -> newPosition.translate(0, 1);
                            }

                            if (gameBoard.isValidPosition(newPosition) &&
                                    gameBoard.canMove(gameBoard.getPlayers()[playerIndex], direction) &&
                                    !gameBoard.isTileOccupied(newPosition)) {

                                gameBoard.getPlayers()[playerIndex].setPosition(newPosition);
                                view.updatePlayerPosition(playerIndex, newPosition);

                                // Check for token collection
                                view.collectTokenIfPresent(playerIndex, newPosition);

                                // Broadcast updated game state
                                GameState updatedState = new GameState(
                                        gameBoard.getTiles(),
                                        gameBoard.getPlayers(),
                                        gameController.getCurrentPlayerIndex(),
                                        view.getTokenData(),
                                        view.getAssignedPlayerIndex());
                                view.getNetworkManager().broadcastGameState(updatedState);
                                System.out.println("Host: Broadcasting updated game state after move by Player "
                                        + (playerIndex + 1));
                            } else {
                                System.out.println("Host: Invalid move received from Player " + (playerIndex + 1));
                            }
                        }
                        // Handle chat messages
                        else if (msg.startsWith("[ID]#CHAT#")) {
                            System.out.println("Host: Received chat message: " + msg);

                            // Broadcast the chat message to all clients
                            if (view.getNetworkManager() != null) {
                                view.getNetworkManager().broadcastChatMessage(msg);
                            }

                            // Update the host UI
                            String chatMessage = msg.substring(10).replace("[FROM:CLIENT]", "").trim();
                            SwingUtilities.invokeLater(() -> {
                                if (view.getChatArea() != null) {
                                    view.getChatArea().append("Player " + chatMessage + "\n");
                                    view.getChatArea().setCaretPosition(view.getChatArea().getDocument().getLength());
                                }
                            });
                        } else if (msg.startsWith("[ID]#TILEINSERT#")) {
                            String[] parts = msg.substring(15).split(":");
                            int row = Integer.parseInt(parts[0]);
                            int col = Integer.parseInt(parts[1]);
                            String direction = parts[2];

                            System.out.println("Host: Received tile insertion: Row=" + row + ", Col=" + col
                                    + ", Direction=" + direction);

                            // Perform the tile insertion on the host UI
                            view.getGameBoard().handleTileInsert(row, col, direction, view.getGridPanel(),
                                    view.getInsertPanel());

                            // Broadcast the insertion to all clients
                            view.getNetworkManager().broadcastChatMessage(msg);
                        } else {
                            System.out.println("Host: Received unknown message type: " + msg);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Host: Client disconnected - " + clientSocket.getInetAddress());
            }
        }

    }

}
