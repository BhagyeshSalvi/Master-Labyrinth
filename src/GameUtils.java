import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameUtils {
   

    public static List<String> generateImageList() {
        String[] imagePaths = {
            "Pictures/GridCell/brick_cross.png",
            "Pictures/GridCell/hallway_vert.png",
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

    public static Map<String, Boolean> generateTokenPaths() {
    Map<String, Boolean> tokenPaths = new LinkedHashMap<>(); // Maintain insertion order
    
    // Add regular tokens (false means not a magic card)
    tokenPaths.put("Pictures/Token/gold_1.png", false);
    tokenPaths.put("Pictures/Token/gold_3.png", false);
    tokenPaths.put("Pictures/Token/gold_4.png", false);
    tokenPaths.put("Pictures/Token/gold_5.png", false);
    tokenPaths.put("Pictures/Token/gold_6.png", false);
    tokenPaths.put("Pictures/Token/gold_7.png", false);
    tokenPaths.put("Pictures/Token/gold_8.png", false);
    tokenPaths.put("Pictures/Token/gold_9.png", false);
    tokenPaths.put("Pictures/Token/gold_11.png", false);
    tokenPaths.put("Pictures/Token/gold_12.png", false);
    tokenPaths.put("Pictures/Token/gold_13.png", false);
    tokenPaths.put("Pictures/Token/gold_15.png", false);
    tokenPaths.put("Pictures/Token/gold_16.png", false);

    tokenPaths.put(normalizePath("Pictures/MG1.png"), true);
    tokenPaths.put(normalizePath("Pictures/MG2.png"), true);
    tokenPaths.put(normalizePath("Pictures/MG3.png"), true);

    
    return tokenPaths;
}

private static String normalizePath(String path) {
    return path.trim().replace("\\", "/").toLowerCase(); // Trim spaces, unify separators, and lowercase
}

}
