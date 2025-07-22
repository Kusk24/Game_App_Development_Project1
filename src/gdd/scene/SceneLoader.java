package gdd.scene;

import gdd.SpawnDetails;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneLoader {

    /**
     * Loads a 2D int map from a CSV file.
     */
    public static int[][] loadMap(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            List<int[]> rows = new ArrayList<>();
            for (String line; (line = br.readLine()) != null;) {
                String[] tokens = line.split(",");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i].trim());
                }
                rows.add(row);
            }
            return rows.toArray(int[][]::new);
        }
    }

    /**
     * Loads spawn details from a CSV file.
     * Adjusts x coordinate by adding boardWidth.
     * Skips header row.
     */
    public static Map<Integer, SpawnDetails> loadSpawnDetails(
            String path, int boardWidth, boolean isHorizontal) throws IOException {
        Map<Integer, SpawnDetails> spawnMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                String[] tokens = line.split(",");
                int spawnFrame = Integer.parseInt(tokens[0].trim());
                String type = tokens[1].trim();
                int xFromCsv = Integer.parseInt(tokens[2].trim());
                int y = Integer.parseInt(tokens[3].trim());

                int x = isHorizontal ? boardWidth + xFromCsv : xFromCsv;

                spawnMap.put(spawnFrame, new SpawnDetails(type, x, y));
            }
        }

        // Debug output to check what was loaded
        System.out.println("Loaded spawnMap:");
        spawnMap.forEach((frame, spawn) -> System.out
                .println("Frame: " + frame + ", Type: " + spawn.type + ", X: " + spawn.x + ", Y: " + spawn.y));

        return spawnMap;
    }
}
