import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameUtils {
    public static List<String> generateImageList() {
        String[] imagePaths = {
            "Pictures/GridCell/brick_cross.png",
            "Pictures/GridCell/brick_cross.png",
            "Pictures/GridCell/hallway_horiz.png",
            "Pictures/GridCell/brick_NE.png",
            "Pictures/GridCell/brick_NW.png",
            "Pictures/GridCell/brick_SE.png",
            "Pictures/GridCell/brick_SW.png",
            "Pictures/GridCell/brick_Teast.png",
            "Pictures/GridCell/brick_Twest.png",
            "Pictures/GridCell/brick_Tnorth.png",
            "Pictures/GridCell/brick_Tsouth.png",
        };

        List<String> imageList = new ArrayList<>();
        for (int i = 0; i < 2; i++) imageList.add(imagePaths[0]); // Cross
        for (int i = 0; i < 8; i++) imageList.add(imagePaths[1]); // Vertical
        for (int i = 0; i < 7; i++) imageList.add(imagePaths[2]); // Horizontal
        for (int i = 0; i < 4; i++) imageList.add(imagePaths[3]); // Northeast
        for (int i = 0; i < 4; i++) imageList.add(imagePaths[4]); // Northwest
        for (int i = 0; i < 4; i++) imageList.add(imagePaths[5]); // Southeast
        for (int i = 0; i < 4; i++) imageList.add(imagePaths[6]); // Southwest
        for (int i = 0; i < 4; i++) imageList.add(imagePaths[7]); // T-east
        for (int i = 0; i < 4; i++) imageList.add(imagePaths[8]); // T-west
        for (int i = 0; i < 4; i++) imageList.add(imagePaths[9]); // T-north
        for (int i = 0; i < 4; i++) imageList.add(imagePaths[10]); // T-south

        return imageList;
    }
}
