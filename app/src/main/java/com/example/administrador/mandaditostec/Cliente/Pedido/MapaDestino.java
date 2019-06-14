package com.example.administrador.mandaditostec.Cliente.Pedido;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.example.administrador.mandaditostec.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaDestino extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private FloatingActionButton fabCerrar;

    private String direccion;
    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_destino);

        fabCerrar = findViewById(R.id.fabCerrarMapa);
        fabCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent i = getIntent();
        lat = i.getDoubleExtra("latitud", 1);
        lon = i.getDoubleExtra("longitud", 1);
        direccion = i.getStringExtra("direccion");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapita);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        // Add a marker in Sydney and move the camera
        LatLng direction = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(direction).title(direccion).snippet("Dirección de entrega"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(direction, 16f));

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setAllGesturesEnabled(true);
    }
}
