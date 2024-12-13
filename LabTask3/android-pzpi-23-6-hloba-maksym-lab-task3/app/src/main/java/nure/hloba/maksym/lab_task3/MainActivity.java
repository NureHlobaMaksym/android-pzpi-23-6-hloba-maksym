package nure.hloba.maksym.lab_task3;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView result;
    private final Calculator calculator = new Calculator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        result = findViewById(R.id.result);
        Button clearButton = findViewById(R.id.clearButton);
        Button changeSignButton = findViewById(R.id.changeSignButton);
        Button backspaceButton = findViewById(R.id.backspaceButton);
        Button divisionButton = findViewById(R.id.divisionButton);
        Button sevenButton = findViewById(R.id.sevenButton);
        Button eightButton = findViewById(R.id.eightButton);
        Button nineButton = findViewById(R.id.nineButton);
        Button multiplicationButton = findViewById(R.id.multiplicationButton);
        Button fourButton = findViewById(R.id.fourButton);
        Button fiveButton = findViewById(R.id.fiveButton);
        Button sixButton = findViewById(R.id.sixButton);
        Button minusButton = findViewById(R.id.minusButton);
        Button oneButton = findViewById(R.id.oneButton);
        Button twoButton = findViewById(R.id.twoButton);
        Button treeButton = findViewById(R.id.treeButton);
        Button plusButton = findViewById(R.id.plusButton);
        Button zeroButton = findViewById(R.id.zeroButton);
        Button dotButton = findViewById(R.id.dotButton);
        Button equalsButton = findViewById(R.id.equalsButton);
        List<Button> numbersButtonsList = Arrays.asList(
                zeroButton, oneButton, twoButton,
                treeButton, fourButton, fiveButton,
                sixButton, sevenButton, eightButton,
                nineButton
        );
        for (Button numberButton : numbersButtonsList) {
            numberButton.setOnClickListener(view -> {
                int number = Integer.parseInt(numberButton.getText().toString());
                calculator.addDigitToOperandEnd(number);
                updateResult();
            });
        }
        clearButton.setOnClickListener(view -> {
            calculator.reset();
            updateResult();
        });
        backspaceButton.setOnClickListener(view -> {
            calculator.removeLastOperandDigit();
            updateResult();
        });
        changeSignButton.setOnClickListener(view -> {
            calculator.changeOperandSign();
            updateResult();
        });
        dotButton.setOnClickListener(view -> {
            calculator.addDot();
            updateResult();
        });
        divisionButton.setOnClickListener(view -> {
            equalsButton.callOnClick();
            calculator.setOperation(Operation.DIVIDE);
        });
        multiplicationButton.setOnClickListener(view -> {
            equalsButton.callOnClick();
            calculator.setOperation(Operation.MULTIPLY);
        });
        minusButton.setOnClickListener(view -> {
            equalsButton.callOnClick();
            calculator.setOperation(Operation.MINUS);
        });
        plusButton.setOnClickListener(view -> {
            equalsButton.callOnClick();
            calculator.setOperation(Operation.PLUS);
        });
        equalsButton.setOnClickListener(view -> {
            calculator.calculateResult(new CalculatorResultCallback() {
                @Override
                public void onDivisionByZeroError() {
                    setResultText("Not a number");
                }

                @Override
                public void onSuccess() {
                    updateResult();
                }
            });
        });
    }

    private void updateResult() {
        setResultText(calculator.getFormatedResult());
    }

    private void setResultText(String text) {
        result.setText(text);
        if (text.length() < 10) {
            result.setTextSize(TypedValue.COMPLEX_UNIT_SP, 75f);
        } else if (text.length() < 12) {
            result.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60f);
        } else if (text.length() < 16) {
            result.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45f);
        } else {
            result.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35f);
        }
    }
}