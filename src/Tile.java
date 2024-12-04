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

    // Constructor for default initialization
    public Tile(String type, String imagePath) {
        this.type = type;
        this.baseImagePath = imagePath;
        this.rotation = 0; // Default rotation
        this.isMovable = true; // Default movable
        this.row = -1; // Undefined grid position
        this.col = -1;
        loadImage(); // Load the base image
    }

    // Constructor for grid-based initialization
    public Tile(String type, String imagePath, int row, int col, boolean isMovable) {
        this.type = type;
        this.baseImagePath = imagePath;
        this.rotation = 0; // Default rotation
        this.isMovable = isMovable;
        this.row = row; // Grid row position
        this.col = col; // Grid column position
        loadImage();
    }

    // Load the base image
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

    // Rotate the tile clockwise
    public void rotateClockwise() {
        rotation = (rotation + 90) % 360;
        image = rotateImage(image, 90); // Rotate the image dynamically

        if (image != null) {
            imageIcon = new ImageIcon(image.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        }
    }

    // Dynamically rotate the image
    private Image rotateImage(Image img, int angle) {
        if (img == null) {
            System.err.println("Error: Image is null, cannot rotate.");
            return null;
        }
    
        int targetSize = 60; // Desired size of the image
        int width = img.getWidth(null);
        int height = img.getHeight(null);
    
        // Create a new BufferedImage with a fixed canvas size
        BufferedImage rotatedImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();
    
        // Set rendering hints for high-quality rotation
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    
        // Calculate the scaling factor to fit the image within the target size
        double scale = Math.min((double) targetSize / width, (double) targetSize / height);
    
        // Center and scale the image
        int centerX = targetSize / 2;
        int centerY = targetSize / 2;
        g2d.translate(centerX, centerY);
        g2d.rotate(Math.toRadians(angle));
        g2d.scale(scale, scale);
        g2d.translate(-width / 2, -height / 2);
    
        // Draw the original image onto the rotated canvas
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
    
        return rotatedImage;
    }

    // Reload the image and apply the current rotation
    public void reloadImage() {
        loadImage(); // Reload the base image
        for (int i = 0; i < rotation / 90; i++) {
            image = rotateImage(image, 90);
        }
        if (image != null) {
            imageIcon = new ImageIcon(image.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        }
    }

    // Serialization support: reinitialize transient fields after deserialization
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Deserialize non-transient fields
        loadImage(); // Reload the base image
        for (int i = 0; i < rotation / 90; i++) {
            image = rotateImage(image, 90);
        }
    }

    // Getters
    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public String getImagePath() {
        return baseImagePath;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation % 360;
    }

    public String getType() {
        return type;
    }

    public Image getImage() {
        return image;
    }

    public boolean isMovable() {
        return isMovable;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public List<String> getConnections() {
        return GameUtils.getTileConnections().getOrDefault(type, new ArrayList<>());
    }

    public static ImageIcon createGrayscaleIcon(String imagePath, int width, int height) {
        try {
            BufferedImage original = ImageIO.read(new File(imagePath));
            BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

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
