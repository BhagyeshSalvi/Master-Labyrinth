import java.awt.Point;

public class GameBoard {
    private Tile[][] tiles; // 2D array representing the grid
    private int size;       // Size of the grid (e.g., 9x9)
    private Player[] players; // Array of players in the game

    public GameBoard(int size, Player[] players) {
        this.size = size;
        this.players = players;
        this.tiles = new Tile[size][size]; // Initialize grid
    }

    // Getters
    public int getSize() {
        return size;
    }

    public Tile getTileAt(Point position) {
        return tiles[position.x][position.y];
    }

    public Player[] getPlayers() {
        return players;
    }

    // Place a tile at a specific position
    public void placeTile(Tile tile, Point position) {
        tiles[position.x][position.y] = tile;
    }

    // Check if a tile is movable
    // public boolean isTileMovable(Point position) {
    //     Tile tile = tiles[position.x][position.y];
    //     return tile != null && tile.isMovable();
    // }

    // Move the player on the grid
    public boolean movePlayer(Player player, String direction) {
        Point currentPosition = player.getPosition();
        Point newPosition = new Point(currentPosition);
    
        switch (direction.toLowerCase()) {
            case "up":
                newPosition.translate(-1, 0); // Move up
                break;
            case "down":
                newPosition.translate(1, 0); // Move down
                break;
            case "left":
                newPosition.translate(0, -1); // Move left
                break;
            case "right":
                newPosition.translate(0, 1); // Move right
                break;
        }
    
        // Debugging: Print newPosition and validation
        System.out.println("Attempting to move to: " + newPosition + 
                           " | Valid: " + isValidPosition(newPosition));
    
        // Validate the position
        if (isValidPosition(newPosition)) {
            player.setPosition(newPosition); // Update player's logical position
            return true;
        }
    
        return false; // Invalid move
    }
    
    

    // Validate if a position is within bounds and not blocked
    private boolean isValidPosition(Point position) {
        // Ensure the position is within the playable grid (2 to 8 for both rows and cols)
        return position.x >= 2 && position.x <= 8 &&
               position.y >= 2 && position.y <= 8;
    }
    
    
}
