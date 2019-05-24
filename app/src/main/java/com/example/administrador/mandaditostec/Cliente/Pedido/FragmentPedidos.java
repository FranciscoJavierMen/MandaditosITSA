package com.example.administrador.mandaditostec.Cliente.Pedido;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentPedidos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FloatingActionButton fabPedido;


    //Lista y modelo
    private RecyclerView recyclerPedidos;
    private AdaptadorPedidos adaptadorPedidos;
    private ArrayList<ModeloPedidos> listaPedidos = new ArrayList<>();
    private SwipeRefreshLayout refreshPedidos;
    private CoordinatorLayout coordinatorLayout;

    //Firebase
    private DatabaseReference databaseReference;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentPedidos() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_pedidos, container, false);
        //inicializarFirebase();
        inicializarComponentes(view);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pedido");

        // Seteamos los colores que se usarán a lo largo de la animación
        refreshPedidos.setColorSchemeResources(R.color.verde);
        refreshPedidos.setProgressBackgroundColorSchemeResource(R.color.blanco);
        refreshPedidos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(1000);
                    refreshPedidos.setRefreshing(false);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Lista de pedidos actualizada", Snackbar.LENGTH_LONG);

                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                    onStart();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        recyclerPedidos.setLayoutManager(new LinearLayoutManager(getActivity()));

        fabPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogo();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ModeloPedidos> opciones
                = new FirebaseRecyclerOptions.Builder<ModeloPedidos>()
                .setQuery(databaseReference, ModeloPedidos.class)
                .build();

        FirebaseRecyclerAdapter<ModeloPedidos, PedidosViewHolder> adapter
                = new FirebaseRecyclerAdapter<ModeloPedidos, PedidosViewHolder>(opciones) {
            @Override
            protected void onBindViewHolder(final PedidosViewHolder holder, int position, ModeloPedidos model) {
                final String pedidoID = getRef(position).getKey();


                assert pedidoID != null;
                databaseReference.child(pedidoID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            final String direccionD = dataSnapshot.child("mandadero").getValue().toString();
                            final String pedido = dataSnapshot.child("pedido").getValue().toString();
                            final String fecha = dataSnapshot.child("hora").getValue().toString();

                            holder.txtDireccionDestino.setText(direccionD);
                            holder.txtPedido.setText(pedido);
                            holder.txtHora.setText(fecha);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DetallesPedido.display(getFragmentManager());
                                }
                            });
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "El nodo no existe", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public PedidosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_pedido, parent, false);
                return new PedidosViewHolder(view);
            }
        };
        recyclerPedidos.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PedidosViewHolder extends RecyclerView.ViewHolder{

        private TextView txtDireccionDestino, txtPedido, txtHora;

        PedidosViewHolder(View itemView) {
            super(itemView);

            txtDireccionDestino = itemView.findViewById(R.id.txtDireccionPedido);
            txtPedido = itemView.findViewById(R.id.txtDescripcionPedido);
            txtHora = itemView.findViewById(R.id.txtHoraPedido);
        }
    }

    private void inicializarComponentes(View view) {
        recyclerPedidos = view.findViewById(R.id.recyclerPedidos);
        refreshPedidos = view.findViewById(R.id.refreshPedidos);
        fabPedido = view.findViewById(R.id.fabPedido);
        coordinatorLayout = view.findViewById(R.id.coordinatorPedidos);
    }

    private void abrirDialogo(){
        FormDialog.display(getFragmentManager());
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
