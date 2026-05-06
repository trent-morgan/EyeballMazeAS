package nz.ac.ara.tpm.eyeballmazeas.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import nz.ac.ara.tpm.eyeballmazeas.R;
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

        // 2. Loop and create buttons manually
//        int levelCount = viewModel.getGame().getLevelCount();
        int levelCount = 3;

        for (int i = 0; i < levelCount; i++) {
            int levelNum = i + 1;

            // Create a new Button programmatically
            Button btn = new Button(this);

            // Set size to keep it square (roughly 60dp converted to pixels)
            int size = (int) (60 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);

            // Set text and click logic
            btn.setText(String.valueOf(levelNum));
            btn.setOnClickListener(v -> {
                Log.d("LEVEL_CLICK", "Loading: " + levelNum);
                viewModel.loadLevelData(0);
                // Refresh your board here if needed
            });

            // Add the button to the layout
            container.addView(btn);
        }
    }
}