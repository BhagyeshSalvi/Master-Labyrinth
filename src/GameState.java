import java.awt.Point;
import java.io.Serializable;
import java.util.Map;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private Tile[][] tiles;
    private Player[] players;
    private int currentPlayerIndex;
    private Map<Point, String> tokenData; // Token positions to image paths
    private int assignedPlayerIndex; // Added for player role assignment

    public GameState(Tile[][] tiles, Player[] players, int currentPlayerIndex, Map<Point, String> tokenData, int assignedPlayerIndex) {
        this.tiles = tiles;
        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;
        this.tokenData = tokenData;
        this.assignedPlayerIndex = assignedPlayerIndex;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Map<Point, String> getTokenData() {
        return tokenData;
    }

    public void setTokenData(Map<Point, String> tokenData) {
        this.tokenData = tokenData;
    }

    public int getAssignedPlayerIndex() {
        return assignedPlayerIndex;
    }

    public void setAssignedPlayerIndex(int assignedPlayerIndex) {
        this.assignedPlayerIndex = assignedPlayerIndex;
    }
}
