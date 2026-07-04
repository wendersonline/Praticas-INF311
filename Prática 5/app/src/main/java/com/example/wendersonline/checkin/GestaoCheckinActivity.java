package com.example.wendersonline.checkin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GestaoCheckinActivity extends AppCompatActivity {

    private LinearLayout layoutConteudo;
    private LinearLayout layoutDeletar;
    private CheckinDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao_checkin);

        layoutConteudo = findViewById(R.id.layoutConteudo);
        layoutDeletar = findViewById(R.id.layoutDeletar);
        dbHelper = new CheckinDBHelper(this);

        montarLista();
    }

    private void montarLista() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Local FROM Checkin", null);

        while (cursor.moveToNext()) {
            String local = cursor.getString(0);

            // TextView com o nome do local
            TextView tv = new TextView(this);
            tv.setText(local);
            tv.setPadding(0, 24, 0, 24);
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutConteudo.addView(tv);

            // ImageButton para deletar, com tag = chave primária (Local)
            ImageButton btnDeletar = new ImageButton(this);
            btnDeletar.setImageResource(android.R.drawable.ic_delete);
            btnDeletar.setTag(local);
            btnDeletar.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            btnDeletar.setOnClickListener(this::onDeletarClick);
            layoutDeletar.addView(btnDeletar);
        }
        cursor.close();
    }

    // Único método tratando o clique de todos os botões (primeira forma de tratar eventos)
    public void onDeletarClick(android.view.View view) {
        String local = (String) view.getTag();
        confirmarExclusao(local);
    }

    private void confirmarExclusao(String local) {
        new AlertDialog.Builder(this)
                .setTitle("Exclusão")
                .setMessage("Tem certeza que deseja excluir " + local + "?")
                .setNegativeButton("NÃO", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("SIM", (dialog, which) -> excluirLocal(local))
                .show();
    }

    private void excluirLocal(String local) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Checkin", "Local = ?", new String[]{local});

        // fecha e reabre a tela para dar sensação de atualização
        finish();
        startActivity(getIntent());
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