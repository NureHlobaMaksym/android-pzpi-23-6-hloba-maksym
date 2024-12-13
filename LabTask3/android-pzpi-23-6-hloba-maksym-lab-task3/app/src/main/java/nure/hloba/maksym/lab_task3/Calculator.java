package nure.hloba.maksym.lab_task3;

import android.util.Log;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Calculator {
    private BigDecimal operand = BigDecimal.ZERO;
    private boolean isDecimalPartActive;
    private boolean isTrailingDotVisible;
    private int trailingZeroesCount;
    private Operation operation;
    private static final int MAX_NUMBER_LENGTH = 19;
    private boolean isOperandEntered;
    private BigDecimal calculatedPart;

    public void addDigitToOperandEnd(int digit) {
        if (isOperandEntered) {
            isOperandEntered = false;
            isDecimalPartActive = false;
            isTrailingDotVisible = false;
            trailingZeroesCount = 0;
            calculatedPart = operand;
            operand = BigDecimal.ZERO;
        }
        if ((operand.toString() + "0".repeat(trailingZeroesCount)).length() == MAX_NUMBER_LENGTH) {
            return;
        }
        if (isTrailingDotVisible) {
            isDecimalPartActive = true;
        }
        isTrailingDotVisible = false;
        BigDecimal digitDecimal = BigDecimal.valueOf(digit);
        if (isDecimalPartActive) {
            if (digitDecimal.compareTo(BigDecimal.ZERO) == 0) {
                trailingZeroesCount++;
            }
            BigDecimal newDecimalPart = digitDecimal.divide(BigDecimal.TEN.pow(getDecimalPartLength(operand) + 1).multiply(BigDecimal.TEN.pow(trailingZeroesCount)));
            if (operand.compareTo(BigDecimal.ZERO) == -1) {
                newDecimalPart = newDecimalPart.negate();
            }
            operand = operand.add(newDecimalPart);
            if (trailingZeroesCount > 0 && digitDecimal.compareTo(BigDecimal.ZERO) != 0) {
                trailingZeroesCount = 0;
            }
        } else {
            if (operand.compareTo(BigDecimal.ZERO) >= 0) {
                operand = operand.multiply(BigDecimal.TEN).add(digitDecimal);
            } else {
                operand = operand.multiply(BigDecimal.TEN).add(digitDecimal.negate());
            }
        }
    }

    public void reset() {
        isDecimalPartActive = false;
        isTrailingDotVisible = false;
        calculatedPart = null;
        operation = null;
        trailingZeroesCount = 0;
        operand = BigDecimal.ZERO;
    }

    public void removeLastOperandDigit() {
        if (isTrailingDotVisible && trailingZeroesCount == 0) {
            isTrailingDotVisible = false;
            isDecimalPartActive = false;
        } else {
            if (isDecimalPartActive) {
                int decimalPartLength = getDecimalPartLength(operand);
                String resultDecimalPart = getFormatedResult().split("\\.")[1];
                Log.d("calculator", removeHeadingZeroes(resultDecimalPart));
                if (removeHeadingZeroes(resultDecimalPart).length() <= 1) {
                    isTrailingDotVisible = true;
                }
                if (trailingZeroesCount > 0) {
                    trailingZeroesCount--;
                } else {
                    updateTrailingZeroesCount();
                    operand = operand.subtract(((operand.multiply(BigDecimal.TEN.pow(decimalPartLength)).remainder(BigDecimal.TEN)).divide(BigDecimal.TEN.pow(decimalPartLength)))).stripTrailingZeros();
                }
            } else {
                operand = operand.subtract(operand.remainder(BigDecimal.TEN)).divide(BigDecimal.TEN);
            }
        }
    }

    private void updateTrailingZeroesCount() {
        String numberPart = getStringDecimalPart(operand);
        for (int i = numberPart.length() - 2; i >= 0; i--) {
            char digit = numberPart.charAt(i);
            if (digit == '0') {
                trailingZeroesCount++;
            } else {
                break;
            }
        }
    }

    public void changeOperandSign() {
        operand = operand.negate();
    }

    public void addDot() {
        if (isDecimalPartActive) {
            return;
        }
        isTrailingDotVisible = true;
    }

    public void calculateResult(CalculatorResultCallback callback) {
        if (operation == null || calculatedPart == null) {
            return;
        }
        boolean isDividingByZero = false;
        switch (operation) {
            case DIVIDE:
                if (operand.compareTo(BigDecimal.ZERO) == 0) {
                    isDividingByZero = true;
                    operand = BigDecimal.ZERO;
                } else {
                    MathContext mathContext = new MathContext(MAX_NUMBER_LENGTH, RoundingMode.HALF_UP);
                    operand = calculatedPart.divide(operand, mathContext);
                }
                break;
            case MULTIPLY:
                operand = calculatedPart.multiply(operand);
                break;
            case MINUS:
                operand = calculatedPart.subtract(operand);
                break;
            case PLUS:
                operand = calculatedPart.add(operand);
                break;
        }
        operand = operand.stripTrailingZeros();
        if (getDecimalPartLength(operand) > 0) {
            isDecimalPartActive = true;
        }
        calculatedPart = null;
        operation = null;
        if (isDividingByZero) {
            callback.onDivisionByZeroError();
        } else {
            callback.onSuccess();
        }
    }

    public String getFormatedResult() {
        String trailingText = isTrailingDotVisible || (trailingZeroesCount > 0 && getDecimalPartLength(operand) == 0) ? "." : "";
        for (int i = 0; i < trailingZeroesCount; i++) {
            trailingText += "0";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.#################");
        return decimalFormat.format(operand) + trailingText;
    }

    private int getDecimalPartLength(BigDecimal value) {
        return getStringDecimalPart(value).length();
    }

    private String getStringDecimalPart(BigDecimal value) {
        String stringValue = value.toString();
        String[] valueParts = stringValue.split("\\.");
        if (valueParts.length == 1) {
            return "";
        }
        String decimalPart = valueParts[1];
        if (decimalPart.equals("0")) {
            return "";
        } else {
            return decimalPart;
        }
    }

    private String removeHeadingZeroes(String input) {
        int i = 0;
        while (i < input.length() && input.charAt(i) == '0') {
            i++;
        }
        return input.substring(i);
    }

    public void setOperation(Operation operation) {
        isOperandEntered = true;
        this.operation = operation;
    }
}
