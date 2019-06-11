package com.example.administrador.mandaditostec.Cliente.Pedido;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.Cliente.Mandaderos.Mandadero;
import com.example.administrador.mandaditostec.Cliente.checkNetworkConnection;
import com.example.administrador.mandaditostec.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ListaMandaderos extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mandaderos);
        Toolbar toolbar = findViewById(R.id.toolbarListaMandaderos);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Mandaderos disponibles");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkNetworkConnection = new checkNetworkConnection(this);

        init();
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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerMandaderos.setLayoutManager(mLayoutManager);
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

    private void init() {
        coordinatorMandaderos = findViewById(R.id.coordinatorListaMandaderos);
        recyclerMandaderos = findViewById(R.id.recyclerListaMandaderos);
        refreshMandaderos = findViewById(R.id.refreshListaMandaderos);
        avion = findViewById(R.id.imgEmptyListaMandadero);
        textEmpty = findViewById(R.id.textEmptyListaMandadero);
    }

    private void mandaderosObjetos(){
        databaseReference.child("Mandadero").orderByChild("estado").equalTo("disponible")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        checkData(dataSnapshot);
                        mandadero.clear();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            Mandadero valores = item.getValue(Mandadero.class);
                            mandadero.add(valores);

                        }
                        recyclerMandaderos.setAdapter(new mandaderosAdapter(mandadero));
                        recyclerMandaderos.getAdapter().notifyDataSetChanged();
                        databaseReference.child("Mandadero").orderByChild("estado").equalTo("disponible").removeEventListener(this);
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
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_mandadero, null);
            return new mandaderosAdapter.RecViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final mandaderosAdapter.RecViewHolder holder, int position) {
            final Mandadero modelo = mandadero.get(position);
            holder.txtNombre.setText(modelo.getNombre());
            holder.txtEstado.setText(modelo.getEstado());
            holder.image.setImageResource(R.drawable.scooter);
            holder.imgEstado.setImageResource(R.drawable.ic_circle);
            holder.imgEstado.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.verde)));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = modelo.getId();
                    String nombre = modelo.getNombre();

                    detallesMandadero(id, nombre);
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


}
