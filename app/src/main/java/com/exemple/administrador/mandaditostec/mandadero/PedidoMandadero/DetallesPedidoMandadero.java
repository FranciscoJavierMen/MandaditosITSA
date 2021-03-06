package com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.administrador.mandaditostec.Cliente.Pedido.Chat;
import com.example.administrador.mandaditostec.Cliente.Pedido.FormDialog;
import com.example.administrador.mandaditostec.R;
import com.exemple.administrador.mandaditostec.mandadero.MapaMandadero.MapaMandadero;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetallesPedidoMandadero extends DialogFragment {

    public static final String TAG = "Detalles del pedido";
    public static String key;
    private Toolbar toolbar;
    private String latorig,lngorig,latdest,lngdest;
    public static String distancia,direccion;
    private CardView cardChat;
    public static String idMandadero;
    public static String nombreMandadero;
    public static String estado;
    private FloatingActionButton fabEstado;
    private AppCompatButton btnEstado, btnEstado2;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout linearLayout;

    private FloatingActionButton irdireccion,aceptar,cancerlar;
    private DatabaseReference databaseReference;

    private TextView detallespedido,hora,tvdistancia,tvdireccion;

    public static DetallesPedidoMandadero display(FragmentManager fragmentManager) {
        DetallesPedidoMandadero detallesPedido = new DetallesPedidoMandadero();
        detallesPedido.show(fragmentManager, TAG);
        return detallesPedido;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(FormDialog.STYLE_NORMAL,R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getContext(), ""+idMandadero, Toast.LENGTH_SHORT).show();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }

        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    //final String direccionD = dataSnapshot.child("mandadero").getValue().toString();
                    final String pedido = dataSnapshot.child("pedido").getValue().toString();
                    final String fecha = dataSnapshot.child("hora").getValue().toString();
                    latorig = dataSnapshot.child("latitudOrigen").getValue().toString();
                    lngorig = dataSnapshot.child("longitudOrigen").getValue().toString();
                    latdest= dataSnapshot.child("latitudDestino").getValue().toString();
                    lngdest = dataSnapshot.child("longitudDestino").getValue().toString();

                    hora.setText(fecha);
                    detallespedido.setText(pedido);
                    tvdireccion.setText(direccion);
                    tvdistancia.setText(distancia);

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "El nodo no existe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detalles_pedido_mandadero, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        irdireccion = view.findViewById(R.id.verDireccion);
        aceptar = view.findViewById(R.id.aceptarpedido);
        cancerlar = view.findViewById(R.id.cancelarpedido);
        tvdistancia = view.findViewById(R.id.distancia);
        tvdireccion = view.findViewById(R.id.direccionmandado);
        fabEstado = view.findViewById(R.id.fabEstado);
        btnEstado = view.findViewById(R.id.btnEstadoPedido);
        btnEstado2 = view.findViewById(R.id.btnEstadoPedido2);
        coordinatorLayout = view.findViewById(R.id.coordinatorFab);
        linearLayout = view.findViewById(R.id.linearFab);

        detallespedido = view.findViewById(R.id.descripcion);
        cardChat = view.findViewById(R.id.cardChatM);
        hora = view.findViewById(R.id.time);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pedido");

        cardChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat = new Intent(getContext(), ChatM.class);
                chat.putExtra("idCliente", idMandadero);
                startActivity(chat);
            }
        });

        getStatus();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(key).child("estado").setValue("aceptado");
                dismiss();
            }
        });

        cancerlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(key).child("estado").setValue("rechazado");
                dismiss();
                return;
            }
        });
        irdireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MapaMandadero.class);
                i.putExtra("latitud",latdest);
                i.putExtra("longitud",lngdest);
                i.putExtra("latitudorigen",latorig);
                i.putExtra("longitudorigen",lngorig);
                startActivity(i);

            }
        });
        toolbar.setTitle("Detalles del pedido");
        }

    //Método para verificar el esatdo del pedido
    private void getStatus(){
        switch (estado){
            case "pendiente":
                fabEstado.setImageResource(R.drawable.ic_reloj);
                fabEstado.setImageResource(R.drawable.ic_reloj);
                btnEstado.setText("Pedido en espera");
                fabEstado.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.azul)));
                cardChat.setEnabled(true);
                linearLayout.setVisibility(View.VISIBLE);
                coordinatorLayout.setVisibility(View.GONE);
                break;
            case "aceptado":
                fabEstado.setImageResource(R.drawable.ic_done);
                fabEstado.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.azul)));
                btnEstado.setText("Pedido aceptado");
                btnEstado.setEnabled(true);
                btnEstado.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_boton_recibido));
                cardChat.setEnabled(true);
                linearLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
                break;
            case "realizado":
                cardChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "El pedido ya terminó, ya no puedes enviar mensajes", Toast.LENGTH_SHORT).show();
                    }
                });
                fabEstado.setImageResource(R.drawable.ic_gift);
                fabEstado.setImageResource(R.drawable.ic_gift);
                fabEstado.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.verde)));
                btnEstado.setText("Pedido realizado");
                btnEstado.setEnabled(false);
                btnEstado.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_boton_recibido));
                linearLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
                btnEstado2.setText("El pedido ya ha finalizado");
                break;
        }
    }
}
