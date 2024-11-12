import java.awt.Point;

public class GameController {
    private GameBoard gameBoard;
    private MainGame view;

    public GameController(GameBoard gameBoard, MainGame view) {
        this.gameBoard = gameBoard;
        this.view = view;
    }

    public void handleMovement(int playerIndex, String direction) {
        Player currentPlayer = gameBoard.getPlayers()[playerIndex];
        Point oldPosition = currentPlayer.getPosition();
    
        if (gameBoard.movePlayer(currentPlayer, direction)) {
            Point newPosition = currentPlayer.getPosition();
            System.out.println("Player moved to: " + newPosition);
            view.updatePlayerPosition(playerIndex, newPosition); // Update the UI
        } else {
            System.out.println("Invalid move from " + oldPosition + " in direction " + direction);
            view.showInvalidMoveDialog(); // Show dialog for invalid move
        }
    }
    
}
