import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    private String type; // Type of the tile (e.g., straight, corner, T-junction)
    private int rotation; // Current rotation angle (0, 90, 180, 270)
    private boolean isMovable; // Whether the tile can be moved
    private Image image; // Graphical representation of the tile

    public Tile(String type, Image image) {
        this.type = type;
        this.image = image;
        this.rotation = 0; // Default rotation is 0
        this.isMovable = true; // Default to movable
    }

    // Rotate the tile clockwise
    public void rotateClockwise() {
        rotation = (rotation + 90) % 360; // Increment rotation and wrap around at 360
        image = rotateImage(image, 90); // Update the graphical representation
    }

    // Helper method to rotate the image
    private Image rotateImage(Image img, int angle) {
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.rotate(Math.toRadians(angle), img.getWidth(null) / 2.0, img.getHeight(null) / 2.0);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return bufferedImage;
    }

    public int getRotation() {
        return rotation;
    }

    public Image getImage() {
        return image;
    }
}
