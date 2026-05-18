package nz.ac.ara.tpm.eyeballmazeas.ui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
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
        createLevelButtons(container);

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

        btnRules.setOnClickListener(v -> showVideoPopup());

    }

    private void createLevelButtons(LinearLayout container) {
        int levelCount = viewModel.getLevelCount();

        for (var i = 0; i < levelCount; i++) {
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
    }

    private void startLevel(int levelIndex) {
        viewModel.startLevel(levelIndex);
        updateMazeDisplay(levelIndex);
    }

    private void updateMazeDisplay(int levelIndex) {
        if (!setupGridBase()) return;

        int rows = viewModel.getLevelHeight();
        int cols = viewModel.getLevelWidth();
        int squareSize = calculateSquareSize(cols);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                View tile = createTile(r, c, squareSize, levelIndex);
                mazeGrid.addView(tile);
            }
        }
        updateLabelDisplay(levelIndex);
    }

    private boolean setupGridBase() {
        if (mazeGrid == null) mazeGrid = findViewById(R.id.mazeGrid);
        mazeGrid.removeAllViews();

        if (viewModel.getGame() == null || viewModel.getLevelWidth() == 0) {
            return false;
        }

        mazeGrid.setRowCount(viewModel.getLevelHeight());
        mazeGrid.setColumnCount(viewModel.getLevelWidth());
        return true;
    }

    private View createTile(int r, int c, int size, int levelIndex) {
        FrameLayout tile = new FrameLayout(this);
        tile.setSoundEffectsEnabled(false);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                GridLayout.spec(r, 1f),
                GridLayout.spec(c, 1f));
        params.width = params.height = size;
        params.setMargins(1, 1, 1, 1);
        tile.setLayoutParams(params);

        Color color = viewModel.getColorAt(r, c);
        if (color == Color.BLANK) {
            tile.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            return tile;
        }

        tile.setBackgroundResource(R.drawable.square_border);

        Shape shape = viewModel.getShapeAt(r, c);
        if (shape != Shape.BLANK) {
            addShapeToTile(tile, shape, color);
        }

        if (viewModel.isGoalActive(r, c)) {
            tile.setBackgroundResource(R.drawable.bg_goal_stripes);
        }

        if (r == viewModel.getEyeballRow() && c == viewModel.getEyeballColumn()) {
            addEyeballToTile(tile);
        }

        tile.setOnClickListener(v -> handleTileClick(r, c, levelIndex));
        return tile;
    }

    private void addShapeToTile(FrameLayout tile, Shape shape, Color color) {
        ImageView img = new ImageView(this);
        img.setLayoutParams(new FrameLayout.LayoutParams(-1, -1)); // MATCH_PARENT
        img.setPadding(12, 12, 12, 12);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        img.setImageResource(viewModel.getShapeResource(shape.toString()));
        img.setImageTintList(android.content.res.ColorStateList.valueOf(viewModel.getSquareColor(color.toString())));
        tile.addView(img);
    }

    private void addEyeballToTile(FrameLayout tile) {
        ImageView eyeball = new ImageView(this);
        eyeball.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        eyeball.setImageResource(R.drawable.ic_eyeball);
        eyeball.setRotation(viewModel.getRotationForDirection(viewModel.getEyeballDirection().toString()));
        tile.addView(eyeball);
    }

    private void handleTileClick(int r, int c, int levelIndex) {
        Message feedback = viewModel.messageIfMovingTo(r, c);

        if (feedback == Message.OK) {
            playSound(R.raw.move);
            viewModel.moveTo(r, c);
            checkCompletion(levelIndex);
        } else {
            playSound(R.raw.invalid_move);
            Toast.makeText(this, viewModel.getFriendlyMessage(feedback), Toast.LENGTH_SHORT).show();
        }
        updateMazeDisplay(levelIndex);
    }

    private int calculateSquareSize(int cols) {
        return switch (cols) {
            case 6 -> 200;
            case 5 -> 230;
            case 4 -> 280;
            default -> 170;
        };
    }

    private void checkCompletion(int levelIndex) {
        int remainingGoals = viewModel.getGoalCount();
        int totalLevels = viewModel.getLevelCount();

        if (remainingGoals == 0) {
            playSound(R.raw.level_complete);

            androidx.appcompat.app.AlertDialog.Builder builder =
                    new androidx.appcompat.app.AlertDialog.Builder(this);

            StringBuilder titleBuilder = new StringBuilder();
            titleBuilder.append("CLEARED: Level ")
                    .append(levelIndex + 1);

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("""
                    Congratualations!
                    Moves: 
                    """)
                    .append(viewModel.getMoveCount());

            builder.setTitle(titleBuilder.toString())
                    .setMessage(messageBuilder.toString())
                    .setCancelable(false);

            if (levelIndex + 1 < totalLevels) {
                builder.setPositiveButton("Next Level", (dialog, which) -> {
                            int next = levelIndex + 1;
                            startLevel(next);
                        })
                        .setNegativeButton("Replay", (dialog, which) -> {
                            startLevel(levelIndex);
                        });

            } else {
                builder.setPositiveButton("Start Over", (dialog, which) -> {
                            startLevel(0);
                        })
                        .setNegativeButton("Close", null);
            }

            builder.show();
        }
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

    private void playSound(int soundRawId) {
        if (isSoundEnabled) {
            MediaPlayer mp = MediaPlayer.create(this, soundRawId);
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.start();
        }
    }

    private void showVideoPopup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.tutorial_video, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();

        VideoView videoView = popupView.findViewById(R.id.popupVideo);
        Button playBtn = popupView.findViewById(R.id.playBtn);
        Button pauseBtn = popupView.findViewById(R.id.pauseBtn);
        Button closeBtn = popupView.findViewById(R.id.closePopup);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.game_tutorial);
        videoView.setVideoURI(videoUri);
        videoView.start();

        playBtn.setOnClickListener(v -> videoView.start());
        pauseBtn.setOnClickListener(v -> videoView.pause());
        closeBtn.setOnClickListener(v -> dialog.dismiss());
    }

}