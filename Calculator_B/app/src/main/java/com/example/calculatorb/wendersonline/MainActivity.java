package com.example.calculatorb.wendersonline;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    EditText visor;
    String operator = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        visor = (EditText) findViewById(R.id.visor);
    }

    public void pressNumber(View v) {
        String lastNums = visor.getText().toString();
        if (!lastNums.equals(getString(R.string.error))) {
            String newNums = lastNums + v.getTag().toString();
            visor.setText(newNums);
        } else {
            visor.setText(v.getTag().toString());
        }
    }

    public void pressDot(View v) {
        String lastNums = visor.getText().toString();

        if (lastNums.matches(".*\\d$") && !lastNums.matches(".*\\.[^+\\-*/]*$")) {
            String newNums = lastNums + v.getTag().toString();
            visor.setText( newNums );
        }
    }

    public void pressOperator(View v) {
        String lastNums = visor.getText().toString();

        if (!lastNums.isEmpty() && !lastNums.equals(getString(R.string.error)) && !lastNums.matches(".*[+\\-*/].*")) {
            String newNums = lastNums + v.getTag().toString();
            visor.setText( newNums );
            operator = v.getTag().toString();
        }
    }

    public void eraseNumber(View v) {
        if (v.getTag().toString().equals("C")) {
            visor.setText(" ");
        } else  {
            String lastNums = visor.getText().toString();
            if (!lastNums.isEmpty()) {
                lastNums = lastNums.substring(0, lastNums.length() - 1);
            }

            visor.setText(lastNums);
        }
    }

    public void pressEqual(View v) {
        String allOperation = visor.getText().toString();

        if (allOperation.isEmpty()) {
            visor.setText(R.string.error);
            return;
        }

        String[] nums = allOperation.split(Pattern.quote(operator));

        if (nums.length < 2) {
            visor.setText(R.string.error);
            return;
        }

        double firstValue = Double.parseDouble(nums[0]);
        double secondValue = Double.parseDouble(nums[1]);
        double resultValue = 0;

        switch (operator) {
            case "+":
                resultValue = firstValue + secondValue;
                break;

            case "-":
                resultValue = firstValue - secondValue;
                break;

            case "*":
                resultValue = firstValue * secondValue;
                break;

            case "/":
                if (secondValue == 0) {
                    visor.setText(getString(R.string.error));
                    return;
                }
                else {
                    resultValue = (double) firstValue / secondValue;
                    break;
                }
        }

        visor.setText(String.valueOf(resultValue));
    }
}