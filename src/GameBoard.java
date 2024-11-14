import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.util.Collections;
import java.util.List;

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

    public void shiftColumnDown(int col, JPanel gridPanel, JLabel insertPanelLabel) {
        ImageIcon tempIcon = null; // To store the icon of the last playable cell (row 7)
    
        // Save the last playable cell's icon for the InsertPanel
        int lastPlayableIndex = 7 * 9 + col; // Last playable cell index (row 7)
        Component lastPlayableComponent = gridPanel.getComponent(lastPlayableIndex);
        if (lastPlayableComponent instanceof JLayeredPane) {
            JLayeredPane lastPlayablePane = (JLayeredPane) lastPlayableComponent;
            JLabel lastPlayableLabel = (JLabel) lastPlayablePane.getComponent(0);
            tempIcon = (ImageIcon) lastPlayableLabel.getIcon(); // Save the icon
            System.out.println("Last playable cell's icon: " + tempIcon); // Debugging log
        }
    
        // Shift the 7 playable cells down (rows 7 to 1)
        for (int row = 7; row > 0; row--) {
            int currentIndex = row * 9 + col; // Current cell index
            int aboveIndex = (row - 1) * 9 + col; // Cell above the current one
            Component currentComponent = gridPanel.getComponent(currentIndex);
            Component aboveComponent = gridPanel.getComponent(aboveIndex);
    
            if (currentComponent instanceof JLayeredPane && aboveComponent instanceof JLayeredPane) {
                JLabel currentLabel = (JLabel) ((JLayeredPane) currentComponent).getComponent(0);
                JLabel aboveLabel = (JLabel) ((JLayeredPane) aboveComponent).getComponent(0);
    
                currentLabel.setIcon(aboveLabel.getIcon()); // Shift the icon down
                System.out.println("Shifted icon from row " + (row - 1) + " to row " + row); // Debugging log
            }
        }
    
        // Place the InsertPanel's tile into the second-row cell (row 1)
        int secondRowIndex = 1 * 9 + col; // Second row index (row 1)
        Component secondRowComponent = gridPanel.getComponent(secondRowIndex);
        if (secondRowComponent instanceof JLayeredPane) {
            JLayeredPane secondRowPane = (JLayeredPane) secondRowComponent;
            JLabel secondRowLabel = (JLabel) secondRowPane.getComponent(0);
    
            if (insertPanelLabel.getIcon() != null) {
                secondRowLabel.setIcon(insertPanelLabel.getIcon()); // Set the InsertPanel's tile icon
                System.out.println("Second row updated with InsertPanel tile icon: " + insertPanelLabel.getIcon());
            } else {
                System.err.println("Error: InsertPanel tile icon is null. Using placeholder image.");
    
                // Create a scaled placeholder image
                ImageIcon placeholderIcon = new ImageIcon(new ImageIcon("Pictures/GridCell/hallway_horiz.png")
                    .getImage()
                    .getScaledInstance(60, 60, Image.SCALE_SMOOTH)); // Scale to 60x60
    
                secondRowLabel.setIcon(placeholderIcon); // Set the scaled placeholder image
            }
        } else {
            System.err.println("Second row cell (row 1) is not a valid JLayeredPane.");
        }
    
        // Update the InsertPanel with the last playable cell's icon
        if (tempIcon != null) {
            insertPanelLabel.setIcon(tempIcon); // Update the InsertPanel with the last cell's icon
            System.out.println("InsertPanel updated with last playable cell's icon: " + tempIcon);
        } else {
            System.err.println("Error: Last playable cell's icon is null.");
        }
    
        // Refresh the grid
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    


    
    
    
    
    public void shiftColumnUp(int col, JPanel gridPanel, JLabel insertPanelLabel) {
        ImageIcon tempIcon = null; // To store the icon of the first playable cell (row 1)
    
        // Save the first playable cell's icon for the InsertPanel
        int firstPlayableIndex = 1 * 9 + col; // First playable cell index (row 1)
        Component firstPlayableComponent = gridPanel.getComponent(firstPlayableIndex);
        if (firstPlayableComponent instanceof JLayeredPane) {
            JLayeredPane firstPlayablePane = (JLayeredPane) firstPlayableComponent;
            JLabel firstPlayableLabel = (JLabel) firstPlayablePane.getComponent(0);
            tempIcon = (ImageIcon) firstPlayableLabel.getIcon(); // Save the icon
        }
    
        if (tempIcon == null) {
            System.err.println("First playable cell's icon is null. Using placeholder image.");
            tempIcon = new ImageIcon(new ImageIcon("Pictures/GridCell/hallway_horiz.png")
                .getImage()
                .getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        }
    
        // Shift cells upwards (rows 1 to 7)
        for (int row = 1; row < 7; row++) {
            int currentIndex = row * 9 + col;
            int belowIndex = (row + 1) * 9 + col;
    
            Component currentComponent = gridPanel.getComponent(currentIndex);
            Component belowComponent = gridPanel.getComponent(belowIndex);
    
            if (currentComponent instanceof JLayeredPane && belowComponent instanceof JLayeredPane) {
                JLabel currentLabel = (JLabel) ((JLayeredPane) currentComponent).getComponent(0);
                JLabel belowLabel = (JLabel) ((JLayeredPane) belowComponent).getComponent(0);
    
                if (belowLabel.getIcon() == null) {
                    System.err.println("Below cell's icon is null. Assigning placeholder image.");
                    belowLabel.setIcon(new ImageIcon(new ImageIcon("Pictures/GridCell/hallway_horiz.png")
                        .getImage()
                        .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
                }
    
                currentLabel.setIcon(belowLabel.getIcon()); // Shift the icon up
            }
        }
    
        // Place the InsertPanel's tile into the last playable cell (row 7)
        int lastPlayableIndex = 7 * 9 + col; // Last playable cell index (row 7)
        Component lastPlayableComponent = gridPanel.getComponent(lastPlayableIndex);
        if (lastPlayableComponent instanceof JLayeredPane) {
            JLayeredPane lastPlayablePane = (JLayeredPane) lastPlayableComponent;
            JLabel lastPlayableLabel = (JLabel) lastPlayablePane.getComponent(0);
    
            if (insertPanelLabel.getIcon() != null) {
                lastPlayableLabel.setIcon(insertPanelLabel.getIcon());
            } else {
                System.err.println("InsertPanel tile icon is null. Using placeholder image.");
                lastPlayableLabel.setIcon(new ImageIcon(new ImageIcon("Pictures/GridCell/hallway_horiz.png")
                    .getImage()
                    .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
            }
        }
    
        // Update the InsertPanel with the first playable cell's icon
        insertPanelLabel.setIcon(tempIcon);
    
        // Refresh the grid
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    
    
    
    public void shiftRowRight(int row, JPanel gridPanel, JLabel insertPanelLabel) {
        ImageIcon tempIcon = null; // To store the icon of the last playable cell (column 7)
    
        // Save the last playable cell's icon for the InsertPanel
        int lastPlayableIndex = row * 9 + 7; // Last playable cell index (column 7)
        Component lastPlayableComponent = gridPanel.getComponent(lastPlayableIndex);
        if (lastPlayableComponent instanceof JLayeredPane) {
            JLayeredPane lastPlayablePane = (JLayeredPane) lastPlayableComponent;
            JLabel lastPlayableLabel = (JLabel) lastPlayablePane.getComponent(0);
            tempIcon = (ImageIcon) lastPlayableLabel.getIcon(); // Save the icon
            System.out.println("Last playable cell's icon: " + tempIcon); // Debugging log
        }
    
        if (tempIcon == null) {
            System.err.println("Last playable cell's icon is null. Using placeholder image.");
            tempIcon = new ImageIcon(new ImageIcon("Pictures/GridCell/hallway_horiz.png")
                .getImage()
                .getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        }
    
        // Shift cells in the row to the right (columns 7 to 1)
        for (int col = 7; col > 0; col--) {
            int currentIndex = row * 9 + col;
            int leftIndex = row * 9 + (col - 1);
    
            Component currentComponent = gridPanel.getComponent(currentIndex);
            Component leftComponent = gridPanel.getComponent(leftIndex);
    
            if (currentComponent instanceof JLayeredPane && leftComponent instanceof JLayeredPane) {
                JLabel currentLabel = (JLabel) ((JLayeredPane) currentComponent).getComponent(0);
                JLabel leftLabel = (JLabel) ((JLayeredPane) leftComponent).getComponent(0);
    
                if (leftLabel.getIcon() == null) {
                    System.err.println("Left cell's icon is null. Assigning placeholder image.");
                    leftLabel.setIcon(new ImageIcon(new ImageIcon("Pictures/GridCell/hallway_horiz.png")
                        .getImage()
                        .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
                }
    
                currentLabel.setIcon(leftLabel.getIcon()); // Shift the icon to the right
            }
        }
    
        // Place the InsertPanel's tile into the first playable cell (column 1)
        int firstPlayableIndex = row * 9 + 1; // First playable cell index (column 1)
        Component firstPlayableComponent = gridPanel.getComponent(firstPlayableIndex);
        if (firstPlayableComponent instanceof JLayeredPane) {
            JLayeredPane firstPlayablePane = (JLayeredPane) firstPlayableComponent;
            JLabel firstPlayableLabel = (JLabel) firstPlayablePane.getComponent(0);
    
            if (insertPanelLabel.getIcon() != null) {
                firstPlayableLabel.setIcon(insertPanelLabel.getIcon()); // Set the InsertPanel's tile icon
            } else {
                System.err.println("InsertPanel tile icon is null. Using placeholder image.");
                firstPlayableLabel.setIcon(new ImageIcon(new ImageIcon("Pictures/GridCell/hallway_horiz.png")
                    .getImage()
                    .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
            }
        }
    
        // Update the InsertPanel with the last playable cell's icon
        insertPanelLabel.setIcon(tempIcon);
    
        // Refresh the grid
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    
    public void shiftRowLeft(int row, JPanel gridPanel, JLabel insertPanelLabel) {
        ImageIcon tempIcon = null; // To store the icon of the first playable cell (column 1)
    
        // Save the first playable cell's icon for the InsertPanel
        int firstPlayableIndex = row * 9 + 1; // First playable cell index (column 1)
        Component firstPlayableComponent = gridPanel.getComponent(firstPlayableIndex);
        if (firstPlayableComponent instanceof JLayeredPane) {
            JLayeredPane firstPlayablePane = (JLayeredPane) firstPlayableComponent;
            JLabel firstPlayableLabel = (JLabel) firstPlayablePane.getComponent(0);
            tempIcon = (ImageIcon) firstPlayableLabel.getIcon(); // Save the icon
            System.out.println("First playable cell's icon: " + tempIcon); // Debugging log
        }
    
        if (tempIcon == null) {
            System.err.println("First playable cell's icon is null. Using placeholder image.");
            tempIcon = new ImageIcon(new ImageIcon("Pictures/GridCell/hallway_horiz.png")
                .getImage()
                .getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        }
    
        // Shift cells in the row to the left (columns 1 to 7)
        for (int col = 1; col < 8; col++) {
            int currentIndex = row * 9 + col;
            int rightIndex = row * 9 + (col + 1);
    
            Component currentComponent = gridPanel.getComponent(currentIndex);
            Component rightComponent = gridPanel.getComponent(rightIndex);
    
            if (currentComponent instanceof JLayeredPane && rightComponent instanceof JLayeredPane) {
                JLabel currentLabel = (JLabel) ((JLayeredPane) currentComponent).getComponent(0);
                JLabel rightLabel = (JLabel) ((JLayeredPane) rightComponent).getComponent(0);
    
                if (rightLabel.getIcon() == null) {
                    System.err.println("Right cell's icon is null. Assigning placeholder image.");
                    rightLabel.setIcon(new ImageIcon(new ImageIcon("Pictures/GridCell/hallway_horiz.png")
                        .getImage()
                        .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
                }
    
                currentLabel.setIcon(rightLabel.getIcon()); // Shift the icon to the left
            }
        }
    
        // Place the InsertPanel's tile into the last playable cell (column 7)
        int lastPlayableIndex = row * 9 + 7; // Last playable cell index (column 7)
        Component lastPlayableComponent = gridPanel.getComponent(lastPlayableIndex);
        if (lastPlayableComponent instanceof JLayeredPane) {
            JLayeredPane lastPlayablePane = (JLayeredPane) lastPlayableComponent;
            JLabel lastPlayableLabel = (JLabel) lastPlayablePane.getComponent(0);
    
            if (insertPanelLabel.getIcon() != null) {
                lastPlayableLabel.setIcon(insertPanelLabel.getIcon()); // Set the InsertPanel's tile icon
            } else {
                System.err.println("InsertPanel tile icon is null. Using placeholder image.");
                lastPlayableLabel.setIcon(new ImageIcon(new ImageIcon("Pictures/GridCell/hallway_horiz.png")
                    .getImage()
                    .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
            }
        }
    
        // Update the InsertPanel with the first playable cell's icon
        insertPanelLabel.setIcon(tempIcon);
    
        // Refresh the grid
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    

    
    
    
    
    
    
}
