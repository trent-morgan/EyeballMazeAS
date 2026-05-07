package nz.ac.ara.tpm.eyeballmazeas.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import nz.ac.ara.tpm.eyeballmazeas.R;
import nz.ac.ara.tpm.eyeballmazeas.model.Color;
import nz.ac.ara.tpm.eyeballmazeas.model.Shape;
import nz.ac.ara.tpm.eyeballmazeas.viewmodel.GameViewModel;

public class MainActivity extends AppCompatActivity {

    private GameViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // 1. Find the container
        LinearLayout container = findViewById(R.id.levelButtonContainer);

        // FIX: Use the actual count from the Game model instead of a hardcoded '3'
        int levelCount = viewModel.getGame().getLevelCount();

        for (int i = 0; i < levelCount; i++) {
            int levelIndex = i; // The internal index (0, 1, 2...)
            int levelNum = i + 1; // The display number (1, 2, 3...)

            Button btn = new Button(this);

            int size = (int) (60 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);

            btn.setText(String.valueOf(levelNum));

            btn.setOnClickListener(v -> {
                Log.d("LEVEL_CLICK", "Loading Index: " + levelIndex);
                // We no longer need to call loadLevelData here if you pre-load
                // in the ViewModel constructor as we discussed.
                updateMazeDisplay(levelIndex);
//                updateLabelDisplay(levelIndex);
            });

            container.addView(btn);
        }

        // Initialize the display with the first level
        updateMazeDisplay(0);

        CheckBox checkSound = findViewById(R.id.checkSound);
        Button btnRules = findViewById(R.id.btnRules);

        checkSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("GAME_SETTINGS", "Sound: " + isChecked);
        });

        btnRules.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("How to Play")
                    .setMessage("Move the eyeball to the goal by matching colors or shapes!")
                    .setPositiveButton("Got it!", null)
                    .show();
        });
    }

    private void updateMazeDisplay(int levelIndex) {
        // Set the active level in the model before drawing
        viewModel.getGame().setCurrentLevel(levelIndex);

        if (viewModel.getGame() == null || viewModel.getGame().getLevelWidth() == 0) {
            Log.e("UI_ERROR", "Level data missing for index: " + levelIndex);
            return;
        }

        GridLayout mazeGrid = findViewById(R.id.mazeGrid);
        mazeGrid.removeAllViews();

        int rows = viewModel.getGame().getLevelHeight();
        int cols = viewModel.getGame().getLevelWidth();

        mazeGrid.setRowCount(rows);
        mazeGrid.setColumnCount(cols);

        float density = getResources().getDisplayMetrics().density;

        int densityInt = 100 - (4 * viewModel.getGame().getLevelWidth());
        int squareSize = (int) (densityInt * density);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color squareColor = viewModel.getGame().getColorAt(r, c);
                Shape squareShape = viewModel.getGame().getShapeAt(r, c);

                ImageView squareView = new ImageView(this);
                squareView.setPadding(12, 12, 12, 12); // Padding inside the border

                GridLayout.Spec rowSpec = GridLayout.spec(r, 1f);
                GridLayout.Spec colSpec = GridLayout.spec(c, 1f);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);

                params.width = squareSize;
                params.height = squareSize;
                params.setMargins(1, 1, 1, 1);
                squareView.setLayoutParams(params);

                if (squareColor != null) {
                    squareView.setVisibility(View.VISIBLE);

                    // FIX: Set the border XML as the background
                    squareView.setBackgroundResource(R.drawable.square_border);

                    int colorInt = getSquareColor(squareColor.toString());

                    if (squareShape != null) {
                        squareView.setImageResource(getShapeResource(squareShape.toString()));

                        // TINT logic for the Shape (keeps background white/border black)
                        if (squareShape.toString().equalsIgnoreCase("CHARACTER")) {
                            squareView.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.BLACK));
                        } else {
                            squareView.setImageTintList(android.content.res.ColorStateList.valueOf(colorInt));
                        }
                    }
                } else {
                    squareView.setVisibility(View.INVISIBLE);
                }

                mazeGrid.addView(squareView);
            }
        }
        updateLabelDisplay(levelIndex);
    }

    private int getSquareColor(String colorName) {
        if (colorName == null) return android.graphics.Color.TRANSPARENT;
        return switch (colorName.toUpperCase()) {
            case "RED" -> android.graphics.Color.RED;
            case "BLUE" -> android.graphics.Color.CYAN;
            case "GREEN" -> android.graphics.Color.parseColor("#32CD32");
            case "YELLOW" -> android.graphics.Color.YELLOW;
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
            case "CHARACTER" -> R.drawable.ic_eyeball; // Added your eyeball character
            default -> 0;
        };
    }

    private void updateLabelDisplay(int levelIndex) {
        // 1. Find the views
        TextView txtLevel = findViewById(R.id.txtLevel);
        TextView txtMoves = findViewById(R.id.txtMoves);
        TextView txtGoals = findViewById(R.id.txtGoals);

        // 2. Extract data from the model
        // We use levelIndex + 1 for display because users prefer 1, 2, 3 over 0, 1, 2
        int levelNum = levelIndex + 1;

        // Assuming these methods exist in your Game model
        int moves = viewModel.getGame().getMoveCount();
        int goalsReached = viewModel.getGame().getCompletedGoalCount();
        int goalsTotal = viewModel.getGame().getGoalCount();

        // 3. Set the text
        txtLevel.setText("Level: " + levelNum);
        txtMoves.setText("Moves: " + moves);
        txtGoals.setText("Goals: " + goalsReached + "/" + goalsTotal);
    }
}