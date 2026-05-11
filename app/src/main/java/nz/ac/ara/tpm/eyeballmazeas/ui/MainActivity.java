package nz.ac.ara.tpm.eyeballmazeas.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import nz.ac.ara.tpm.eyeballmazeas.R;
import nz.ac.ara.tpm.eyeballmazeas.model.Color;
import nz.ac.ara.tpm.eyeballmazeas.model.Message;
import nz.ac.ara.tpm.eyeballmazeas.model.Shape;
import nz.ac.ara.tpm.eyeballmazeas.viewmodel.GameViewModel;

public class MainActivity extends AppCompatActivity {

    private GameViewModel viewModel;
    private GridLayout mazeGrid;

    private boolean isSoundEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);
        mazeGrid = findViewById(R.id.mazeGrid);

        LinearLayout container = findViewById(R.id.levelButtonContainer);
        int levelCount = viewModel.getLevelCount();

        for (int i = 0; i < levelCount; i++) {
            int levelIndex = i;
            int levelNum = i + 1;

            Button btn = new Button(this);
            btn.setSoundEffectsEnabled(false);
            int size = (int) (60 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);

            btn.setText(String.valueOf(levelNum));
            btn.setTag(i);

            btn.setOnClickListener(v -> startLevel(levelIndex));

            container.addView(btn);
        }

        startLevel(0);

        CheckBox checkSound = findViewById(R.id.checkSound);
        checkSound.setChecked(true);
        checkSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isSoundEnabled = isChecked;
            Log.d("GAME_SETTINGS", "Sound enabled: " + isChecked);
        });

        Button btnRestart = findViewById(R.id.btnRestart);

        btnRestart.setOnClickListener(v -> startLevel(viewModel.getCurrentLevel()));

        Button btnRules = findViewById(R.id.btnRules);

        btnRules.setOnClickListener(v -> new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("How to Play")
                .setMessage("Move the eyeball to the goal by matching colors or shapes!")
                .setPositiveButton("Got it!", null)
                .show());

    }

    private void startLevel(int levelIndex) {
        viewModel.setCurrentLevel(levelIndex);
        viewModel.resetEyeballForLevel(levelIndex);
        viewModel.resetMoveCount();
        viewModel.resetGoalsForCurrentLevel();
        updateMazeDisplay(levelIndex);
    }

    private void updateMazeDisplay(int levelIndex) {
        if (mazeGrid == null) mazeGrid = findViewById(R.id.mazeGrid);
        mazeGrid.removeAllViews();

        if (viewModel.getGame() == null || viewModel.getLevelWidth() == 0) {
            Log.e("UI_ERROR", "Level data missing for index: " + levelIndex);
            return;
        }

        int eyeRow = viewModel.getEyeballRow();
        int eyeCol = viewModel.getEyeballColumn();
        int rows = viewModel.getLevelHeight();
        int cols = viewModel.getLevelWidth();

        mazeGrid.setRowCount(rows);
        mazeGrid.setColumnCount(cols);

        int squareSize = switch (cols) {
            case 6 -> 200;
            case 5 -> 230;
            case 4 -> 280;
            default -> 170;
        };

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color squareColor = viewModel.getColorAt(r, c);
                Shape squareShape = viewModel.getShapeAt(r, c);

                FrameLayout tile = new FrameLayout(this);
                tile.setSoundEffectsEnabled(false);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(r, 1f),
                        GridLayout.spec(c, 1f)
                );
                params.width = squareSize;
                params.height = squareSize;
                params.setMargins(1, 1, 1, 1);
                tile.setLayoutParams(params);

                if (squareColor == null) {
                    tile.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                    mazeGrid.addView(tile);
                    continue;
                }

                tile.setBackgroundResource(R.drawable.square_border);

                if (squareShape != null) {
                    ImageView shapeImage = new ImageView(this);
                    shapeImage.setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));
                    shapeImage.setPadding(12, 12, 12, 12);
                    shapeImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    shapeImage.setImageResource(getShapeResource(squareShape.toString()));

                    int colorInt = getSquareColor(squareColor.toString());
                    shapeImage.setImageTintList(android.content.res.ColorStateList.valueOf(colorInt));
                    tile.addView(shapeImage);
                }

                if (viewModel.isGoalActive(r, c)) {
                    tile.setBackgroundResource(R.drawable.bg_goal_stripes);
                }

                if (r == eyeRow && c == eyeCol) {
                    ImageView eyeballOverlay = new ImageView(this);
                    eyeballOverlay.setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));
                    eyeballOverlay.setImageResource(R.drawable.ic_eyeball);
                    eyeballOverlay.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    eyeballOverlay.setImageTintList(null);

                    String direction = viewModel.getEyeballDirection().toString();
                    eyeballOverlay.setRotation(getRotationForDirection(direction));

                    tile.addView(eyeballOverlay);
                }

                final int clickedRow = r;
                final int clickedCol = c;
                tile.setOnClickListener(v -> {
                    Message feedback = viewModel.messageIfMovingTo(clickedRow, clickedCol);

                    if (feedback == Message.OK) {
                        playSound(R.raw.move);
                        viewModel.moveTo(clickedRow, clickedCol);

                        checkCompletion(levelIndex);
                    } else {
                        playSound(R.raw.invalid_move);
                        String userFriendlyText = getFriendlyMessage(feedback);
                        Toast.makeText(this, userFriendlyText, Toast.LENGTH_SHORT).show();                    }

                    updateMazeDisplay(levelIndex);
                });

                mazeGrid.addView(tile);
            }
        }
        updateLabelDisplay(levelIndex);
    }

    private void checkCompletion(int levelIndex) {
        int remainingGoals = viewModel.getGoalCount();
        int totalLevels = viewModel.getLevelCount();

        if (remainingGoals == 0) {
            playSound(R.raw.level_complete);

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

            if (levelIndex + 1 < totalLevels) {
                builder.setTitle("Level " + (levelIndex + 1) + " Cleared")
                        .setMessage("Moves: " + viewModel.getMoveCount())
                        .setCancelable(false)
                        .setPositiveButton("Next Level", (dialog, which) -> {
                            int next = levelIndex + 1;
                            startLevel(next);
                        })
                        .setNegativeButton("Replay", (dialog, which) -> {
                            startLevel(levelIndex);
                        });

            } else {
                builder.setTitle("Level " + (levelIndex + 1) + " Cleared")
                        .setMessage("Moves: " + viewModel.getMoveCount())
                        .setCancelable(false)
                        .setPositiveButton("Start Over", (dialog, which) -> {
                            startLevel(0);
                        })
                        .setNegativeButton("Close", null);
            }
            builder.show();
        }
    }
