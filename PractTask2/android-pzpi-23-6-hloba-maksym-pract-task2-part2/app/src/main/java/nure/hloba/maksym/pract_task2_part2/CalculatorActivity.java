package nure.hloba.maksym.pract_task2_part2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalculatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
    }

    public void onClickNumber(View view) {
        TextView calculatorTextView = findViewById(R.id.calculatorTextView);
        calculatorTextView.setText(calculatorTextView.getText() + ((Button) view).getText().toString());
    }
}