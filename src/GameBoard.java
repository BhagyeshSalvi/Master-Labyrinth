import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
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
    private int size;       // Size of the grid (e.g., 9x9)
    private Player[] players; // Array of players in the game
    private MainGame view;

    public GameBoard(int size, Player[] players) {
        this.size = size;
        this.players = players;
        this.tiles = new Tile[size][size]; // Initialize grid
        List<String> imageList = GameUtils.generateImageList();
        initializeTiles(imageList);
        
    
    }

    public Tile getTile(int row, int col) {
        if (row < 0 || row >= 9 || col < 0 || col >= 9) return null; // Bounds check
        return tiles[row][col];
    }
    
    private Map<Point, JLabel> tokenMap = new HashMap<>();

    public Map<Point, JLabel> getTokenMap() {
        return tokenMap;
    }

    public void addToken(Point position, JLabel tokenLabel) {
        tokenMap.put(position, tokenLabel);
    }

    private void initializeTiles(List<String> imageList) {
        Collections.shuffle(imageList); // Shuffle for random placement
        int imageIndex = 0;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if ((row == 2 || row == 4 || row == 6) && (col == 0 || col == 8)) {
                    String imagePath = (col == 0)
                        ? "Pictures/GridCell/insert_right.png"
                        : "Pictures/GridCell/insert_left.png";
                    tiles[row][col] = new Tile(imagePath, row, col, false);
                } else if ((col == 2 || col == 4 || col == 6) && (row == 0 || row == 8)) {
                    String imagePath = (row == 0)
                        ? "Pictures/GridCell/insert_down.png"
                        : "Pictures/GridCell/insert_up.png";
                    tiles[row][col] = new Tile(imagePath, row, col, false);
                } else if (row >= 1 && row <= 7 && col >= 1 && col <= 7) {
                    if (imageIndex < imageList.size()) {
                        tiles[row][col] = new Tile(imageList.get(imageIndex), row, col, true);
                        imageIndex++;
                    } else {
                        tiles[row][col] = new Tile("Pictures/placeholder.png", row, col, true);
                    }
                } else {
                    tiles[row][col] = null; // Empty corners
                }
            }
        }
        
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
        if (isValidPosition(newPosition) && !isTileOccupied(newPosition)) {
            player.setPosition(newPosition); // Update player's logical position
            return true; // Valid move
        }
    
        return false; // Invalid move
    }
    
    

    // Validate if a position is within bounds and not blocked
    private boolean isValidPosition(Point position) {
        // Ensure the position is within the playable grid (2 to 8 for both rows and cols)
        return position.x >= 2 && position.x <= 8 &&
               position.y >= 2 && position.y <= 8;
    }
    
    public boolean isTileOccupied(Point position) {
        for (Player player : players) { // Iterate through all players
            if (player.getPosition().equals(position)) {
                return true; // Tile is occupied by another player
            }
        }
        return false; // Tile is not occupied
    }

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
    
    
    
}
