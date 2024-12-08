
/**
 * Name - Bhagyesh Salvi
 * Number - 041103856
 * Java Application Programming UI Assignment
 * 
 * This class represents the game board, including tiles, players, and token placements.
 * It provides methods for managing tiles and their movements, player positions, and the game grid.
 */

import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GameBoard {
    private Tile[][] tiles; // 2D array representing the grid
    private int size; // Size of the grid (e.g., 9x9)
    private Player[] players; // Array of players in the game
    private MainGame view;
    private Map<Point, JLabel> tokenMap = new HashMap<>();

    /**
     * Constructs a GameBoard with the specified size and players.
     * Initializes the grid and populates it with tiles.
     * 
     * @param size    the size of the board (number of rows and columns)
     * @param players an array of Player objects participating in the game
     */
    public GameBoard(int size, Player[] players) {
        this.size = size;
        this.players = players;
        this.tiles = new Tile[size][size]; // Initialize grid
        initializeTiles();
        // System.out.println(getTile(4, 4));
    }

    /**
     * Constructs a GameBoard with the specified tile layout and players.
     * Used when synchronizing the game state between host and client.
     * 
     * @param tiles   a 2D array of Tile objects representing the board layout
     * @param players an array of Player objects participating in the game
     */
    public GameBoard(Tile[][] tiles, Player[] players) {
        this.size = tiles.length;
        this.players = players;
        this.tiles = tiles;
    }

    /**
     * Retrieves the map of tokens currently on the game board.
     * 
     * @return a Map where the keys are the positions (Point) of the tokens and
     *         the values are the corresponding JLabel objects representing the
     *         tokens.
     */
    public Map<Point, JLabel> getTokenMap() {
        return tokenMap;
    }

    /**
     * Adds a token to the game board at the specified position.
     * 
     * @param position   the position (Point) where the token will be added
     * @param tokenLabel the JLabel representing the token to be added
     */
    public void addToken(Point position, JLabel tokenLabel) {
        tokenMap.put(position, tokenLabel);
    }

    /**
     * Initializes the tiles on the game board with specific configurations.
     * 
     * This method populates the `tiles` array based on the board layout. It places:
     * - Insert tiles at the edges for interactive tile insertion.
     * - Randomized tiles with valid connections for the playable area (1-7
     * rows/columns).
     * - Placeholder tiles for empty or undefined areas.
     * 
     * The tiles are selected based on the frequencies defined in
     * {@link GameUtils#getTileFrequencies()}
     * and mapped to their types using {@link GameUtils#getTileTypeMapping()}.
     */
    private void initializeTiles() {
        Map<String, Integer> tileFrequencies = GameUtils.getTileFrequencies();
        Map<String, String> tileTypeMapping = GameUtils.getTileTypeMapping();
        List<String> availableTiles = new ArrayList<>(tileFrequencies.keySet());
        Collections.shuffle(availableTiles);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if ((row == 2 || row == 4 || row == 6) && (col == 0 || col == 8)) {
                    // Edge tiles for interactive insertion
                    String imagePath = (col == 0)
                            ? "Pictures/GridCell/insert_right.png"
                            : "Pictures/GridCell/insert_left.png";
                    tiles[row][col] = new Tile("insert", imagePath, row, col, false);
                } else if ((col == 2 || col == 4 || col == 6) && (row == 0 || row == 8)) {
                    // Edge tiles for interactive insertion
                    String imagePath = (row == 0)
                            ? "Pictures/GridCell/insert_down.png"
                            : "Pictures/GridCell/insert_up.png";
                    tiles[row][col] = new Tile("insert", imagePath, row, col, false);
                } else if (row >= 1 && row <= 7 && col >= 1 && col <= 7) {
                    // Playable tiles in the 7x7 grid
                    String selectedTile = getRandomTile(tileFrequencies, availableTiles);
                    if (selectedTile != null) {
                        tiles[row][col] = new Tile(tileTypeMapping.get(selectedTile), selectedTile, row, col, true);
                    } else {
                        tiles[row][col] = new Tile("placeholder", "Pictures/placeholder.png", row, col, true);
                    }
                } else {
                    // Non-interactive tiles
                    tiles[row][col] = new Tile("empty", "Pictures/placeholder.png", row, col, false);
                }

                // Debug log for each tile
                if (tiles[row][col] != null) {
                    System.out.println("Tile at (" + row + ", " + col + "): " +
                            "Type: " + tiles[row][col].getType() +
                            ", Connections: " + tiles[row][col].getConnections());
                }
            }
        }
    }

    /**
     * Randomly selects a tile from the available tiles based on their remaining
     * frequency.
     * 
     * This method ensures that tiles are selected in a randomized order, while
     * respecting
     * the defined frequencies in the `tileFrequencies` map. Each time a tile is
     * selected,
     * its frequency is decremented.
     * 
     * @param tileFrequencies a map containing the tile types as keys and their
     *                        remaining
     *                        frequencies as values
     * @param availableTiles  a list of available tile types to select from
     * @return the file path of the selected tile, or {@code null} if no tiles are
     *         available
     */
    private String getRandomTile(Map<String, Integer> tileFrequencies, List<String> availableTiles) {
        Collections.shuffle(availableTiles); // Randomize the selection order
        for (String tile : availableTiles) {
            int remaining = tileFrequencies.getOrDefault(tile, 0);
            if (remaining > 0) {
                tileFrequencies.put(tile, remaining - 1); // Decrement the count
                return tile; // Return the selected tile path
            }
        }
        return null; // No more tiles available
    }

    /**
     * Retrieves the tile located at the specified row and column on the game board.
     * 
     * This method checks if the requested position is within bounds and returns the
     * corresponding tile if available. If the position is out of bounds, it logs an
     * error and returns {@code null}.
     * 
     * @param row the row index of the requested tile (0-based)
     * @param col the column index of the requested tile (0-based)
     * @return the {@link Tile} object at the specified position, or {@code null} if
     *         the position is out of bounds or the tile does not exist
     */
    public Tile getTile(int row, int col) {
        if (row < 0 || row >= 9 || col < 0 || col >= 9) {
            System.out.println("Requested position (" + row + ", " + col + ") is out of bounds.");
            return null;
        }
        Tile tile = tiles[row][col];
        System.out.println("Accessed Tile at (" + row + ", " + col + "): " +
                (tile != null ? "Type: " + tile.getType() + ", Connections: " + tile.getConnections() : "null"));
        return tile;
    }

    // Getters
    /**
     * Returns the size of the game board (number of rows or columns).
     * 
     * @return the size of the game board
     */
    public int getSize() {
        return size;
    }

    /**
     * Retrieves the tile at the specified position on the game board.
     * 
     * @param position a {@link Point} object representing the row and column of the
     *                 tile
     * @return the {@link Tile} at the specified position
     */
    public Tile getTileAt(Point position) {
        return tiles[position.x][position.y];
    }

    /**
     * Returns the two-dimensional array of tiles representing the game board.
     * 
     * This method is used for serialization or synchronization of the game state.
     * 
     * @return a 2D array of {@link Tile} objects
     */
    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * Sets the two-dimensional array of tiles for the game board.
     * 
     * This method is used for deserialization or synchronization of the game state.
     * 
     * @param tiles a 2D array of {@link Tile} objects to set as the game board
     */
    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    /**
     * Returns the array of players currently in the game.
     * 
     * @return an array of {@link Player} objects
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Sets the array of players for the game.
     * 
     * This method is used for updating or synchronizing the player data.
     * 
     * @param players an array of {@link Player} objects to set
     */
    public void setPlayers(Player[] players) {
        this.players = players;
    }

    // Move the player on the grid
    /**
     * Moves the specified player in the given direction if the move is valid.
     * 
     * This method checks if the target position is within bounds, not blocked, and
     * accessible
     * based on the current and target tile connections. If the move is valid, it
     * updates the
     * player's position.
     * 
     * @param player    the {@link Player} to move
     * @param direction the direction to move ("up", "down", "left", or "right")
     * @return {@code true} if the player successfully moves, {@code false}
     *         otherwise
     */
    public boolean movePlayer(Player player, String direction) {
        Point currentPosition = player.getPosition();
        Point newPosition = new Point(currentPosition);

        switch (direction.toLowerCase()) {
            case "up":
                newPosition.translate(-1, 0);
                break;
            case "down":
                newPosition.translate(1, 0);
                break;
            case "left":
                newPosition.translate(0, -1);
                break;
            case "right":
                newPosition.translate(0, 1);
                break;
        }

        if (!isValidPosition(newPosition)) {
            System.out.println("Invalid move: Target out of bounds at " + newPosition);
            return false;
        }

        Tile currentTile = getTile(currentPosition.x, currentPosition.y);
        Tile targetTile = getTile(newPosition.x, newPosition.y);

        if (currentTile == null || targetTile == null) {
            System.out.println("Invalid move: One of the tiles is null");
            return false;
        }

        if (canMove(player, direction)) {
            System.out.println("Player moved from " + currentPosition + " to " + newPosition);
            player.setPosition(newPosition);
            return true;
        } else {
            System.out.println("Move blocked");
            return false;
        }
    }

    /**
     * Checks if the specified position is within the playable grid and not out of
     * bounds.
     * 
     * @param position a {@link Point} object representing the target position
     * @return {@code true} if the position is valid, {@code false} otherwise
     */
    public boolean isValidPosition(Point position) {
        return position.x >= 1 && position.x <= 7 &&
                position.y >= 1 && position.y <= 7;
    }

    /**
     * Checks if the specified tile is occupied by any player.
     * 
     * @param position a {@link Point} object representing the target position
     * @return {@code true} if the tile is occupied by a player, {@code false}
     *         otherwise
     */
    public boolean isTileOccupied(Point position) {
        for (Player player : players) { // Iterate through all players
            if (player.getPosition().equals(position)) {
                return true; // Tile is occupied by another player
            }
        }
        return false; // Tile is not occupied
    }

    /**
     * Updates the icon of a specific cell in the grid panel to reflect the given
     * tile's image.
     * 
     * This method retrieves the component at the specified row and column in the
     * grid panel,
     * verifies it is a {@link JLayeredPane}, and updates the icon of the first
     * {@link JLabel}
     * in the pane with the image of the specified tile.
     * 
     * @param gridPanel the {@link JPanel} representing the game grid
     * @param row       the row index of the cell to update
     * @param col       the column index of the cell to update
     * @param tile      the {@link Tile} whose image will be used for the cell, or
     *                  {@code null} to clear the cell's icon
     */
    private void updateCellIcon(JPanel gridPanel, int row, int col, Tile tile) {
        int cellIndex = row * 9 + col; // Calculate the cell index in the grid
        Component cellComponent = gridPanel.getComponent(cellIndex);

        if (cellComponent instanceof JLayeredPane) {
            JLayeredPane layeredPane = (JLayeredPane) cellComponent;
            JLabel label = (JLabel) layeredPane.getComponent(0); // Assuming the JLabel is the first component
            if (tile != null && tile.getImageIcon() != null) {
                label.setIcon(tile.getImageIcon());
            } else {
                label.setIcon(null); // Clear the icon if the Tile is null or has no image
            }
        }
    }

    /**
     * Updates the icon of the insert panel to reflect the given tile's image.
     * 
     * This method retrieves the first {@link JLabel} in the insert panel and
     * updates its icon
     * with the image of the specified tile. If the tile has no image or is
     * {@code null},
     * the icon is cleared.
     * 
     * @param insertPanel the {@link JPanel} representing the insert panel
     * @param tile        the {@link Tile} whose image will be used for the insert
     *                    panel, or {@code null} to clear the panel's icon
     */
    private void updateInsertPanelIcon(JPanel insertPanel, Tile tile) {
        JLabel insertPanelLabel = (JLabel) insertPanel.getComponent(0); // Assuming the JLabel is the first component
        if (tile != null && tile.getImagePath() != null) {
            tile.reloadImage(); // Ensure the image is loaded
            insertPanelLabel.setIcon(new ImageIcon(new ImageIcon(tile.getImagePath())
                    .getImage()
                    .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } else {
            insertPanelLabel.setIcon(null); // Clear the icon if the Tile is null or has no image
        }
    }

    /**
     * Shifts a column of tiles downwards in the grid.
     * 
     * This method moves the tiles in the specified column down by one position,
     * taking the bottommost tile and placing it in the insert panel. The new tile
     * from the insert panel is placed at the top of the column.
     * 
     * @param col         the column index to be shifted
     * @param gridPanel   the {@link JPanel} representing the game grid
     * @param insertPanel the {@link JPanel} representing the insert panel
     */
    public void shiftColumnDown(int col, JPanel gridPanel, JPanel insertPanel) {
        // Retrieve the current Tile from the InsertPanel
        Tile insertPanelTile = (Tile) insertPanel.getClientProperty("currentTile");

        if (insertPanelTile == null || insertPanelTile.getImagePath() == null) {
            System.err.println("Error: InsertPanel Tile is null or has no valid imagePath.");
            return;
        }

        // Save the last playable Tile (row 7)
        Tile lastTile = tiles[7][col];
        if (lastTile == null || lastTile.getImagePath() == null) {
            System.err.println("Error: Last Tile in the column is null or has no valid imagePath.");
            return;
        }

        // Shift rows 7 to 1 downwards
        for (int row = 7; row > 1; row--) {
            tiles[row][col] = tiles[row - 1][col]; // Move Tile in the array

            // Update the UI for the current cell
            updateCellIcon(gridPanel, row, col, tiles[row][col]);
        }

        // Update the first playable Tile (row 1) with the rotated InsertPanel Tile
        tiles[1][col] = insertPanelTile;

        // Update the UI for the first cell (row 1)
        updateCellIcon(gridPanel, 1, col, tiles[1][col]);

        // Update the InsertPanel with the last playable Tile
        insertPanel.putClientProperty("currentTile", lastTile);
        updateInsertPanelIcon(insertPanel, lastTile);

        // Refresh the grid
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Shifts a column of tiles upwards in the grid.
     * 
     * This method moves the tiles in the specified column up by one position,
     * taking the topmost tile and placing it in the insert panel. The new tile
     * from the insert panel is placed at the bottom of the column.
     * 
     * @param col         the column index to be shifted
     * @param gridPanel   the {@link JPanel} representing the game grid
     * @param insertPanel the {@link JPanel} representing the insert panel
     */
    public void shiftColumnUp(int col, JPanel gridPanel, JPanel insertPanel) {
        // Retrieve the current Tile from the InsertPanel
        Tile insertPanelTile = (Tile) insertPanel.getClientProperty("currentTile");

        if (insertPanelTile == null || insertPanelTile.getImagePath() == null) {
            System.err.println("Error: InsertPanel Tile is null or has no valid imagePath.");
            return;
        }

        // Save the first playable Tile (row 1)
        Tile firstTile = tiles[1][col];
        if (firstTile == null || firstTile.getImagePath() == null) {
            System.err.println("Error: First Tile in the column is null or has no valid imagePath.");
            return;
        }

        // Debugging: Log the InsertPanel Tile and the first Tile before shifting
        System.out.println("InsertPanel Tile before shift: " + insertPanelTile.getImagePath());
        System.out.println("First Tile before shift (to go into InsertPanel): " + firstTile.getImagePath());

        // Shift rows 1 to 7 upwards
        for (int row = 1; row < 7; row++) {
            tiles[row][col] = tiles[row + 1][col]; // Move Tile in the array
            updateCellIcon(gridPanel, row, col, tiles[row][col]); // Update UI
        }

        // Update the last playable row (row 7) with the InsertPanel Tile
        tiles[7][col] = insertPanelTile;
        updateCellIcon(gridPanel, 7, col, tiles[7][col]);

        // Update the InsertPanel with the first playable Tile (row 1)
        insertPanel.putClientProperty("currentTile", firstTile);
        updateInsertPanelIcon(insertPanel, firstTile);

        // Debugging: Log the updated InsertPanel Tile
        System.out.println("InsertPanel Tile after shift: " + firstTile.getImagePath());

        // Refresh the grid
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Shifts a row of tiles to the right in the grid.
     * 
     * This method moves the tiles in the specified row to the right by one
     * position,
     * taking the rightmost tile and placing it in the insert panel. The new tile
     * from the insert panel is placed at the leftmost position of the row.
     * 
     * @param row         the row index to be shifted
     * @param gridPanel   the {@link JPanel} representing the game grid
     * @param insertPanel the {@link JPanel} representing the insert panel
     */
    public void shiftRowRight(int row, JPanel gridPanel, JPanel insertPanel) {
        // Retrieve the current Tile from the InsertPanel
        Tile insertPanelTile = (Tile) insertPanel.getClientProperty("currentTile");

        if (insertPanelTile == null || insertPanelTile.getImagePath() == null) {
            System.err.println("Error: InsertPanel Tile is null or has no valid imagePath.");
            return;
        }

        // Save the last playable Tile (column 7)
        Tile lastTile = tiles[row][7];
        if (lastTile == null || lastTile.getImagePath() == null) {
            System.err.println("Error: Last Tile in the row is null or has no valid imagePath.");
            return;
        }

        // Debugging: Log the InsertPanel Tile and the last Tile before shifting
        System.out.println("InsertPanel Tile before shift: " + insertPanelTile.getImagePath());
        System.out.println("Last Tile before shift (to go into InsertPanel): " + lastTile.getImagePath());

        // Shift columns 7 to 1 to the right
        for (int col = 7; col > 1; col--) {
            tiles[row][col] = tiles[row][col - 1]; // Move Tile in the array
            updateCellIcon(gridPanel, row, col, tiles[row][col]); // Update UI
        }

        // Update the first playable column (column 1) with the InsertPanel Tile
        tiles[row][1] = insertPanelTile;
        updateCellIcon(gridPanel, row, 1, tiles[row][1]);

        // Update the InsertPanel with the last playable Tile (column 7)
        insertPanel.putClientProperty("currentTile", lastTile);
        updateInsertPanelIcon(insertPanel, lastTile);

        // Debugging: Log the updated InsertPanel Tile
        System.out.println("InsertPanel Tile after shift: " + lastTile.getImagePath());

        // Refresh the grid
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Shifts a row of tiles to the left in the grid.
     * 
     * This method moves the tiles in the specified row to the left by one position,
     * taking the leftmost tile and placing it in the insert panel. The new tile
     * from the insert panel is placed at the rightmost position of the row.
     * 
     * @param row         the row index to be shifted
     * @param gridPanel   the {@link JPanel} representing the game grid
     * @param insertPanel the {@link JPanel} representing the insert panel
     */
    public void shiftRowLeft(int row, JPanel gridPanel, JPanel insertPanel) {
        // Retrieve the current Tile from the InsertPanel
        Tile insertPanelTile = (Tile) insertPanel.getClientProperty("currentTile");

        if (insertPanelTile == null || insertPanelTile.getImagePath() == null) {
            System.err.println("Error: InsertPanel Tile is null or has no valid imagePath.");
            return;
        }

        // Save the first playable Tile (column 1)
        Tile firstTile = tiles[row][1];
        if (firstTile == null || firstTile.getImagePath() == null) {
            System.err.println("Error: First Tile in the row is null or has no valid imagePath.");
            return;
        }

        // Debugging: Log the InsertPanel Tile and the first Tile before shifting
        System.out.println("InsertPanel Tile before shift: " + insertPanelTile.getImagePath());
        System.out.println("First Tile before shift (to go into InsertPanel): " + firstTile.getImagePath());

        // Shift columns 1 to 7 to the left
        for (int col = 1; col < 7; col++) {
            tiles[row][col] = tiles[row][col + 1]; // Move Tile in the array
            updateCellIcon(gridPanel, row, col, tiles[row][col]); // Update UI
        }

        // Update the last playable column (column 7) with the InsertPanel Tile
        tiles[row][7] = insertPanelTile;
        updateCellIcon(gridPanel, row, 7, tiles[row][7]);

        // Update the InsertPanel with the first playable Tile (column 1)
        insertPanel.putClientProperty("currentTile", firstTile);
        updateInsertPanelIcon(insertPanel, firstTile);

        // Debugging: Log the updated InsertPanel Tile
        System.out.println("InsertPanel Tile after shift: " + firstTile.getImagePath());

        // Refresh the grid
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Determines if a player can move in the specified direction.
     * 
     * This method evaluates whether the movement from the player's current position
     * to the target position is valid based on the tile connections, grid
     * boundaries,
     * and the specified direction.
     * 
     * @param player    the {@link Player} attempting the move
     * @param direction the direction of movement ("up", "down", "left", "right")
     * @return {@code true} if the move is valid and allowed; {@code false}
     *         otherwise
     */
    public boolean canMove(Player player, String direction) {
        Point currentPosition = player.getPosition();
        Point targetPosition = new Point(currentPosition);

        // Determine the target position
        switch (direction.toLowerCase()) {
            case "up":
                targetPosition.translate(-1, 0);
                break;
            case "down":
                targetPosition.translate(1, 0);
                break;
            case "left":
                targetPosition.translate(0, -1);
                break;
            case "right":
                targetPosition.translate(0, 1);
                break;
        }

        System.out.println("Attempting to move from " + currentPosition + " to " + targetPosition);

        // Validate bounds
        if (!isValidPosition(targetPosition)) {
            System.out.println("Target position " + targetPosition + " is out of bounds.");
            return false;
        }

        // Fetch the current and target tiles
        Tile currentTile = getTile(currentPosition.x, currentPosition.y);
        Tile targetTile = getTile(targetPosition.x, targetPosition.y);

        if (currentTile == null || targetTile == null) {
            System.out.println("One of the tiles is null.");
            return false;
        }

        // Map movement direction to connection terms
        String normalizedDirection = mapDirection(direction);
        String reverseDirection = getReverseDirection(normalizedDirection);

        // Debugging
        System.out.println("Accessed Tile at (" + currentPosition.x + ", " + currentPosition.y + "): " +
                "Type: " + currentTile.getType() + ", Connections: " + currentTile.getConnections());
        System.out.println("Accessed Tile at (" + targetPosition.x + ", " + targetPosition.y + "): " +
                "Type: " + targetTile.getType() + ", Connections: " + targetTile.getConnections());
        System.out.println("Direction: " + normalizedDirection + ", Reverse Direction: " + reverseDirection);

        // Validate spatial alignment
        boolean currentTileValid = currentTile.getConnections().contains(normalizedDirection);
        boolean targetTileValid = targetTile.getConnections().contains(reverseDirection);

        System.out.println("Current Tile Valid: " + currentTileValid);
        System.out.println("Target Tile Valid: " + targetTileValid);

        if (currentTileValid && targetTileValid) {
            System.out.println("Move allowed!");
            return true;
        } else {
            System.out.println("Move blocked. Connections do not align.");
            return false;
        }
    }

    /**
     * Maps a directional input to its corresponding tile connection term.
     * 
     * @param direction the direction of movement ("up", "down", "left", "right")
     * @return the corresponding connection term ("north", "south", "west", "east"),
     *         or {@code null} if the direction is invalid
     */
    private String mapDirection(String direction) {
        switch (direction.toLowerCase()) {
            case "up":
                return "north";
            case "down":
                return "south";
            case "left":
                return "west";
            case "right":
                return "east";
            default:
                return null;
        }
    }

    /**
     * Gets the reverse of a given direction.
     * 
     * This method determines the opposite tile connection term for the specified
     * direction.
     * 
     * @param direction the connection term ("north", "south", "east", "west")
     * @return the reverse direction (e.g., "north" -> "south"), or {@code null} if
     *         the direction is invalid
     */
    private String getReverseDirection(String direction) {
        if (direction == null) {
            System.err.println("Error: Direction is null.");
            return null; // Log and return early for debugging purposes
        }

        switch (direction.toLowerCase()) {
            case "north":
                return "south";
            case "south":
                return "north";
            case "east":
                return "west";
            case "west":
                return "east";
            default:
                System.err.println("Error: Invalid direction - " + direction);
                return null;
        }
    }

    /**
     * Handles the insertion of a tile at the specified position and direction for
     * networking
     * 
     * This method manages tile insertion logic for networking scenarios by updating
     * the game grid
     * and UI based on the provided row, column, and direction of the insertion.
     * 
     * @param row         the row index where the tile is inserted
     * @param col         the column index where the tile is inserted
     * @param direction   the direction of the insertion ("down", "up", "right",
     *                    "left")
     * @param gridPanel   the {@link JPanel} representing the game grid
     * @param insertPanel the {@link JPanel} representing the insert tile panel
     */
    public void handleTileInsert(int row, int col, String direction, JPanel gridPanel, JPanel insertPanel) {
        switch (direction) {
            case "down":
                shiftColumnDown(col, gridPanel, insertPanel);
                break;
            case "up":
                shiftColumnUp(col, gridPanel, insertPanel);
                break;
            case "right":
                shiftRowRight(row, gridPanel, insertPanel);
                break;
            case "left":
                shiftRowLeft(row, gridPanel, insertPanel);
                break;
            default:
                System.err.println("Invalid tile insertion direction: " + direction);
        }
    }

}
