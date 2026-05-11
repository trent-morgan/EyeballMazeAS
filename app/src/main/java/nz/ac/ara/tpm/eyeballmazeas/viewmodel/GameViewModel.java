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

    //LIST FOR KEEPING STARTING EYEBALL POSITION
    private final java.util.List<EyeballData> levelStarts = new java.util.ArrayList<>();

    private int currentLevelIndex = 0;

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

                //TO STOP LEVEL BEING SORTED ALPHABETICALLY (11 < 9 WOULD BE WRONG)
                java.util.Collections.sort(fileList, (o1, o2) -> {
                    try {
                        int n1 = Integer.parseInt(o1.replaceAll("[^0-9]", ""));
                        int n2 = Integer.parseInt(o2.replaceAll("[^0-9]", ""));
                        return Integer.compare(n1, n2);
                    } catch (Exception e) {
                        return o1.compareTo(o2);
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

    private void loadSpecificLevel(String path) {
        String jsonString = loadJSONFromAsset(getApplication(), path);
        if (jsonString != null) {
            Gson gson = new Gson();
            LevelData data = gson.fromJson(jsonString, LevelData.class);

            game.addLevel(data.height, data.width);

            for (SquareData s : data.squares) {
                game.addSquare(new PlayableSquare(s.color, s.shape), s.row, s.column);
            }

            if (data.goals != null) {
                for (GoalData g : data.goals) {
                    game.addGoal(g.row, g.column);
                    Log.d("LOAD_DEBUG", "Goal added at: " + g.row + "," + g.column);
                }
            }

            if (data.eyeball != null) {
                levelStarts.add(data.eyeball);

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

            game.addEyeball(start.row, start.column, dir);
        }
    }

    public void resetGoalsForCurrentLevel () {game.resetGoalsForCurrentLevel();}

    public Game getGame() {
        return this.game;
    }


    private static class LevelData {
        int width;
        int height;
        EyeballData eyeball;
        List<GoalData> goals;
        List<SquareData> squares;
    }
    //JSON DATA HOLDERS
    private static class GoalData {
        int row;
        int column;
    }

    private static class EyeballData {
        int row;
        int column;
        String direction;
    }

    private static class SquareData {
        int row;
        int column;
        Color color;
        Shape shape;
    }

    //GAME CLASS ACCESSORS
    public int getLevelWidth() {
        return game.getLevelWidth();
    }

    public int getLevelHeight() {
        return game.getLevelHeight();
    }


    public void setCurrentLevel(int currentLevelIndex) {
        game.setCurrentLevel(currentLevelIndex);
        this.currentLevelIndex = currentLevelIndex;
    }

    public int getCurrentLevel() {
        return this.currentLevelIndex;
    }

    public int getLevelCount() { return game.getLevelCount();}

    public int getGoalCount() { return game.getGoalCount();}

    public boolean isGoalActive(int row, int col) {
        return game.hasGoalAt(row, col);
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

    public Direction getEyeballDirection() {
        return game.getEyeballDirection();
    }

    public Message messageIfMovingTo(int row, int col) { return game.messageIfMovingTo(row, col); }

    public void moveTo(int destinationRow, int destinationColumn) {
        game.moveTo(destinationRow,destinationColumn);
    }

    public int getMoveCount() {
        return game.getMoveCount();
    }

    public void resetMoveCount() {
        game.resetMoveCount();
    }






}