import java.io.*;
import java.net.Socket;

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
    
    
    public void listenForUpdates(GameController controller) {
        new Thread(() -> {
            while (true) {
                GameState updatedState = receiveGameState();
                if (updatedState != null) {
                    controller.updateGameState(updatedState);
                }
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
            out.writeObject(message);
            out.flush();
            System.out.println("Client: Message sent - " + message);
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
