package com.example.wendersonline.checkin;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_LOCATION = 100;

    private AutoCompleteTextView autoCompleteLocal;
    private Spinner spinnerCategoria;
    private TextView tvLatitude, tvLongitude;
    private Button btnCheckin;

    private CheckinDBHelper dbHelper;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Double latitudeAtual = null;
    private Double longitudeAtual = null;

    // guarda o id da categoria (chave primária) correspondente à posição do spinner
    private List<Integer> idsCategorias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteLocal = findViewById(R.id.autoCompleteLocal);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        btnCheckin = findViewById(R.id.btnCheckin);

        dbHelper = new CheckinDBHelper(this);

        carregarCategorias();
        carregarLocaisVisitados();
        configurarLocalizacao();

        btnCheckin.setOnClickListener(v -> realizarCheckin());
    }

    // Spinner de categorias
    private void carregarCategorias() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT idCategoria, nome FROM Categoria ORDER BY idCategoria ASC", null);

        List<String> nomes = new ArrayList<>();
        idsCategorias.clear();

        while (cursor.moveToNext()) {
            idsCategorias.add(cursor.getInt(0));
            nomes.add(cursor.getString(1));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nomes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }

    // AutoComplete com locais já visitados
    private void carregarLocaisVisitados() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Local FROM Checkin", null);

        List<String> locais = new ArrayList<>();
        while (cursor.moveToNext()) {
            locais.add(cursor.getString(0));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, locais);
        autoCompleteLocal.setAdapter(adapter);
    }

    // Localização em tempo real
    private void configurarLocalizacao() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    latitudeAtual = location.getLatitude();
                    longitudeAtual = location.getLongitude();
                    tvLatitude.setText(String.valueOf(latitudeAtual));
                    tvLongitude.setText(String.valueOf(longitudeAtual));
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
        } else {
            iniciarAtualizacaoLocalizacao(locationRequest);
        }
    }

    private void iniciarAtualizacaoLocalizacao(LocationRequest locationRequest) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            iniciarAtualizacaoLocalizacao(locationRequest);
        } else {
            Toast.makeText(this, "Permissão de localização é necessária", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    // Lógica de check-in
    private void realizarCheckin() {
        String local = autoCompleteLocal.getText().toString().trim();

        if (local.isEmpty()) {
            Toast.makeText(this, "Informe o nome do local", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spinnerCategoria.getSelectedItem() == null) {
            Toast.makeText(this, "Selecione uma categoria", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitudeAtual == null || longitudeAtual == null) {
            Toast.makeText(this, "Aguardando localização do dispositivo...", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT qtdVisitas FROM Checkin WHERE Local = ?",
                new String[]{local});

        if (cursor.moveToFirst()) {
            // já existe -> UPDATE (ignora categoria e nova posição)
            int qtdAtual = cursor.getInt(0);
            ContentValues cv = new ContentValues();
            cv.put("qtdVisitas", qtdAtual + 1);
            db.update("Checkin", cv, "Local = ?", new String[]{local});
        } else {
            // não existe -> INSERT
            int idCategoriaSelecionada = idsCategorias.get(spinnerCategoria.getSelectedItemPosition());
            ContentValues cv = new ContentValues();
            cv.put("Local", local);
            cv.put("qtdVisitas", 1);
            cv.put("cat", idCategoriaSelecionada);
            cv.put("latitude", String.valueOf(latitudeAtual));
            cv.put("longitude", String.valueOf(longitudeAtual));
            db.insert("Checkin", null, cv);
        }
        cursor.close();

        Toast.makeText(this, "Check-in realizado!", Toast.LENGTH_SHORT).show();

        // fecha e reabre a tela para dar sensação de atualização
        finish();
        startActivity(getIntent());
    }

    // ---------- Menu ----------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuMapa) {
            if (latitudeAtual == null || longitudeAtual == null) {
                Toast.makeText(this, "Localização ainda não obtida", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, MapaCheckinActivity.class);
                intent.putExtra("latitude", latitudeAtual);
                intent.putExtra("longitude", longitudeAtual);
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.menuGestao) {
            startActivity(new Intent(this, GestaoCheckinActivity.class));
            return true;
        } else if (id == R.id.menuRelatorio) {
            startActivity(new Intent(this, RelatorioActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}