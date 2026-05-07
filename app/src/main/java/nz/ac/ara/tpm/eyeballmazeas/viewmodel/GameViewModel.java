package nz.ac.ara.tpm.eyeballmazeas.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel; // Updated this
import nz.ac.ara.tpm.eyeballmazeas.model.Game;
import nz.ac.ara.tpm.eyeballmazeas.model.PlayableSquare;
import nz.ac.ara.tpm.eyeballmazeas.model.Color;
import nz.ac.ara.tpm.eyeballmazeas.model.Shape;

import com.google.gson.Gson;

import java.util.List;

public class GameViewModel extends AndroidViewModel { // Changed to AndroidViewModel

    private final Game game;

    public GameViewModel(Application application) {
        super(application); // Added this mandatory line
        game = new Game();
        loadAllLevels();
    }

    public void loadAllLevels() {
        try {
            String[] levelFiles = getApplication().getAssets().list("levels");

            if (levelFiles != null) {
                // Convert to a List so we can use a custom sort
                java.util.List<String> fileList = new java.util.ArrayList<>(java.util.Arrays.asList(levelFiles));

                // Custom Sort: Extracts the number from "level_X.json"
                java.util.Collections.sort(fileList, (o1, o2) -> {
                    try {
                        int n1 = Integer.parseInt(o1.replaceAll("[^0-9]", ""));
                        int n2 = Integer.parseInt(o2.replaceAll("[^0-9]", ""));
                        return Integer.compare(n1, n2);
                    } catch (Exception e) {
                        return o1.compareTo(o2); // Fallback to alpha if no number found
                    }
                });

                for (String fileName : fileList) {
                    if (fileName.endsWith(".json")) {
                        Log.d("LEVEL_ORDER", "Loading in order: " + fileName);
                        loadSpecificLevel("levels/" + fileName);
                    }
                }
            }
        } catch (java.io.IOException e) {
            Log.e("ASSET_ERROR", "Could not list levels folder", e);
        }
    }

    // Refactor your loading logic into this helper
    private void loadSpecificLevel(String path) {
        String jsonString = loadJSONFromAsset(getApplication(), path);
        if (jsonString != null) {
            Gson gson = new Gson();
            LevelData data = gson.fromJson(jsonString, LevelData.class);

            // 1. Initialize the level structure
            game.addLevel(data.height, data.width);

            // 2. Load the Squares (for the grid)
            for (SquareData s : data.squares) {
                game.addSquare(new PlayableSquare(s.color, s.shape), s.row, s.column);
            }

            // 3. FIX: Load the Goals from the "goals" list in JSON
            if (data.goals != null) {
                for (GoalData g : data.goals) {
                    game.addGoal(g.row, g.column);
                    Log.d("LOAD_DEBUG", "Goal added at: " + g.row + "," + g.column);
                }
            }

            // 4. Load the Eyeball starting position
            if (data.eyeball != null) {
                // Assuming your Direction enum matches "UP", "DOWN", etc.
                nz.ac.ara.tpm.eyeballmazeas.model.Direction dir =
                        nz.ac.ara.tpm.eyeballmazeas.model.Direction.valueOf(data.eyeball.direction.toUpperCase());
                game.addEyeball(data.eyeball.row, data.eyeball.column, dir);
            }
        }
    }

    private String loadJSONFromAsset(Application application, String fileName) {
        String json;
        try {
            java.io.InputStream is = application.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public Game getGame() {
        return this.game;
    }

    // These inner classes tell GSON how to read your level_1.json file
    // Inside GameViewModel.java
    private static class LevelData {
        int width;
        int height;
        EyeballData eyeball;    // Matches the "eyeball" object in JSON
        List<GoalData> goals;   // Matches the "goals" list in JSON
        List<SquareData> squares;
    }

    private static class GoalData {
        int row;
        int column;
    }

    private static class EyeballData {
        int row;
        int column;
        String direction; // Will be converted to your Direction Enum
    }

    private static class SquareData {
        int row;
        int column;
        Color color;
        Shape shape;
    }
}