
/**
 * Name - Bhagyesh Salvi
 * Number - 041103856
 * Java Application Programming UI Assignment
 * 
 * This class represents the game's current state, including tiles, players, and token data.
 * It is used for synchronizing the game between the host and clients.
 */

import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a GameState representing the current state of the game.
     * 
     * @param tiles               the 2D array of {@link Tile} objects representing
     *                            the game board
     * @param players             the array of {@link Player} objects participating
     *                            in the game
     * @param currentPlayerIndex  the index of the current player
     * @param tokenData           a map of {@link Point} positions to token image
     *                            paths
     * @param assignedPlayerIndex the index of the player assigned to this instance
     */
    private Tile[][] tiles;
    private Player[] players;
    private int currentPlayerIndex;
    private Map<Point, String> tokenData; // Token positions to image paths
    private int assignedPlayerIndex; // Added for player role assignment

    public GameState(Tile[][] tiles, Player[] players, int currentPlayerIndex, Map<Point, String> tokenData,
            int assignedPlayerIndex) {
        this.tiles = tiles;
        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;
        this.tokenData = new HashMap<>(tokenData);
        this.assignedPlayerIndex = assignedPlayerIndex;
    }

    /**
     * Retrieves the 2D array of {@link Tile} objects representing the game board.
     * 
     * @return the 2D array of {@link Tile} objects
     */
    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * Sets the 2D array of {@link Tile} objects representing the game board.
     * 
     * @param tiles the 2D array of {@link Tile} objects to set
     */
    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    /**
     * Retrieves the array of {@link Player} objects participating in the game.
     * 
     * @return the array of {@link Player} objects
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Sets the array of {@link Player} objects participating in the game.
     * 
     * @param players the array of {@link Player} objects to set
     */
    public void setPlayers(Player[] players) {
        this.players = players;
    }

    /**
     * Retrieves the index of the current player.
     * 
     * @return the index of the current player
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Sets the index of the current player.
     * 
     * @param currentPlayerIndex the index of the current player to set
     */
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    /**
     * Retrieves the map of token data, associating {@link Point} positions with
     * token image paths.
     * 
     * @return the map of token data
     */
    public Map<Point, String> getTokenData() {
        return tokenData;
    }

    /**
     * Sets the map of token data, associating {@link Point} positions with token
     * image paths.
     * 
     * @param tokenData the map of token data to set
     */
    public void setTokenData(Map<Point, String> tokenData) {
        this.tokenData = tokenData;
    }

    /**
     * Retrieves the index of the player assigned to this instance.
     * 
     * @return the assigned player index
     */
    public int getAssignedPlayerIndex() {
        return assignedPlayerIndex;
    }

    /**
     * Sets the index of the player assigned to this instance.
     * 
     * @param assignedPlayerIndex the assigned player index to set
     */
    public void setAssignedPlayerIndex(int assignedPlayerIndex) {
        this.assignedPlayerIndex = assignedPlayerIndex;
    }
}
