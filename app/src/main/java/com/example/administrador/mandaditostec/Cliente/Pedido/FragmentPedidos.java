package com.example.administrador.mandaditostec.Cliente.Pedido;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.Info.AppInfo;
import com.Info.Terminos;
import com.Info.Tutorial;
import com.example.administrador.mandaditostec.Cliente.Pedido.EstadoPedido.TabPedidos;
import com.example.administrador.mandaditostec.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FragmentPedidos extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private AppCompatButton btnPedidos, btnTuto, btnAcerca, btnTerminos;

    public FragmentPedidos() {
        // Required empty public constructor
    }

    public static FragmentPedidos newInstance(String param1, String param2) {
        FragmentPedidos fragment = new FragmentPedidos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_pedidos, container, false);

        inicializarComponentes(view);
        return view;
    }

    private void inicializarComponentes(View v){
        btnPedidos = v.findViewById(R.id.btnCardPedidos);
        btnTuto = v.findViewById(R.id.btnCardTuto);
        btnAcerca = v.findViewById(R.id.btnCardAcerca);
        btnTerminos = v.findViewById(R.id.btnCardTerminos);

        btnPedidos.setOnClickListener(this);
        btnTuto.setOnClickListener(this);
        btnAcerca.setOnClickListener(this);
        btnTerminos.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCardPedidos:
                startActivity(new Intent(getContext(), TabPedidos.class));
                break;
            case R.id.btnCardTuto:
                startActivity(new Intent(getContext(), Tutorial.class));
                break;
            case R.id.btnCardAcerca:
                startActivity(new Intent(getContext(), AppInfo.class));
                break;
            case R.id.btnCardTerminos:
                startActivity(new Intent(getContext(), Terminos.class));
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
