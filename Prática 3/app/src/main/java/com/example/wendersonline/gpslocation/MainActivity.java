package com.example.wendersonline.gpslocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

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

        dbHelper = new DatabaseHelper(this);
    }

    public void close(View v) {
        finish();
    }

    public void view_location(View v) {
        Intent it = new Intent(this, GpsActivity.class);

        int opcao;
        String mensagem;

        if (v.getId() == R.id.born_city) {
            opcao    = 0;
            mensagem = "Cidade Natal";
        } else if (v.getId() == R.id.house_vicosa) {
            opcao    = 1;
            mensagem = "Viçosa";
        } else {
            opcao    = 2;
            mensagem = "DPI";
        }

        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();

        dbHelper.inserirLog(opcao);

        it.putExtra("opcao", opcao);
        close(v);
        startActivity(it);
    }

    public void view_report(View v) {
        Intent it = new Intent(this, ReportActivity.class);
        close(v);
        startActivity(it);
    }
}