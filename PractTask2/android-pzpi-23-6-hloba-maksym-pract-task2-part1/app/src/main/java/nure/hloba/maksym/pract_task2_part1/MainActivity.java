package nure.hloba.maksym.pract_task2_part1;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private boolean isColorChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View view) {
        TextView androidColorTextView = findViewById(R.id.androidColorTextView);
        ImageView imageView = findViewById(R.id.imageView);
        if (isColorChanged) {
            androidColorTextView.setText("Android color green!");
            imageView.setImageTintList(ColorStateList.valueOf(Color.GREEN));
        } else {
            androidColorTextView.setText("Android color red!");
            imageView.setImageTintList(ColorStateList.valueOf(Color.RED));
        }
        isColorChanged = !isColorChanged;
    }

    public void showMessage(View view) {
        Toast.makeText(this, "Message is showing", Toast.LENGTH_SHORT).show();
    }
}