package com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.Cliente.Pedido.DetallesPedido;
import com.example.administrador.mandaditostec.Cliente.Pedido.FormDialog;
import com.example.administrador.mandaditostec.R;
import com.exemple.administrador.mandaditostec.mandadero.DetallesPedidoMandadero;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PedidosMandaderosFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FloatingActionButton fabPedido;


    //Lista y modelo
    private RecyclerView rpedidos;
    private AdaptadorPedidos adaptadorPedidos;
    private ArrayList<PedidosMandadero> listaPedidos = new ArrayList<>();
    private SwipeRefreshLayout refreshPedidos;
    private CoordinatorLayout coordinatorLayout;

    //Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PedidosMandaderosFragment.OnFragmentInteractionListener mListener;

    public PedidosMandaderosFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PedidosMandaderosFragment newInstance(String param1, String param2) {
        PedidosMandaderosFragment fragment = new PedidosMandaderosFragment();
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
        View view = inflater.inflate(R.layout.fragment_pedidos_mandaderos, container, false);
        //inicializarFirebase();
        inicializarComponentes(view);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pedido");

        rpedidos.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PedidosMandadero> opciones
                = new FirebaseRecyclerOptions.Builder<PedidosMandadero>()
                .setQuery(databaseReference, PedidosMandadero.class)
                .build();

        FirebaseRecyclerAdapter<PedidosMandadero, PedidosMandaderosFragment.PedidosMandaderoViewHolder> adapter
                = new FirebaseRecyclerAdapter<PedidosMandadero, PedidosMandaderosFragment.PedidosMandaderoViewHolder>(opciones) {
            @Override
            protected void onBindViewHolder(final PedidosMandaderoViewHolder holder, int position, PedidosMandadero model) {
                final String pedidoID = getRef(position).getKey();

                databaseReference.child(pedidoID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            final String direccionD = dataSnapshot.child("mandadero").getValue().toString();
                            final String pedido = dataSnapshot.child("pedido").getValue().toString();
                            final String fecha = dataSnapshot.child("hora").getValue().toString();

                            holder.tvpedido.setText(pedido);
                            holder.tvhora.setText(fecha);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DetallesPedidoMandadero.display(getFragmentManager());
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
            public PedidosMandaderosFragment.PedidosMandaderoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_pedido, parent, false);
                return new PedidosMandaderosFragment.PedidosMandaderoViewHolder(view);
            }
        };
        rpedidos.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PedidosMandaderoViewHolder extends RecyclerView.ViewHolder{

        private TextView tvpedido, tvhora;

        public PedidosMandaderoViewHolder(View itemView) {
            super(itemView);

            tvpedido = itemView.findViewById(R.id.tvdescripcionpedido);
            tvhora = itemView.findViewById(R.id.tvhorapedido);
        }
    }

    private void inicializarComponentes(View view) {
        rpedidos = view.findViewById(R.id.recyclerPedidosMandaderos);
        //refreshPedidos = view.findViewById(R.id.refreshPedidos);
        //fabPedido = view.findViewById(R.id.fabPedido);
        coordinatorLayout = view.findViewById(R.id.fragmentpedidos);
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
        if (context instanceof PedidosMandaderosFragment.OnFragmentInteractionListener) {
            mListener = (PedidosMandaderosFragment.OnFragmentInteractionListener) context;
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

    private class HackingBackgroundTask extends AsyncTask<Void, Void, ArrayList<PedidosMandadero>> {

        static final int DURACION = 2 * 1000;

        @Override
        protected ArrayList doInBackground(Void... params) {
            // Simulación de la carga de items
            try {
                Thread.sleep(DURACION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Retornar en nuevos elementos para el adaptador
            return listaPedidos;
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);

            // Limpiar elementos antiguos
            adaptadorPedidos.clear();

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Lista de pedidos actualizada", Snackbar.LENGTH_LONG);

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

            // Parar la animación del indicador
            refreshPedidos.setRefreshing(false);
        }

    }

}