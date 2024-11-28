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
        starsCollected++;
        System.out.println(name + " collected a star! Total stars: " + starsCollected);
    }

    public int getStarsCollected() {
        return starsCollected;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        System.out.println("Player " + name + " position updated from: " + this.position + " to: " + position);
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
