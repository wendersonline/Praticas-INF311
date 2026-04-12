package com.example.calculator.calculatora.wendersonline;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    EditText num_1;
    EditText num_2;
    TextView result;
    TextView resultText;

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

        num_1 = (EditText) findViewById(R.id.editText1);
        num_2 = (EditText) findViewById(R.id.editText2);
        result = (TextView) findViewById(R.id.result);
        resultText = (TextView) findViewById(R.id.resultText);
    }

    public void onOperation(View v) {
        String firstValueS = num_1.getText().toString().trim();
        String secondValueS = num_2.getText().toString().trim();

        if (firstValueS.isEmpty() || secondValueS.isEmpty()) {
            resultText.setText(getString(R.string.resultAlertValue));
            resultText.setVisibility(View.VISIBLE);
            result.setVisibility(View.INVISIBLE);
            return;
        } else if (!firstValueS.matches("-?[0-9]+") || !secondValueS.matches("-?[0-9]+")) {
            resultText.setText(getString(R.string.resultAlertNotNumber));
            resultText.setVisibility(View.VISIBLE);
            result.setVisibility(View.INVISIBLE);
            return;
        }

        int firstValue = Integer.parseInt(firstValueS);
        int secondValue = Integer.parseInt(secondValueS);
        double resultValue = 0;

        switch (v.getTag().toString()) {
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
                    resultText.setText(getString(R.string.resultAlertZero));
                    result.setVisibility(View.INVISIBLE);
                    return;
                }
                else {
                    resultValue = (double) firstValue / secondValue;
                    break;
                }
        }

        resultText.setText(getString(R.string.resultText));
        result.setText(String.valueOf(resultValue));
        result.setVisibility(View.VISIBLE);
    }
}