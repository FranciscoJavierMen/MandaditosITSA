package com.example.administrador.mandaditostec.Cliente.Pedido;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.administrador.mandaditostec.R;

public class ListaMandaderos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mandaderos);
        Toolbar toolbar = findViewById(R.id.toolbarListaMandaderos);
        setSupportActionBar(toolbar);

    }

    private void detallesMandadero(String id, String nombre){
        try{
            Intent regresarMandadero = new Intent();
            regresarMandadero.putExtra("id_mandadero", id);
            regresarMandadero.putExtra("nombre_mandadero", nombre);
            setResult(Activity.RESULT_OK, regresarMandadero);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
