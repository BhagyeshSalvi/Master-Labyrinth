    import java.awt.Point;
    import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

    public class GameController {
        private GameBoard gameBoard;
        private MainGame view;
        private int currentPlayerIndex=0; // Start with Player 1 (index 0)


        public GameController(GameBoard gameBoard, MainGame view) {
            this.gameBoard = gameBoard;
            this.view = view;
        }

        public GameController(GameBoard gameBoard2) {
            //TODO Auto-generated constructor stub
        }

        public int getCurrentPlayerIndex() {
           // System.out.println("Returning Current Player Index: " + currentPlayerIndex); // Debug log
            return currentPlayerIndex;
        }
        


        public void nextTurn() {
            currentPlayerIndex = (currentPlayerIndex + 1) % gameBoard.getPlayers().length;
            System.out.println("It's now Player " + (currentPlayerIndex + 1) + "'s turn.");
            view.updateTurnLabel(currentPlayerIndex);
            view.updateTurnIndicator(currentPlayerIndex);
            
        }
        


        public void handleMovement(int playerIndex, String direction) {
            if (playerIndex != currentPlayerIndex) {
                System.out.println("Invalid action: It's not Player " + (playerIndex + 1) + "'s turn!");
                return; // Ignore movement if it's not this player's turn
            }
            System.out.println("Handling movement for Player " + (playerIndex+1) + " in direction: " + direction);
        
            Player currentPlayer = gameBoard.getPlayers()[getCurrentPlayerIndex()];
            Point oldPosition = currentPlayer.getPosition();
            Point newPosition = new Point(oldPosition);
        
            // Update position based on direction
            switch (direction.toLowerCase()) {
                case "up": newPosition.translate(-1, 0); break;
                case "down": newPosition.translate(1, 0); break;
                case "left": newPosition.translate(0, -1); break;
                case "right": newPosition.translate(0, 1); break;
            }
        
            System.out.println("Attempting to move to: " + newPosition);
        
            // Validate new position
            if (gameBoard.isValidPosition(newPosition) &&
                gameBoard.canMove(currentPlayer, direction) &&
                !gameBoard.isTileOccupied(newPosition)) {
        
                currentPlayer.setPosition(newPosition);
                view.updatePlayerPosition(playerIndex, newPosition);
        
                // Check for tokens
                view.collectTokenIfPresent(playerIndex, newPosition);
        
                System.out.println("Player moved logically to: " + newPosition);
                
            } else {
                System.out.println("Invalid move: Tile occupied, out of bounds, or connections invalid");
                view.showInvalidMoveDialog();
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
        
           
        }
        


        private String getDirectionFromBorderCell(int row, int col) {
            if (row == 0) return "down";      // Top border
            if (row == 8) return "up";        // Bottom border
            if (col == 0) return "right";     // Left border
            if (col == 8) return "left";      // Right border
            return null; // Invalid border cell
        }

         
    }
