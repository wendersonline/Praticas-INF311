package com.example.wendersonline.checkin;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaCheckinActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private double latitudeAtual;
    private double longitudeAtual;

    private CheckinDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_checkin);

        latitudeAtual = getIntent().getDoubleExtra("latitude", 0);
        longitudeAtual = getIntent().getDoubleExtra("longitude", 0);

        dbHelper = new CheckinDBHelper(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapCheckin);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // centraliza na posição atual do usuário
        LatLng posicaoAtual = new LatLng(latitudeAtual, longitudeAtual);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicaoAtual, 15));

        carregarMarcadores();
    }

    private void carregarMarcadores() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT c.Local, c.qtdVisitas, c.latitude, c.longitude, cat.nome " +
                "FROM Checkin c INNER JOIN Categoria cat ON c.cat = cat.idCategoria";

        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            String local = cursor.getString(0);
            int qtdVisitas = cursor.getInt(1);
            double lat = Double.parseDouble(cursor.getString(2));
            double lng = Double.parseDouble(cursor.getString(3));
            String nomeCategoria = cursor.getString(4);

            String snippet = "Categoria: " + nomeCategoria + " Visitas: " + qtdVisitas;

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(local)
                    .snippet(snippet));
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuVoltar) {
            finish();
            return true;
        } else if (id == R.id.menuGestao) {
            startActivity(new android.content.Intent(this, GestaoCheckinActivity.class));
            return true;
        } else if (id == R.id.menuRelatorio) {
            startActivity(new android.content.Intent(this, RelatorioActivity.class));
            return true;
        } else if (id == R.id.menuMapaNormal) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        } else if (id == R.id.menuMapaHibrido) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
