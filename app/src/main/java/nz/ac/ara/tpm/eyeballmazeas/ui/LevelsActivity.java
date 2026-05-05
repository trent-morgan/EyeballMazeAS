package nz.ac.ara.tpm.eyeballmazeas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import nz.ac.ara.tpm.eyeballmazeas.R;
import nz.ac.ara.tpm.eyeballmazeas.model.LevelFactory;
import nz.ac.ara.tpm.eyeballmazeas.viewmodel.GameViewModel;

public class LevelsActivity extends AppCompatActivity {
    private GameViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.levels), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);
        LevelFactory.loadAllLevelSkeletons(viewModel.getGame());

        LinearLayout mainContainer = findViewById(R.id.layout_button_container);
        int totalLevels = viewModel.getGame().getLevelCount();
        int cols = 5;
        LinearLayout currentRow = null;

        for (int i = 0; i < totalLevels; i++) {
            if (i % cols == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                mainContainer.addView(currentRow);
            }
            Button btn = new Button(this);
            btn.setText(String.valueOf(i + 1));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, // Width 0 because weight handles it
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f // Weight 1
            );

            params.setMargins(4, 4, 4, 4);
            btn.setLayoutParams(params);

            final int levelIndex = i;
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(LevelsActivity.this, GameActivity.class);
                intent.putExtra("SELECTED_LEVEL", levelIndex);
                startActivity(intent);
            });

            if (currentRow != null) {
                currentRow.addView(btn);
            }
        }

    }

}
