package com.example.wendersonline.checkin;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RelatorioActivity extends AppCompatActivity {

    private LinearLayout layoutLocais;
    private LinearLayout layoutVisitas;
    private CheckinDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        layoutLocais = findViewById(R.id.layoutLocais);
        layoutVisitas = findViewById(R.id.layoutVisitas);
        dbHelper = new CheckinDBHelper(this);

        montarLista();
    }

    private void montarLista() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // ordenado de forma decrescente pela quantidade de visitas
        Cursor cursor = db.rawQuery(
                "SELECT Local, qtdVisitas FROM Checkin ORDER BY qtdVisitas DESC", null);

        while (cursor.moveToNext()) {
            String local = cursor.getString(0);
            int qtdVisitas = cursor.getInt(1);

            TextView tvLocal = new TextView(this);
            tvLocal.setText(local);
            tvLocal.setPadding(0, 24, 0, 24);
            tvLocal.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutLocais.addView(tvLocal);

            TextView tvVisitas = new TextView(this);
            tvVisitas.setText(String.valueOf(qtdVisitas));
            tvVisitas.setPadding(24, 24, 0, 24);
            tvVisitas.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutVisitas.addView(tvVisitas);
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_voltar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuVoltar) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}