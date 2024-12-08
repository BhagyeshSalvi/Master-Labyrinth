
/**
 * Name - Bhagyesh Salvi
 * Number - 041103856
 * Java Application Programming UI Assignment
 * 
 * This class represents a single tile on the game board.
 * It includes attributes for tile type, image, rotation, and methods for tile manipulation.
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Tile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type; // Type of the tile (e.g., straight, corner, T-junction)
    private int rotation; // Current rotation angle (0, 90, 180, 270)
    private boolean isMovable; // Whether the tile can be moved
    private String baseImagePath; // Base image path (unrotated)
    private int row; // Grid row position
    private int col; // Grid column position

    private transient Image image; // Mark non-serializable fields as transient
    private transient ImageIcon imageIcon; // For UI representation

    /**
     * Constructor for default initialization.
     * 
     * @param type      the type of the tile (e.g., straight, corner)
     * @param imagePath the path to the tile's image
     */
    public Tile(String type, String imagePath) {
        this.type = type;
        this.baseImagePath = imagePath;
        this.rotation = 0; // Default rotation
        this.isMovable = true; // Default movable
        this.row = -1; // Undefined grid position
        this.col = -1;
        loadImage(); // Load the base image
    }

    /**
     * Constructor for grid-based initialization.
     * 
     * @param type      the type of the tile
     * @param imagePath the path to the tile's image
     * @param row       the grid row position
     * @param col       the grid column position
     * @param isMovable whether the tile can be moved
     */
    public Tile(String type, String imagePath, int row, int col, boolean isMovable) {
        this.type = type;
        this.baseImagePath = imagePath;
        this.rotation = 0; // Default rotation
        this.isMovable = isMovable;
        this.row = row; // Grid row position
        this.col = col; // Grid column position
        loadImage();
    }

    /**
     * Loads the base image for the tile from the specified path.
     */
    private void loadImage() {
        try {
            if (baseImagePath != null && !baseImagePath.isEmpty()) {
                this.image = new ImageIcon(baseImagePath).getImage();
                this.imageIcon = new ImageIcon(image.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
            } else {
                throw new IllegalArgumentException("Invalid imagePath provided.");
            }
        } catch (Exception e) {
            System.err.println("Error loading image for tile: " + baseImagePath);
            this.image = null;
            this.imageIcon = null;
        }
    }

    /**
     * Rotates the tile clockwise by 90 degrees.
     */
    public void rotateClockwise() {
        rotation = (rotation + 90) % 360;
        image = rotateImage(image, 90); // Rotate the image dynamically

        if (image != null) {
            imageIcon = new ImageIcon(image.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        }
    }

    /**
     * Dynamically rotates the given image by the specified angle.
     * 
     * @param img   the image to rotate
     * @param angle the angle of rotation in degrees
     * @return the rotated image
     */
    private Image rotateImage(Image img, int angle) {
        if (img == null) {
            System.err.println("Error: Image is null, cannot rotate.");
            return null;
        }

        int targetSize = 60; // Desired size of the image
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        BufferedImage rotatedImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        double scale = Math.min((double) targetSize / width, (double) targetSize / height);

        int centerX = targetSize / 2;
        int centerY = targetSize / 2;
        g2d.translate(centerX, centerY);
        g2d.rotate(Math.toRadians(angle));
        g2d.scale(scale, scale);
        g2d.translate(-width / 2, -height / 2);

        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotatedImage;
    }

    /**
     * Reloads the tile's image and applies the current rotation.
     */
    public void reloadImage() {
        loadImage(); // Reload the base image
        for (int i = 0; i < rotation / 90; i++) {
            image = rotateImage(image, 90);
        }
        if (image != null) {
            imageIcon = new ImageIcon(image.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        }
    }

    /**
     * Reinitializes transient fields after deserialization.
     * 
     * @param in the ObjectInputStream used for deserialization
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if a class is not found
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Deserialize non-transient fields
        loadImage(); // Reload the base image
        for (int i = 0; i < rotation / 90; i++) {
            image = rotateImage(image, 90);
        }
    }

    // Getters
    /**
     * Gets the image icon representing the tile.
     * 
     * @return the ImageIcon of the tile
     */
    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    /**
     * Gets the image path of the tile.
     * 
     * @return the base image path of the tile
     */
    public String getImagePath() {
        return baseImagePath;
    }

    /**
     * Gets the rotation of the tile in degrees.
     * 
     * @return the rotation of the tile
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation of the tile.
     * 
     * @param rotation the new rotation of the tile
     */
    public void setRotation(int rotation) {
        this.rotation = rotation % 360;
    }

    /**
     * Gets the type of the tile.
     * 
     * @return the type of the tile
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the image of the tile.
     * 
     * @return the image of the tile
     */
    public Image getImage() {
        return image;
    }

    /**
     * Checks if the tile is movable.
     * 
     * @return true if the tile is movable, false otherwise
     */
    public boolean isMovable() {
        return isMovable;
    }

    /**
     * Gets the grid row position of the tile.
     * 
     * @return the grid row position of the tile
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the grid column position of the tile.
     * 
     * @return the grid column position of the tile
     */
    public int getCol() {
        return col;
    }

    /**
     * Gets the connections of the tile based on its type.
     * 
     * @return a list of connections for the tile
     */
    public List<String> getConnections() {
        return GameUtils.getTileConnections().getOrDefault(type, new ArrayList<>());
    }

    /**
     * Creates a grayscale version of an image icon.
     * 
     * @param imagePath the path to the image
     * @param width     the width of the grayscale image
     * @param height    the height of the grayscale image
     * @return a grayscale ImageIcon
     */
    public static ImageIcon createGrayscaleIcon(String imagePath, int width, int height) {
        try {
            BufferedImage original = ImageIO.read(new File(imagePath));
            BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(),
                    BufferedImage.TYPE_BYTE_GRAY);

            Graphics g = grayscale.getGraphics();
            g.drawImage(original, 0, 0, null);
            g.dispose();

            Image scaledImage = grayscale.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
