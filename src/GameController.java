    import java.awt.Point;
import java.io.IOException;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

    public class GameController {
        private GameBoard gameBoard;
        private MainGame view;
        private int currentPlayerIndex; // Start with Player 1 (index 0)
        private int assignedPlayerIndex;
        
        
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
        
                public void setCurrentPlayerIndex(int index) {
                    this.currentPlayerIndex = index;
                }

                
                
        
        
                public void nextTurn() {
                    currentPlayerIndex = (currentPlayerIndex + 1) % gameBoard.getPlayers().length;
                    System.out.println("It's now Player " + (currentPlayerIndex + 1) + "'s turn.");
                
                    // Notify the view to update UI
                    view.updateTurnLabel(currentPlayerIndex);
                    view.updateTurnIndicator(currentPlayerIndex);
                
                    // Broadcast updated game state if host
                    if (view.isHost() && view.getNetworkManager() != null) {
                        GameState updatedState = new GameState(
                            gameBoard.getTiles(),
                            gameBoard.getPlayers(),
                            currentPlayerIndex,
                            view.getTokenData(),
                            view.getAssignedPlayerIndex()
                        );
                        System.out.println("Server: Sending GameState...");
                        view.getNetworkManager().broadcastGameState(updatedState);
                    }
                }
                

                public void updateGameState(GameState state) {
                    gameBoard.setTiles(state.getTiles());
                    gameBoard.setPlayers(state.getPlayers());
                    currentPlayerIndex = state.getCurrentPlayerIndex();
                    System.out.println("GameController: GameState applied. Current Player Index: " + currentPlayerIndex);
                
                    Player[] players = state.getPlayers();
                    for (int i = 0; i < players.length; i++) {
                        view.updatePlayerStars(i, players[i].getStarsCollected());
                    }

                    for (int i = 0; i < gameBoard.getPlayers().length; i++) {
                        Player player = gameBoard.getPlayers()[i];
                        view.updatePlayerPosition(i, player.getPosition()); // Update each player's position
                    }
                    // Notify the view to update UI
                    view.updateTurnLabel(currentPlayerIndex);
                    view.updateTurnIndicator(currentPlayerIndex);
                
                    // Enable/disable movement based on the current turn
                    if (view.getAssignedPlayerIndex() == currentPlayerIndex) {
                        System.out.println("GameController: It's your turn. Assigned Player Index: " + view.getAssignedPlayerIndex());
                    } else {
                        System.out.println("GameController: Waiting for Player " + (currentPlayerIndex + 1) + "'s move.");
                    }
                }
                
                
                
                
                
        
                public void handleMovement(int playerIndex, String direction) throws IOException {
                    // Validate that the action is for the correct player and turn
                    if (!view.canHostAct(playerIndex, currentPlayerIndex, view.isHost())) {
                        System.out.println("Invalid move: Player " + (playerIndex + 1) + " is trying to move out of turn!");
                        return;
                    }
                
                    System.out.println("Handling movement for Player " + (playerIndex + 1) + " in direction: " + direction);
                
                    Player currentPlayer = gameBoard.getPlayers()[currentPlayerIndex];
                    Point oldPosition = currentPlayer.getPosition();
                    Point newPosition = new Point(oldPosition);
                
                    // Update position based on direction
                    switch (direction.toLowerCase()) {
                        case "up" -> newPosition.translate(-1, 0);
                        case "down" -> newPosition.translate(1, 0);
                        case "left" -> newPosition.translate(0, -1);
                        case "right" -> newPosition.translate(0, 1);
                    }
                
                    System.out.println("Attempting to move Player " + (playerIndex + 1) + " to: " + newPosition);
                
                    // Validate the new position
                    if (gameBoard.isValidPosition(newPosition) &&
                        gameBoard.canMove(currentPlayer, direction) &&
                        !gameBoard.isTileOccupied(newPosition)) {
                
                        currentPlayer.setPosition(newPosition);
                        view.updatePlayerPosition(playerIndex, newPosition);
                
                        // Check for tokens
                        view.collectTokenIfPresent(playerIndex, newPosition);
                
                        System.out.println("Player " + (playerIndex + 1) + " moved logically to: " + newPosition);
                
                        // Notify host in host-client mode
                        if (view.isHost()) {
                            GameState updatedState = new GameState(
                                gameBoard.getTiles(),
                                gameBoard.getPlayers(),
                                currentPlayerIndex,
                                view.getTokenData(),
                                view.getAssignedPlayerIndex()
                            );
                            view.getNetworkManager().broadcastGameState(updatedState);
                            System.out.println("Host: Broadcasting updated game state after movement.");
                        }
                
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
