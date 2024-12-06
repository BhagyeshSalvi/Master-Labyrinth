import java.io.*;
import java.net.Socket;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Client {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int assignedPlayerIndex; // Player index assigned by the host

    public Client(String host, int port) throws IOException {
        this.clientSocket = new Socket(host, port);
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Client: Connected to server at " + host + ":" + port);
    }

    public GameState receiveGameState() {
        try {
            System.out.println("Client: Waiting to receive GameState...");
            Object message = in.readObject();
            if (message instanceof GameState) {
                GameState state = (GameState) message;
                System.out.println("Client: GameState received. Current Player Index: " + state.getCurrentPlayerIndex());
                return state;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Client: Error receiving GameState - " + e.getMessage());
        }
        return null;
    }
    
    public void listenForUpdates(GameController gameController, JTextArea chatArea) {
        new Thread(() -> {
            try {
                while (true) {
                    Object message = in.readObject();
    
                    // Handle GameState updates
                    if (message instanceof GameState) {
                        GameState state = (GameState) message;
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
                            SwingUtilities.invokeLater(() -> chatArea.append("Player "+chatMessage + "\n")); // Update UI
                            System.out.println("Client: Received chat message: " + chatMessage);
                            chatArea.revalidate();
                            chatArea.repaint();
                        } else {
                            System.out.println("Client: Received unknown string message: " + msg);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Client: Error listening for updates: " + e.getMessage());
            }
        }).start();
    }
    
    
    
    public void sendPlayerMove(int playerIndex, String direction) {
        try {
            out.writeObject("MOVE:" + playerIndex + ":" + direction);
            out.flush();
            System.out.println("Client: Sent move - Player " + (playerIndex + 1) + ", Direction: " + direction);
        } catch (IOException e) {
            System.err.println("Client: Error sending move - " + e.getMessage());
        }
    }
    
    

    public int getAssignedPlayerIndex() {
        return assignedPlayerIndex;
    }

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
    
    
    public void close() {
        try {
            clientSocket.close();
            System.out.println("Client: Connection closed.");
        } catch (IOException e) {
            System.err.println("Client: Error while closing - " + e.getMessage());
        }
    }
}
