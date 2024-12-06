    import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.IOException;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.util.Iterator;


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
                    
                    Map<Point, String> updatedTokenData = state.getTokenData();
                    Map<Point, JLabel> currentTokenMap = view.getTokenMap(); // Access tokenMap
                
                    for (String tokenPath : GameUtils.generateTokenPaths().keySet()) {
                    boolean isMagicCard = GameUtils.generateTokenPaths().getOrDefault(tokenPath, false);
                         if (isMagicCard && !updatedTokenData.containsValue(tokenPath)) {
                         JLabel magicalLabel = view.getMagicalComponentLabels().get(tokenPath);
                         if (magicalLabel != null) {
                       Icon grayscaleIcon = Tile.createGrayscaleIcon(tokenPath, 80, 80);

                            if (grayscaleIcon != null) {
                                magicalLabel.setIcon(grayscaleIcon);
                            }
                            magicalLabel.setText("Collected");
                            magicalLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                            magicalLabel.setVerticalTextPosition(SwingConstants.CENTER);
                            magicalLabel.setFont(new Font("Arial", Font.BOLD, 17));
                            magicalLabel.setForeground(Color.black);
                            magicalLabel.getParent().revalidate();
                            magicalLabel.getParent().repaint();
                            System.out.println("Client: Updated magic card to collected: " + tokenPath);
                        }
                    }
    }

                    // Use an iterator to remove collected tokens
                    Iterator<Map.Entry<Point, JLabel>> iterator = currentTokenMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Point, JLabel> entry = iterator.next();
                        Point position = entry.getKey();
                        if (!updatedTokenData.containsKey(position)) {
                            JLabel tokenLabel = entry.getValue();
                            if (tokenLabel != null && view.getLayeredPane().isAncestorOf(tokenLabel)) {
                                tokenLabel.getParent().remove(tokenLabel);
                                System.out.println("Client: Removed token at " + position);
                            }
                            iterator.remove(); // Safely remove the token from the map
                        }
                    }
                
                    // Update tokenMap
                    for (Map.Entry<Point, String> entry : updatedTokenData.entrySet()) {
                        Point position = entry.getKey();
                        if (!currentTokenMap.containsKey(position)) {
                            String tokenPath = entry.getValue();
                            JLabel tokenLabel = view.createTokenLabel(tokenPath, 20); // Create token UI
                            currentTokenMap.put(position, tokenLabel);
                            System.out.println("Client: Added token at " + position);
                        }
                    }
                
                
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
                    view.getLayeredPane().revalidate();
                    view.getLayeredPane().repaint();
                
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
                        if (view.isHost() && view.getNetworkManager() != null) {
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
