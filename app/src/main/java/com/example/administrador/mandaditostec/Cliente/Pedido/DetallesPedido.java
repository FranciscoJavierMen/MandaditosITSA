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
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.administrador.mandaditostec.Cliente.Pedido.FormDialog.TAG;

public class DetallesPedido extends DialogFragment implements View.OnClickListener{

    public static final String TAG = "Detalles del pedido";
    private Toolbar toolbar;

    //Declaración de las vistas
    private TextView txtPedido, txtDireccion, txtFecha, txtMandadero, txtEstado;
    private FloatingActionButton fabEstado, fabMapa;
    private AppCompatButton btnConfirmar;

    //Firebase
    private DatabaseReference databaseReference;

    //Variables para recepción de datos
    private String id, pedido, fecha, mandadero, direccion;
    private boolean realizado;
    private double lat, lon;

    public static DetallesPedido display(FragmentManager fragmentManager) {
        DetallesPedido detallesPedido = new DetallesPedido();
        detallesPedido.show(fragmentManager, TAG);
        return detallesPedido;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            id = getArguments().getString("id", "No se pudo obtener el id");
            pedido = getArguments().getString("pedido", "No se pudo cargar el pedido");
            fecha = getArguments().getString("fecha", "No se pudo obtener la fecha");
            mandadero = getArguments().getString("mandadero", "No se pudo obtener el mandadero");
            realizado = getArguments().getBoolean("realizado", false);
            direccion = getArguments().getString("direccion", "No se pudo obtener la dirección");
            lat = getArguments().getDouble("latitud");
            lon = getArguments().getDouble("longitud");
        }
        setStyle(FormDialog.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detalles_pedido, container, false);
        inicializar(view);
        setValues();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pedido");

        btnConfirmar.setOnClickListener(this);
        fabMapa.setOnClickListener(this);

        toolbar = view.findViewById(R.id.toolbarDetallesPedido);
        return view;
    }

    //Inicializar lo componentes gráficos
    private void inicializar(View v){
        txtPedido = v.findViewById(R.id.textPedido);
        txtDireccion = v.findViewById(R.id.textDireccion);
        txtFecha = v.findViewById(R.id.textFecha);
        txtMandadero = v.findViewById(R.id.textMandadero);
        txtEstado = v.findViewById(R.id.textEstado);
        fabEstado = v.findViewById(R.id.fabEstado);
        fabMapa = v.findViewById(R.id.fabMapaDestino);
        btnConfirmar = v.findViewById(R.id.btnConfirmarRecepcion);
    }

    //establecer los valores
    private void setValues(){
        txtPedido.setText(pedido);
        txtFecha.setText(fecha);
        txtMandadero.setText(mandadero);
        txtDireccion.setText(direccion);
        if (realizado){
            txtEstado.setText("El pedido ya ha sido entregado");
            fabEstado.setImageResource(R.drawable.ic_done);
            fabEstado.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.verde)));
            btnConfirmar.setText("Pedido recibido");
            btnConfirmar.setEnabled(false);
            btnConfirmar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_boton_recibido));
        } else {
            txtEstado.setText("El pedido está en camino");
            fabEstado.setImageResource(R.drawable.ic_motorcycle);
            fabEstado.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.azul)));
        }
    }

    protected void showDialog(){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view  = this.getLayoutInflater().inflate(R.layout.dialogo_confirmacion_recepcion, null);
        dialog.setContentView(view);

        AppCompatButton btnAceptar = view.findViewById(R.id.btnBotonAceptarConfirmacion);
        AppCompatButton btnCancelar = view.findViewById(R.id.btnBotonCancelarConfirmacion);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(id).child("realizado").setValue(true);
                Toast.makeText(getActivity(), "Se ha confirmado la recepción del pedido", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                FragmentPedidos fragmentPedidos = new FragmentPedidos();
                setFragment(fragmentPedidos);
            }
        });
        toolbar.setTitle("Detalles del pedido");
    }



    //Método para establecer el fragment seleccionado dentro del FrameLayout
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnConfirmarRecepcion:
                showDialog();
                break;
            case R.id.fabMapaDestino:
                Intent i = new Intent(getContext(), MapaDestino.class);
                i.putExtra("longitud", lon);
                i.putExtra("latitud", lat);
                i.putExtra("direccion", direccion);
                startActivity(i);
                break;
        }
    }
}
