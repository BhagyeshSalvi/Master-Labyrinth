import java.awt.Color;
import java.awt.Point;

public class Player {
    private String name;
    private Point position;
    private int starsCollected;
    private Color color;

    public Player(String name, Point position, Color color) {
        this.name = name;
        this.position = position;
        this.color = color;
    }
    public void collectStar() {
        starsCollected++; // Increment the star count
        System.out.println("Star collected! Total stars: " + starsCollected);
    }

    public int getStarsCollected() {
        return starsCollected; // Getter for stars
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void move(String direction, int gridSize) {
        switch (direction.toLowerCase()) {
            case "up":
                if (position.x > 2) position.x--; // Move up
                break;
            case "down":
                if (position.x < gridSize - 2) position.x++; // Move down
                break;
            case "left":
                if (position.y > 2) position.y--; // Move left
                break;
            case "right":
                if (position.y < gridSize - 2) position.y++; // Move right
                break;
        }
    }
}
