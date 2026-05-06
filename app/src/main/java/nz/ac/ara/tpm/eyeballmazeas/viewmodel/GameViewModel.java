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
        loadLevelData(0);
    }

    public void loadLevelData(int levelIndex) {

        // 2. Correct Java syntax for a List of strings
        java.util.List<String> levels = java.util.Arrays.asList(
                "levels/level_1.json",
                "levels/level_2.json",
                "levels/level_3.json"
        );

        // 3. Prevent an "IndexOutOfBounds" error if the levelIndex is wrong
        if (levelIndex < 0 || levelIndex >= levels.size()) {
            Log.e("JSON_LOAD", "Invalid level index: " + levelIndex);
            return;
        }

        // 4. Use the levelIndex to get the correct filename from your list
        String fileName = levels.get(levelIndex);
        String jsonString = loadJSONFromAsset(getApplication(), fileName);

        if (jsonString != null) {
            Gson gson = new Gson();
            LevelData data = gson.fromJson(jsonString, LevelData.class);

            // 5. Clear the current game state before loading a new level
            // (Assuming your Game class has a way to reset/clear old levels)
            // game.clear();

            game.addLevel(data.height, data.width);

            for (SquareData s : data.squares) {
                game.addSquare(new PlayableSquare(s.color, s.shape), s.row, s.column);
            }

            Log.d("JSON_LOAD", "Successfully loaded: " + fileName);
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
    private static class LevelData {
        int width;
        int height;
        List<SquareData> squares;
    }

    private static class SquareData {
        int row;
        int column;
        Color color; // GSON is smart enough to match string "RED" to Color.RED
        Shape shape;
    }
}