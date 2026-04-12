package com.example.wendersonline.imccalculator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RelatoryScreen extends MainActivity {
    TextView name;
    TextView age;
    TextView weight;
    TextView height;
    TextView imc;
    TextView classification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.relatory_screen);

        Log.i("Ciclo De Vida", getClassName() + ".onCreate() chamado.");

        name = (TextView) findViewById(R.id.name_result);
        age = (TextView) findViewById(R.id.age_result);
        weight = (TextView) findViewById(R.id.weight_result);
        height = (TextView) findViewById(R.id.height_result);
        imc = (TextView) findViewById(R.id.imc_result);
        classification = (TextView) findViewById(R.id.classification_result);

        Intent it = getIntent();
        Bundle user_infos = it.getExtras();

        name.setText(user_infos.getString("name"));
        age.setText(user_infos.getString("age"));
        weight.setText(user_infos.getString("weight"));
        height.setText(user_infos.getString("height"));

        float weight_int = Float.parseFloat(user_infos.getString("weight").replace(",", "."));
        float height_int = Float.parseFloat(user_infos.getString("height").replace(",", "."));

        float imc_result = weight_int / (height_int * height_int);

        String classify;

        if (imc_result < 18.5) {
            classify = "Abaixo do peso";
            classification.setTextColor(Color.BLUE);
        } else if (imc_result >= 18.5 && imc_result < 24.9) {
            classify = "Saudável";
            classification.setTextColor(Color.GREEN);
        } else if (imc_result >= 25 && imc_result < 29.9) {
            classify = "Sobrepeso";
            classification.setTextColor(Color.parseColor("#eead2d"));
        } else if (imc_result >= 30 && imc_result < 34.9) {
            classify = "Obesidade Grau I";
            classification.setTextColor(Color.parseColor("#eead2d"));
        } else if (imc_result >= 35 && imc_result < 39.9) {
            classify = "Obesidade Grau II (Severa)";
            classification.setTextColor(Color.RED);
        } else {
            classify = "Obesidade Grau III (Mórbida)";
            classification.setTextColor(Color.RED);
        }


        imc.setText((Math.round(imc_result * 100.0f) / 100.0) + "/m²");
        classification.setText(classify);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void back_fun (View v) {
        finish();
    }

    protected String getClassName(){
        String s = getClass().getName();
        return s;
    }

    protected void onStart(){
        super.onStart();
        Log.i("Ciclo De Vida", getClassName() + ".onStart() chamado.");
    }

    protected void onRestart(){
        super.onRestart();
        Log.i("Ciclo De Vida", getClassName() + ".onRestart() chamado.");
    }

    protected void onResume(){
        super.onResume();
        Log.i("Ciclo De Vida", getClassName() + ".onResume() chamado.");
    }

    protected void onPause(){
        super.onPause();
        Log.i("Ciclo De Vida", getClassName() + ".onPause() chamado.");
    }

    protected void onStop(){
        super.onStop();
        Log.i("Ciclo De Vida", getClassName() + ".onStop() chamado.");
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.i("Ciclo De Vida", getClassName() + ".onDestroy() chamado.");
    }
}
