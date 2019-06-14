package com.exemple.administrador.mandaditostec.mandadero.MapaMandadero;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.administrador.mandaditostec.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapaMandadero extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double lat = 0.0, lng = 0.0;
    private Marker marcador;
    Polyline polyline1,polyline2;

    double latdestino,lngdestino,latorigen,lngorigen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_mandadero);

        latdestino = Double.parseDouble(getIntent().getStringExtra("latitud"));
        lngdestino = Double.parseDouble(getIntent().getStringExtra("longitud"));
        latorigen = Double.parseDouble(getIntent().getStringExtra("latitudorigen"));
        lngorigen = Double.parseDouble(getIntent().getStringExtra("longitudorigen"));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        miUbicacion();

        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(latorigen, lngorigen),
                        new LatLng(latdestino,lngdestino)));

        // Add a marker in Sydney and move the camera
        LatLng origen = new LatLng(latorigen, lngorigen);
        LatLng destino = new LatLng(latdestino,lngdestino);
        mMap.addMarker(new MarkerOptions().position(origen).title("Origen del mandado"));
        mMap.addMarker(new MarkerOptions().position(destino).title("destino del mandado"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 13));


        //mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
    }

    private void agregarMarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miubicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 18);
        if (polyline2 != null)
            polyline2.remove();

        polyline2 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(lat,lng),
                        new LatLng(latorigen,lngorigen)));
        mMap.animateCamera(miubicacion);
    }

    private void actualizaUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregarMarcador(lat, lng);
        }
    }

    private void miUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizaUbicacion(location);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,loclistener);
    }

    LocationListener loclistener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizaUbicacion(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
