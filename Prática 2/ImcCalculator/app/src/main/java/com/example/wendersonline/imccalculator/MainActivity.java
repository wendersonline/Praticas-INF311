package com.example.wendersonline.imccalculator;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText name;
    EditText age;
    EditText weigth;
    EditText height;
    TextView error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Log.i("Ciclo De Vida", getClassName() + ".onCreate() chamado.");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = (EditText) findViewById(R.id.name_edit_text);
        age = (EditText) findViewById(R.id.age_edit_text);
        weigth = (EditText) findViewById(R.id.weight_edit_text);
        height = (EditText) findViewById(R.id.height_edit_text);
        error = (TextView) findViewById(R.id.error);
    }


    public void click_relatory(View v) {

        if (weigth.getText().toString().isEmpty() || height.getText().toString().isEmpty()) {
            error.setVisibility(VISIBLE);
            return;
        }

        Intent it = new Intent(getBaseContext(), RelatoryScreen.class);

        it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        Bundle infos_user = new Bundle();

        infos_user.putString("name", name.getText().toString());
        infos_user.putString("age", age.getText().toString());
        infos_user.putString("weight", weigth.getText().toString());
        infos_user.putString("height", height.getText().toString());

        if (error.getVisibility() == v.VISIBLE) {
            error.setVisibility(INVISIBLE);
        }

        it.putExtras(infos_user);
        startActivity(it);
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