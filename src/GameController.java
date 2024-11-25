    import java.awt.Point;
    import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

    public class GameController {
        private GameBoard gameBoard;
        private MainGame view;

        public GameController(GameBoard gameBoard, MainGame view) {
            this.gameBoard = gameBoard;
            this.view = view;
        }

        public GameController(GameBoard gameBoard2) {
            //TODO Auto-generated constructor stub
        }

        public void handleMovement(int playerIndex, String direction) {
            Player currentPlayer = gameBoard.getPlayers()[playerIndex]; // Get current player
            Point oldPosition = currentPlayer.getPosition();
        
            if (gameBoard.movePlayer(currentPlayer, direction)) { // If the move is valid
                Point newPosition = currentPlayer.getPosition();
                view.collectTokenIfPresent(playerIndex, newPosition); // Check for token collection
                view.updatePlayerPosition(playerIndex, newPosition); // Update player's position on the grid
            } else {
                System.out.println("Invalid move for Player " + (playerIndex + 1));
                view.showInvalidMoveDialog(); // Optional: show an invalid move dialog
            }
        }
        


        public void handleInsertClick(int row, int col) {
            String direction = getDirectionFromBorderCell(row, col);
            if (direction == null) {
                System.err.println("Invalid insert click position!");
                return;
            }
        
            System.out.println("Insert direction: " + direction);
        
            // Retrieve the current tile and its JLabel from the InsertPanel
            JPanel insertPanel = view.getInsertPanel();
            Tile currentInsertTile = (Tile) insertPanel.getClientProperty("currentTile");
            JLabel tileImageLabel = (JLabel) insertPanel.getClientProperty("tileImageLabel");
            
            // Pass the JLabel to the shifting logic
            JPanel gridPanel = view.getGridPanel();
            switch (direction) {
                case "down":
                    gameBoard.shiftColumnDown(col, gridPanel,insertPanel );
                    break;
                case "up":
                    gameBoard.shiftColumnUp(col, gridPanel, insertPanel);
                    break;
                case "right":
                    gameBoard.shiftRowRight(row, gridPanel, insertPanel);
                    break;
                case "left":
                    gameBoard.shiftRowLeft(row, gridPanel, insertPanel);
                    break;
                default:
                    System.err.println("Invalid direction: " + direction);
            }
        
            // // Debugging: Verify if the InsertPanel tile was updated correctly
            // System.out.println("Updated InsertPanel Tile: " + currentInsertTile.getImagePath());
        
            // Update the grid display
            // view.updateGrid(gameBoard);
        }
        


        private String getDirectionFromBorderCell(int row, int col) {
            if (row == 0) return "down";      // Top border
            if (row == 8) return "up";        // Bottom border
            if (col == 0) return "right";     // Left border
            if (col == 8) return "left";      // Right border
            return null; // Invalid border cell
        }

        // private void shiftTiles(String direction, int row, int col) {
        //     Tile newTile = view.getInsertPanelTile(); // Get the tile from InsertPanel
        //     if (newTile == null) {
        //         System.err.println("InsertPanel tile is null!");
        //         return;
        //     }
        
        //     Tile tempTile = null;
        
        //     System.out.println("Shifting tiles. Direction: " + direction + ", Row: " + row + ", Column: " + col);
        
        //     switch (direction) {
        //         case "down":
        //             tempTile = gameBoard.shiftColumnDown(col - 1, newTile); // Adjust col to map to playable grid
        //             break;
        
        //         case "up":
        //             tempTile = gameBoard.shiftColumnUp(col - 1, newTile); // Adjust col to map to playable grid
        //             break;
        
        //         case "left":
        //             tempTile = gameBoard.shiftRowLeft(row - 1, newTile); // Adjust row to map to playable grid
        //             break;
        
        //         case "right":
        //             tempTile = gameBoard.shiftRowRight(row - 1, newTile); // Adjust row to map to playable grid
        //             break;
        
        //         default:
        //             System.err.println("Invalid direction: " + direction);
        //             break;
        //     }
        
        //     if (tempTile != null) {
        //         System.out.println("Shift successful. Temp tile: " + tempTile);
        //         view.updateInsertPanelTile(tempTile); // Update InsertPanel with the shifted-out tile
        //     } else {
        //         System.err.println("Shift failed. Temp tile is null.");
        //     }
        // }
        
        
        
        
        
        

        
    }
