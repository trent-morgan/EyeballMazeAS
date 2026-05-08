package nz.ac.ara.tpm.eyeballmazeas.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel; // Updated this

import nz.ac.ara.tpm.eyeballmazeas.model.Direction;
import nz.ac.ara.tpm.eyeballmazeas.model.Game;
import nz.ac.ara.tpm.eyeballmazeas.model.Message;
import nz.ac.ara.tpm.eyeballmazeas.model.PlayableSquare;
import nz.ac.ara.tpm.eyeballmazeas.model.Color;
import nz.ac.ara.tpm.eyeballmazeas.model.Shape;
import nz.ac.ara.tpm.eyeballmazeas.model.Square;

import com.google.gson.Gson;

import java.util.List;

public class GameViewModel extends AndroidViewModel {

    private final Game game;

    // New List to remember the start positions
    private final java.util.List<EyeballData> levelStarts = new java.util.ArrayList<>();

    // Inside GameViewModel.java
    public GameViewModel(Application application) {
        super(application);
        game = new Game();
        loadAllLevels();
    }

    public void loadAllLevels() {
        try {
            String[] levelFiles = getApplication().getAssets().list("levels");

            if (levelFiles != null) {
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
                // CRITICAL: Ensure this list grows at the exact same pace as game.addLevel
                levelStarts.add(data.eyeball);

                // Set initial state for the VERY last level loaded
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

    public void resetEyeballForLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levelStarts.size()) {
            EyeballData start = levelStarts.get(levelIndex);

            nz.ac.ara.tpm.eyeballmazeas.model.Direction dir =
                    nz.ac.ara.tpm.eyeballmazeas.model.Direction.valueOf(start.direction.toUpperCase());

            // This forces the Game's EyeballHolder to move to the correct spot
            game.addEyeball(start.row, start.column, dir);
        }
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

    // Inside GameViewModel.java
    public boolean isGoalActive(int row, int col) {
        // This simply tells the UI: "Is there a goal here right now?"
        return game.hasGoalAt(row, col);
    }

    public boolean isGoalFinished(int row, int col) {
        // If the model doesn't give us the 'completedGoals' list,
        // and you can't change the model to add a getter,
        // we have to check if it's NOT in the active list
        // but the level design says it WAS a goal.

        // Most Eyeball Maze models have a way to check the level's static data:
        return !game.hasGoalAt(row, col) && game.hasGoalAt(row, col);
    }

    public int getCompletedGoalCount() {
        return game.getCompletedGoalCount();
    }

    public void addSquare(Square square, int row, int column) {
        game.addSquare(square, row, column);
    }

    public Color getColorAt(int row, int column) {
        return game.getColorAt(row, column);
    }

    public Shape getShapeAt(int row, int column) {
        return game.getShapeAt(row, column);
    }

    public void addEyeball(int row, int column, Direction direction) {
        game.addEyeball(row,column,direction);
    }

    public int getEyeballRow() {
        return game.getEyeballRow();
    }

    public int getEyeballColumn() {
        return game.getEyeballColumn();
    }

    public void moveTo(int destinationRow, int destinationColumn) {
        game.moveTo(destinationRow,destinationColumn);
    }

    public int getMoveCount() {
        return game.getMoveCount();
    }

    public void resetMoveCount() {
        game.resetMoveCount();
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