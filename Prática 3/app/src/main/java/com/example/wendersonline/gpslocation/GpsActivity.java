package com.example.wendersonline.gpslocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.fragment.app.FragmentActivity;

public class GpsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap map;
    private Marker marcadorAtual = null;
    private FusedLocationProviderClient fusedLocationClient;
    private int opcao = 0;

    private LatLng[] pontos  = new LatLng[3];
    private String[] nomes   = new String[3];
    private boolean  dadosOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.gps_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gps_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        opcao = getIntent().getIntExtra("opcao", 0);

        carregarPontosDoBanco();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void carregarPontosDoBanco() {
        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT descricao, latitude, longitude FROM Location ORDER BY id", null);

        int i = 0;
        while (cursor.moveToNext() && i < 3) {
            nomes[i]  = cursor.getString(0);
            pontos[i] = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
            i++;
        }
        cursor.close();
        db.close();
        dadosOk = (i == 3);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (!dadosOk) {
            Toast.makeText(this, "Erro ao carregar pontos do banco.", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < 3; i++) {
            map.addMarker(new MarkerOptions().position(pontos[i]).title(nomes[i]));
        }

        centralizarMapa(opcao);
    }

    private void centralizarMapa(int opcao) {
        if (!dadosOk) return;

        int zoom;
        if (opcao == 0)      zoom = 14;
        else if (opcao == 1) zoom = 16;
        else                 zoom = 17;

        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pontos[opcao], zoom));
    }

    public void get_location(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location == null) {
                        Toast.makeText(this,
                                "Não foi possível obter a localização. Tente novamente.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    LatLng ATUAL = new LatLng(location.getLatitude(), location.getLongitude());

                    if (marcadorAtual != null) marcadorAtual.remove();

                    marcadorAtual = map.addMarker(new MarkerOptions()
                            .position(ATUAL)
                            .title("Minha localização atual")
                            .icon(BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_AZURE)));

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(ATUAL, 17));

                    float[] resultado = new float[1];
                    Location.distanceBetween(
                            location.getLatitude(), location.getLongitude(),
                            pontos[opcao].latitude, pontos[opcao].longitude,
                            resultado);
                    int distancia = Math.round(resultado[0]);

                    Toast.makeText(this,
                            "Você está a " + distancia + " m de " + nomes[opcao] + ".",
                            Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                get_location(null);
            } else {
                Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void go_born_city(View v) { opcao = 0; centralizarMapa(0); }
    public void go_vicosa(View v)    { opcao = 1; centralizarMapa(1); }
    public void go_dpi(View v)       { opcao = 2; centralizarMapa(2); }

    public void back_to_list(View v) {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}