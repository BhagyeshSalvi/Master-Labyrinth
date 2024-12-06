import java.awt.Point;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Host {
    private Host host;
    private MainGame view;
    private GameBoard gameBoard;
    private ServerSocket serverSocket;
    private GameController gameController;
    private List<Socket> connectedClients = new ArrayList<>();
    private List<ObjectOutputStream> outputStreams = new ArrayList<>();
    private int nextPlayerIndex = 1; // Start with Player 2 (Host is Player 1)

    public Host(int port,GameController gameController,GameBoard gameBoard,MainGame view) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setReuseAddress(true);
        this.gameController=gameController;
        this.gameBoard=gameBoard;
        this.view=view;

    }

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
                    0
                    );

                    clientGameState.setAssignedPlayerIndex(nextPlayerIndex);
                    nextPlayerIndex++; // Increment for the next client
                    sendGameState(out, clientGameState);

                    new Thread(new ClientHandler(clientSocket,this.gameController,this.gameBoard,this.view)).start();
                }
            } catch (IOException e) {
                System.err.println("Server: Error accepting clients - " + e.getMessage());
            }
        }).start();
    }

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
    

    public List<ObjectOutputStream> getOutputStreams() {
        return outputStreams;
    }

    public void broadcastGameState(GameState gameState) {
        if (host != null) {
            for (ObjectOutputStream out : host.getOutputStreams()) {
                host.sendGameState(out, gameState);
            }
        } else {
            System.err.println("NetworkManager: Cannot broadcast without a host.");
        }
    }
    
    

    

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

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private GameController gameController;
        private GameBoard gameBoard;
        private MainGame view;
    
        public ClientHandler(Socket clientSocket, GameController gameController, GameBoard gameBoard, MainGame view) {
            this.clientSocket = clientSocket;
            this.gameController = gameController;
            this.gameBoard = gameBoard;
            this.view = view;
        }
    
        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                while (true) {
                    Object message = in.readObject();
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
                                System.out.println("Invalid move: Player " + (playerIndex + 1) + " attempted to move out of turn!");
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
                                    view.getAssignedPlayerIndex()
                                );
                                view.getNetworkManager().broadcastGameState(updatedState);
                                System.out.println("Host: Broadcasting updated game state after move by Player " + (playerIndex + 1));
                            } else {
                                System.out.println("Host: Invalid move received from Player " + (playerIndex + 1));
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Host: Client disconnected - " + clientSocket.getInetAddress());
            }
        }
        

    }
    
}
