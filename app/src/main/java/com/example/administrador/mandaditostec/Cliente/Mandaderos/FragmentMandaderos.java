package com.example.administrador.mandaditostec.Cliente.Mandaderos;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.Cliente.Pedido.FormDialog;
import com.example.administrador.mandaditostec.Cliente.checkNetworkConnection;
import com.example.administrador.mandaditostec.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class FragmentMandaderos extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //Firebase
    private DatabaseReference databaseReference;

    //Lista y modelo
    private RecyclerView recyclerMandaderos;
    private SwipeRefreshLayout refreshMandaderos;
    private CoordinatorLayout coordinatorMandaderos;
    private ArrayList<Mandadero> mandadero = new ArrayList<>();
    private ImageView avion;
    private TextView textEmpty;
    private com.example.administrador.mandaditostec.Cliente.checkNetworkConnection checkNetworkConnection;

    private OnFragmentInteractionListener mListener;

    public FragmentMandaderos() {
        // Required empty public constructor
    }

    public static FragmentMandaderos newInstance(String param1, String param2) {
        FragmentMandaderos fragment = new FragmentMandaderos();
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
        checkNetworkConnection = new checkNetworkConnection(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_mandaderos, container, false);
        init(view);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Seteamos los colores que se usarán a lo largo de la animación
        refreshMandaderos.setColorSchemeResources(R.color.verde);
        refreshMandaderos.setProgressBackgroundColorSchemeResource(R.color.blanco);
        refreshMandaderos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(1000);
                    refreshMandaderos.setRefreshing(false);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorMandaderos, "Lista de pedidos aceptada actualizada", Snackbar.LENGTH_LONG);

                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                    onStart();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerMandaderos.setLayoutManager(mLayoutManager);
        return view;
    }

    private void init(View view) {
        coordinatorMandaderos = view.findViewById(R.id.coordinatorMandaderos);
        recyclerMandaderos = view.findViewById(R.id.recyclerMandaderos);
        refreshMandaderos = view.findViewById(R.id.refreshMandaderos);
        avion = view.findViewById(R.id.imgEmptyMandadero);
        textEmpty = view.findViewById(R.id.textEmptyMandadero);
    }

    private void mandaderosObjetos(){
        databaseReference.child("Mandadero").orderByChild("estado")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        checkData(dataSnapshot);
                        Toast.makeText(getActivity(), "No. de mandaderos : " + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                        mandadero.clear();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            Mandadero valores = item.getValue(Mandadero.class);
                            mandadero.add(valores);

                        }

                        recyclerMandaderos.setAdapter(new mandaderosAdapter(mandadero));
                        recyclerMandaderos.getAdapter().notifyDataSetChanged();
                        databaseReference.child("Mandadero").orderByChild("estado").removeEventListener(this);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private class mandaderosAdapter extends RecyclerView.Adapter<mandaderosAdapter.RecViewHolder> {


        private ArrayList<Mandadero> mandadero;

        public mandaderosAdapter(ArrayList<Mandadero> mandadero) {
            this.mandadero = mandadero;
        }

        @Override
        public mandaderosAdapter.RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_mandadero, null);
            return new mandaderosAdapter.RecViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final mandaderosAdapter.RecViewHolder holder, int position) {
            final Mandadero modelo = mandadero.get(position);
            holder.txtNombre.setText(modelo.getNombre());
            holder.txtEstado.setText(modelo.getEstado());
            holder.image.setImageResource(R.drawable.scooter);

            if (modelo.getEstado().equals("disponible")){
                holder.imgEstado.setImageResource(R.drawable.ic_circle);
                holder.imgEstado.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.verde)));
            }

            if (modelo.getEstado().equals("ocupado")){
                holder.imgEstado.setImageResource(R.drawable.ic_circle);
                holder.imgEstado.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.naranja)));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", modelo.getId());
                    bundle.putString("nombre", modelo.getNombre());

                    FragmentManager manager = getFragmentManager();
                    FormDialog formDialog = new FormDialog();
                    formDialog.setArguments(bundle);
                    formDialog.show(manager, "tag");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mandadero.size();
        }

        public class RecViewHolder extends RecyclerView.ViewHolder {

            private TextView txtNombre, txtEstado;
            private ImageView image, imgEstado;

            public RecViewHolder(View itemView) {
                super(itemView);

                txtNombre = itemView.findViewById(R.id.txtNombreMandadero);
                txtEstado = itemView.findViewById(R.id.txtEstadoMandadero);
                image = itemView.findViewById(R.id.imgMandadero);
                imgEstado = itemView.findViewById(R.id.imgEstadoMandadero);
            }
        }
    }

    //Abre el dialogo con el formulario de pedidos
    private void abrirDialogo(){
        FormDialog.display(getFragmentManager());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!checkNetworkConnection.isConnected()){
            recyclerMandaderos.setVisibility(View.GONE);
            avion.setImageResource(R.drawable.no_wifi);
            avion.setVisibility(View.VISIBLE);
            textEmpty.setText("No estas conectado a internet");
            textEmpty.setVisibility(View.VISIBLE);
        }
        else{
            mandaderosObjetos();
            recyclerMandaderos.setVisibility(View.VISIBLE);
            avion.setVisibility(View.GONE);
            textEmpty.setVisibility(View.GONE);
        }
    }

    private void checkData(DataSnapshot dataSnapshot){
        if (dataSnapshot.getChildrenCount() < 1){
            recyclerMandaderos.setVisibility(View.GONE);
            avion.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.VISIBLE);
        }
        else{
            recyclerMandaderos.setVisibility(View.VISIBLE);
            avion.setVisibility(View.GONE);
            textEmpty.setVisibility(View.GONE);
        }
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
