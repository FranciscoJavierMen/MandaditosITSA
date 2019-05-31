package com.example.administrador.mandaditostec.Cliente.Pedido;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.Cliente.Pedido.EstadoPedido.Pendiente;
import com.example.administrador.mandaditostec.Cliente.Pedido.EstadoPedido.TabPedidos;
import com.example.administrador.mandaditostec.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.administrador.mandaditostec.Cliente.Pedido.FormDialog.TAG;

public class DetallesPedido extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "Detalles del pedido";
    private Toolbar toolbar;

    //Declaración de las vistas
    private TextView txtPedido, txtDireccion, txtFecha, txtMandadero, txtEstado;
    private FloatingActionButton fabEstado, fabMapa, fabRecepcion;
    private AppCompatButton btnConfirmar;

    //Firebase
    private DatabaseReference databaseReference;

    //Variables para recepción de datos
    private String id, pedido, fecha, mandadero, direccion, estado;
    private double lat, lon;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detalles_pedido);
        toolbar = findViewById(R.id.toolbarDetallesPedido);
        toolbar.setTitle(TAG);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try{
            Intent i = getIntent();
            id = i.getStringExtra("id");
            pedido = i.getStringExtra("pedido");
            fecha = i.getStringExtra("fecha");
            mandadero = i.getStringExtra("mandadero");
            direccion = i.getStringExtra("direccion");
            estado = i.getStringExtra("estado");
            lat = i.getDoubleExtra("latitud", 1);
            lon = i.getDoubleExtra("longitud", 1);

        }catch (Exception e){
            e.printStackTrace();
        }

        inicializar();
        setValues();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pedido");

        btnConfirmar.setOnClickListener(this);
        fabMapa.setOnClickListener(this);
    }

    //Inicializar lo componentes gráficos
    private void inicializar(){
        txtPedido = findViewById(R.id.textPedido);
        txtDireccion = findViewById(R.id.textDireccion);
        txtFecha = findViewById(R.id.textFecha);
        txtMandadero = findViewById(R.id.textMandadero);
        txtEstado = findViewById(R.id.textEstado);
        fabEstado = findViewById(R.id.fabEstado);
        fabMapa = findViewById(R.id.fabMapaDestino);
        fabRecepcion = findViewById(R.id.fabRecepcion);
        btnConfirmar = findViewById(R.id.btnConfirmarRecepcion);
    }

    //establecer los valores
    private void setValues(){
        txtPedido.setText(pedido);
        txtFecha.setText(fecha);
        txtMandadero.setText(mandadero);
        txtDireccion.setText(direccion);

        switch (estado){
            case "pendiente":
                txtEstado.setText("El pedido está en espera de ser aceptado");
                fabEstado.setImageResource(R.drawable.ic_motorcycle);
                fabEstado.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.azul)));
                break;
            case "aceptado":
                txtEstado.setText("El pedido ha sido aceptado por el mandadero");
                fabEstado.setImageResource(R.drawable.ic_done);
                fabEstado.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.azul)));
                btnConfirmar.setText("Pedido aceptado");
                btnConfirmar.setEnabled(false);
                btnConfirmar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_boton_recibido));
                break;
            case "finalizado":
                txtEstado.setText("El pedido ya ha te ha sido entregado");
                fabEstado.setImageResource(R.drawable.ic_gift);
                fabEstado.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.verde)));
                btnConfirmar.setText("Pedido recibido");
                btnConfirmar.setEnabled(false);
                btnConfirmar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_boton_recibido));
                break;
            case "rechazado":
                txtEstado.setText("El pedido ha sido rechazado por el mandadero");
                fabEstado.setImageResource(R.drawable.ic_close_black_24dp);
                fabEstado.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.escarlata)));
                fabRecepcion.setImageResource(R.drawable.ic_cancel);
                fabRecepcion.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.naranja)));
                btnConfirmar.setText("Pedido rechazado");
                btnConfirmar.setEnabled(false);
                btnConfirmar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_boton_rechazado));
                break;
        }
    }

    //Muestra mensaje de confirmación de recepción
    protected void showDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);

        View view  = this.getLayoutInflater().inflate(R.layout.dialogo_confirmacion_recepcion, null);
        dialog.setContentView(view);

        AppCompatButton btnAceptar = view.findViewById(R.id.btnBotonAceptarConfirmacion);
        AppCompatButton btnCancelar = view.findViewById(R.id.btnBotonCancelarConfirmacion);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(id).child("estado").setValue("finalizado");
                Toast.makeText(DetallesPedido.this, "Se ha confirmado la recepción del pedido", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Intent i = new Intent(getApplicationContext(), TabPedidos.class);
                startActivity(i);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnConfirmarRecepcion:
                showDialog();
                break;
            case R.id.fabMapaDestino:
                Intent i = new Intent(this, MapaDestino.class);
                i.putExtra("longitud", lon);
                i.putExtra("latitud", lat);
                i.putExtra("direccion", direccion);
                startActivity(i);
                break;
        }
    }
}