//    private void updateGoals() {
//        if (mazeGrid == null) return;
//
//        int cols = viewModel.getLevelWidth();
//
//        for (int i = 0; i < mazeGrid.getChildCount(); i++) {
//            View child = mazeGrid.getChildAt(i);
//            if (child instanceof FrameLayout) {
//                int r = i / cols;
//                int c = i % cols;
//
//                // Since hitting a goal removes it from the list in your model:
//                if (viewModel.isGoalActive(r, c)) {
//                    child.setBackgroundResource(R.drawable.bg_goal_stripes);
//                } else {
//                    child.setBackgroundResource(R.drawable.square_border);
//                }
//            }
//        }
//    }

    private int getSquareColor(String colorName) {
        if (colorName == null) return android.graphics.Color.TRANSPARENT;
        return switch (colorName.toUpperCase()) {
            case "RED" -> android.graphics.Color.RED;
            case "BLUE" -> android.graphics.Color.CYAN;
            case "GREEN" -> android.graphics.Color.parseColor("#32CD32");
            case "YELLOW" -> android.graphics.Color.YELLOW;
            case "PURPLE" -> android.graphics.Color.MAGENTA;
            default -> android.graphics.Color.LTGRAY;
        };
    }

    private int getShapeResource(String shapeName) {
        if (shapeName == null) return 0;
        return switch (shapeName.toUpperCase()) {
            case "STAR" -> R.drawable.ic_star;
            case "FLOWER" -> R.drawable.ic_flower;
            case "DIAMOND" -> R.drawable.ic_diamond;
            case "CROSS" -> R.drawable.ic_cross;
            case "LIGHTNING" -> R.drawable.ic_lightning;
            case "EYEBALL" -> R.drawable.ic_eyeball;
            default -> 0;
        };
    }

    private void updateLabelDisplay(int levelIndex) {
        TextView txtLevel = findViewById(R.id.txtLevel);
        TextView txtMoves = findViewById(R.id.txtMoves);
        TextView txtGoals = findViewById(R.id.txtGoals);

        int activeGoals = viewModel.getGoalCount();
        int completedGoals = viewModel.getCompletedGoalCount();

        txtLevel.setText("Level: " + (levelIndex + 1));
        txtMoves.setText("Moves: " + viewModel.getMoveCount());
        txtGoals.setText("Goals: " + completedGoals + "/" + (activeGoals + completedGoals));

        LinearLayout container = findViewById(R.id.levelButtonContainer);
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof Button btn) {
                if (btn.getTag() != null && (int) btn.getTag() == levelIndex) {
                    btn.setBackgroundColor(android.graphics.Color.BLUE);
                    btn.setTextColor(android.graphics.Color.WHITE);
                } else {
                    btn.setBackgroundColor(android.graphics.Color.LTGRAY);
                    btn.setTextColor(android.graphics.Color.BLACK);
                }
            }
        }
    }

    private float getRotationForDirection(String direction) {
        if (direction == null) return 0f;
        return switch (direction.toUpperCase()) {
            case "UP" -> 0f;
            case "RIGHT" -> 90f;
            case "DOWN" -> 180f;
            case "LEFT" -> 270f;
            default -> 0f;
        };
    }

    private String getFriendlyMessage(Message message) {
        if (message == null) return "";

        return switch (message) {
            case OK -> "Move successful!";
            case MOVING_DIAGONALLY -> "You can only move in straight lines.";
            case BACKWARDS_MOVE -> "No looking back! You can't move backwards.";
            case MOVING_OVER_BLANK -> "You can't jump over empty spaces.";
            case DIFFERENT_SHAPE_OR_COLOR -> "You must match the color or the shape!";
            default -> "Illegal move!";
        };
    }

    private void playSound(int soundRawId) {
        if (isSoundEnabled) {
            MediaPlayer mp = MediaPlayer.create(this, soundRawId);
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.start();
        }
    }
}