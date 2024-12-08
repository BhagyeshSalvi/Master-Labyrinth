
/**
 * Name - Bhagyesh Salvi
 * Number - 041103856
 * Java Application Programming UI Assignment
 * 
 * This class represents a player in the game. 
 * It tracks player attributes such as position, stars collected, and color.
 */

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Point position;
    private int starsCollected;
    private Color color;

    /**
     * Constructs a new Player with the specified name, initial position, and color.
     * 
     * @param name     the name of the player
     * @param position the initial position of the player on the board
     * @param color    the color representing the player
     */
    public Player(String name, Point position, Color color) {
        this.name = name;
        this.position = position;
        this.color = color;
    }

    /**
     * Increments the number of stars collected by the player.
     * Prints a message indicating the updated total stars.
     */
    public void collectStar() {
        starsCollected++;
        System.out.println(name + " collected a star! Total stars: " + starsCollected);
    }

    /**
     * Retrieves the total number of stars collected by the player.
     * 
     * @return the number of stars collected
     */
    public int getStarsCollected() {
        return starsCollected;
    }

    /**
     * Retrieves the current position of the player on the board.
     * 
     * @return the player's current position as a Point
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Updates the player's position on the board.
     * Prints a message indicating the position change.
     * 
     * @param position the new position of the player
     */
    public void setPosition(Point position) {
        System.out.println("Player " + name + " position updated from: " + this.position + " to: " + position);
        this.position = position;
    }

    /**
     * Retrieves the player's name.
     * 
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the player's color.
     * 
     * @return the color representing the player
     */
    public Color getColor() {
        return color;
    }
}
