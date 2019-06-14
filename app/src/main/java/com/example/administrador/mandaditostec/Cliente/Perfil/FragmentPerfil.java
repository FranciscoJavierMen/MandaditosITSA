package com.example.administrador.mandaditostec.Cliente.Perfil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.Login.Acceder;
import com.example.administrador.mandaditostec.Cliente.Pedido.ModeloPedidos;
import com.example.administrador.mandaditostec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class FragmentPerfil extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //Firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String idCliente;
    private String total, pendiente, aceptado, rechazado, finalizado;

    private AppCompatButton btnCerrarSesion;
    private TextView txtNombre, txtCorreo, txtTotal, txtPendiente, txtAceptado, txtFinalizado, txtRechazado;
    private ArrayList<ModeloPedidos> pedidos = new ArrayList<>();


    public FragmentPerfil() {
        // Required empty public constructor
    }

    public static FragmentPerfil newInstance(String param1, String param2) {
        FragmentPerfil fragment = new FragmentPerfil();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        if (current_user != null) {
            idCliente = current_user.getUid();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        init(view);

        return  view;
    }

    private void init(View v){
        txtTotal = v.findViewById(R.id.txtPedidosTotal);
        txtNombre =v.findViewById(R.id.txtNombreClienteP);
        txtCorreo = v.findViewById(R.id.txtCorreoClienteP);
        txtPendiente = v.findViewById(R.id.txtPedidosPendientes);
        txtAceptado = v.findViewById(R.id.txtPedidosAceptados);
        txtRechazado = v.findViewById(R.id.txtPedidosRechazados);
        txtFinalizado = v.findViewById(R.id.txtPedidosFinalizados);

        btnCerrarSesion = v.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    private void datosUsuario(){
        databaseReference.child("Cliente").child(idCliente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nombre = dataSnapshot.child("nombre").getValue().toString();
                String correo = dataSnapshot.child("correo").getValue().toString();

                txtCorreo.setText(correo);
                txtNombre.setText(nombre);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showEstadisticas(){
        try{
            datosUsuario();
            pedidosTotales();
            pedidosAceptados();
            pedidosPendientes();
            pedidosRechazados();
            pedidosFinalizados();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void pedidosTotales(){
        databaseReference.child("Pedido")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        pedidos.clear();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            ModeloPedidos pedido = item.getValue(ModeloPedidos.class);

                            if (pedido.getIdCliente().equals(idCliente)){
                                pedidos.add(pedido);
                            }

                        }
                        if (pedidos.size() < 1){
                            total = "0";
                        } else {
                            total = ""+pedidos.size();
                        }
                        txtTotal.setText(total);
                        databaseReference.child("Pedido").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        txtTotal.setText(total);
    }

    private void pedidosPendientes(){
        databaseReference.child("Pedido")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        pedidos.clear();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            ModeloPedidos pedido = item.getValue(ModeloPedidos.class);

                            if (pedido.getIdCliente().equals(idCliente) &&
                                    pedido.getEstado().equals("pendiente")){
                                pedidos.add(pedido);
                            }

                        }
                        if (pedidos.size() < 1){
                            pendiente = "0";
                        } else {
                            pendiente = ""+pedidos.size();
                        }
                        txtPendiente.setText(pendiente);
                        databaseReference.child("Pedido").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        txtPendiente.setText(pendiente);
    }

    private void pedidosAceptados(){
        databaseReference.child("Pedido")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        pedidos.clear();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            ModeloPedidos pedido = item.getValue(ModeloPedidos.class);

                            if (pedido.getIdCliente().equals(idCliente) &&
                                    pedido.getEstado().equals("aceptado")){
                                pedidos.add(pedido);
                            }

                        }
                        if (pedidos.size() < 1){
                            aceptado = "0";
                        } else {
                            aceptado = ""+pedidos.size();
                        }
                        txtAceptado.setText(aceptado);
                        databaseReference.child("Pedido").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        txtAceptado.setText(aceptado);
    }

    private void pedidosRechazados(){
        databaseReference.child("Pedido")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        pedidos.clear();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            ModeloPedidos pedido = item.getValue(ModeloPedidos.class);

                            if (pedido.getIdCliente().equals(idCliente) &&
                                    pedido.getEstado().equals("rechazado")){
                                pedidos.add(pedido);
                            }

                        }
                        if (pedidos.size() < 1){
                            rechazado = "0";
                        } else {
                            rechazado = ""+pedidos.size();
                        }
                        txtRechazado.setText(rechazado);
                        databaseReference.child("Pedido").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        txtRechazado.setText(rechazado);
    }

    private void pedidosFinalizados(){
        databaseReference.child("Pedido")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        pedidos.clear();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            ModeloPedidos pedido = item.getValue(ModeloPedidos.class);

                            if (pedido.getIdCliente().equals(idCliente) &&
                                    pedido.getEstado().equals("finalizado")){
                                pedidos.add(pedido);
                            }

                        }
                        if (pedidos.size() < 1){
                            finalizado = "0";
                        } else {
                            finalizado = ""+pedidos.size();
                        }
                        txtFinalizado.setText(finalizado);
                        databaseReference.child("Pedido").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        txtFinalizado.setText(finalizado);
    }

    @Override
    public void onStart() {
        super.onStart();
        showEstadisticas();
    }

    protected void showDialog(){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view  = this.getLayoutInflater().inflate(R.layout.dialog_cerrar_sesion_cliente, null);
        dialog.setContentView(view);

        AppCompatButton btnAceptar = view.findViewById(R.id.btnAceptarSesion);
        AppCompatButton btnCancelar = view.findViewById(R.id.btnCancelarSesion);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                backToWelcome();
            }
        });

        dialog.show();
    }

    //Método para volver a activitu de logeo y registro
    private void backToWelcome() {
        Intent start = new Intent(getContext(), Acceder.class);
        startActivity(start);
        getActivity().finish();
    }



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
