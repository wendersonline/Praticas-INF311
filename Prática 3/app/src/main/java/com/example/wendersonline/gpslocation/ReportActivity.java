package com.example.wendersonline.gpslocation;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.buscarTodosLogs();

        ArrayList<String> itens   = new ArrayList<>();
        ArrayList<Integer> logIds = new ArrayList<>();

        while (cursor.moveToNext()) {
            int    id        = cursor.getInt(0);
            String msg       = cursor.getString(1);
            String timestamp = cursor.getString(2);
            itens.add(msg + " - " + timestamp);
            logIds.add(id);
        }
        cursor.close();

        ListView listView = findViewById(R.id.list_logs);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, itens);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int logId = logIds.get(position);
            Cursor c = db.buscarLatLngDoLog(logId);
            if (c.moveToFirst()) {
                double lat = c.getDouble(0);
                double lng = c.getDouble(1);
                Toast.makeText(this,
                        "Latitude: " + lat + "\nLongitude: " + lng,
                        Toast.LENGTH_LONG).show();
            }
            c.close();
        });
    }

    public void back_to_list(View v) {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}