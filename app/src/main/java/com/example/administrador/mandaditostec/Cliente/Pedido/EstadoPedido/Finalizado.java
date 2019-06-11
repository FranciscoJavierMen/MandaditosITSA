package com.example.administrador.mandaditostec.Cliente.Pedido.EstadoPedido;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.Cliente.Pedido.DetallesPedido;
import com.example.administrador.mandaditostec.Cliente.Pedido.ModeloPedidos;
import com.example.administrador.mandaditostec.Cliente.checkNetworkConnection;
import com.example.administrador.mandaditostec.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Finalizado extends Fragment {

    //Firebase
    private DatabaseReference databaseReference;
    private ImageView avion;
    private TextView textEmpty;

    private FirebaseAuth mAuth;
    private String idCliente;

    //Lista y modelo
    private RecyclerView recyclerPedidos;
    private SwipeRefreshLayout refreshPedidos;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<ModeloPedidos> pedidos = new ArrayList<>();

    private com.example.administrador.mandaditostec.Cliente.checkNetworkConnection checkNetworkConnection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNetworkConnection = new checkNetworkConnection(getContext());

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
        View view = inflater.inflate(R.layout.fragment_tab_pendiente, container, false);
        //inicializarFirebase();
        inicializarComponentes(view);

        databaseReference = FirebaseDatabase.getInstance().getReference();

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
                            .make(coordinatorLayout, "Lista de pedidos finalizados actualizada", Snackbar.LENGTH_LONG);

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

        recyclerPedidos.setLayoutManager(mLayoutManager);
        return view;
    }

    private void isCurrenUser(){
        databaseReference.child("Pedido")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        checkData(dataSnapshot);
                        pedidos.clear();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            ModeloPedidos pedido = item.getValue(ModeloPedidos.class);

                            if (pedido.getIdCliente().equals(idCliente) && pedido.getEstado().equals("finalizado")){
                                pedidos.add(pedido);
                            }

                        }
                        recyclerPedidos.setAdapter(new pedidosAdapter(pedidos));
                        recyclerPedidos.getAdapter().notifyDataSetChanged();
                        databaseReference.child("Pedido").removeEventListener(this);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void pedidosObjetos(){
        databaseReference.child("Pedido").orderByChild("estado").equalTo("finalizado")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        checkData(dataSnapshot);
                        pedidos.clear();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            ModeloPedidos pedido = item.getValue(ModeloPedidos.class);
                            pedidos.add(pedido);

                        }

                        recyclerPedidos.setAdapter(new pedidosAdapter(pedidos));
                        recyclerPedidos.getAdapter().notifyDataSetChanged();
                        databaseReference.child("Pedido").orderByChild("estado").equalTo("aceptado").removeEventListener(this);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private class pedidosAdapter extends RecyclerView.Adapter<pedidosAdapter.RecViewHolder> {


        private ArrayList<ModeloPedidos> pedidos;

        public pedidosAdapter(ArrayList<ModeloPedidos> pedidos) {
            this.pedidos = pedidos;
        }

        @Override
        public pedidosAdapter.RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_pedido, null);
            return new pedidosAdapter.RecViewHolder(v);
        }

        @Override
        public void onBindViewHolder(pedidosAdapter.RecViewHolder holder, int position) {
            final ModeloPedidos modelo = pedidos.get(position);
            holder.txtDireccionDestino.setText(modelo.getMandadero());
            holder.txtPedido.setText(modelo.getPedido());
            holder.txtHora.setText(modelo.getHora());
            holder.image.setImageResource(R.drawable.img_finalizado);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String id = modelo.getId();
                    final String pedidos = modelo.getPedido();
                    final String fecha = modelo.getHora();
                    final String mandadero = modelo.getMandadero();
                    final String lat = modelo.getLatitudDestino();
                    final String lon = modelo.getLongitudDestino();
                    final String estado = modelo.getEstado();

                    final double latDes = Double.valueOf(lat);
                    final double lonDes = Double.valueOf(lon);

                    Intent i = new Intent(getActivity(), DetallesPedido.class);
                    //Enviando los datos a la actividad de detalles
                    i.putExtra("id", id);
                    i.putExtra("pedido", pedidos);
                    i.putExtra("fecha", fecha);
                    i.putExtra("mandadero", mandadero);
                    i.putExtra("estado", estado);
                    i.putExtra("direccion", setDireccion(latDes, lonDes));
                    i.putExtra("longitud", lonDes);
                    i.putExtra("latitud", latDes);

                    startActivity(i);
                }
            });
        }


        @Override
        public int getItemCount() {
            return pedidos.size();
        }

        public class RecViewHolder extends RecyclerView.ViewHolder {

            private TextView txtDireccionDestino, txtPedido, txtHora;
            private ImageView image;

            public RecViewHolder(View itemView) {
                super(itemView);

                txtDireccionDestino = itemView.findViewById(R.id.txtDireccionPedido);
                txtPedido = itemView.findViewById(R.id.txtDescripcionPedido);
                txtHora = itemView.findViewById(R.id.txtHoraPedido);
                image = itemView.findViewById(R.id.imgPedido);
            }
        }
    }

    private void checkData(DataSnapshot dataSnapshot){
        if (dataSnapshot.getChildrenCount() < 1){
            recyclerPedidos.setVisibility(View.GONE);
            avion.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.VISIBLE);
        }
        else{
            recyclerPedidos.setVisibility(View.VISIBLE);
            avion.setVisibility(View.GONE);
            textEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!checkNetworkConnection.isConnected()){
            recyclerPedidos.setVisibility(View.GONE);
            avion.setImageResource(R.drawable.no_wifi);
            avion.setVisibility(View.VISIBLE);
            textEmpty.setText("No estas conectado a internet");
            textEmpty.setVisibility(View.VISIBLE);
        }
        else{
            isCurrenUser();
            recyclerPedidos.setVisibility(View.VISIBLE);
            avion.setVisibility(View.GONE);
            textEmpty.setVisibility(View.GONE);
        }
    }

    //Inicializa los componentes (vistas)
    private void inicializarComponentes(View view) {
        avion = view.findViewById(R.id.imgEmpty);
        textEmpty = view.findViewById(R.id.textEmpty);
        recyclerPedidos = view.findViewById(R.id.recyclerPedidos);
        refreshPedidos = view.findViewById(R.id.refreshPedidos);
        coordinatorLayout = view.findViewById(R.id.coordinatorPedidos);
    }

    //Obtiene la dirección origen de las coordenadas dadas
    private String setDireccion(double lat, double lon){
        String cityName = "";
        String addresName = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        try{
            addresses = geocoder.getFromLocation(lat, lon, 10);
            if (addresses.size() > 0){
                for (Address adr: addresses){
                    if (adr.getLocality() != null && adr.getLocality().length() > 0){
                        cityName = adr.getLocality();
                        addresName = adr.getAddressLine(0);
                        break;
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return addresName + " - " + cityName;
    }
}
