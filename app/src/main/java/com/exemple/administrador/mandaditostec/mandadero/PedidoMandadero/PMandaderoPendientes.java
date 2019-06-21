package com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.Cliente.checkNetworkConnection;
import com.example.administrador.mandaditostec.R;
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

public class PMandaderoPendientes extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    LocationManager mlocManager;
    Localizacion Local;
    Location lastLocation;

    Double lat = 0.0, lng = 0.0;
    private String latorig="0.0", lngorig="0.0", latdest="0.0", lngdest="0.0",
            distancia="0.0", direccion="0.0";

    //Lista y modelo
    private RecyclerView recyclerPendientes;
    private ArrayList<PedidosMandadero> listaPedidos = new ArrayList<>();
    private com.example.administrador.mandaditostec.Cliente.checkNetworkConnection checkNetworkConnection;

    //Firebase
    private DatabaseReference databaseReference;
    private String idMandadero;

    private ImageView avion;
    private TextView textEmpty;


    private PMandaderoPendientes.OnFragmentInteractionListener mListener;

    public PMandaderoPendientes() {
        // Required empty public constructor
    }

    public static PMandaderoPendientes newInstance(String param1, String param2) {
        PMandaderoPendientes fragment = new PMandaderoPendientes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkNetworkConnection = new checkNetworkConnection(getContext());
        try{
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser current_user = mAuth.getCurrentUser();
            if (current_user != null) {
                idMandadero = current_user.getUid();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedidos_mandaderos, container, false);
        inicializarComponentes(view);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerPendientes.setLayoutManager(mLayoutManager);

        return view;
    }

    private void isCurrenUser(){
        databaseReference.child("Pedido")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        checkData(dataSnapshot);
                        listaPedidos.clear();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            PedidosMandadero pedido = item.getValue(PedidosMandadero.class);

                            if (pedido.getIdMandadero().equals(idMandadero) && pedido.getEstado().equals("pendiente")) {
                                listaPedidos.add(pedido);
                            }

                        }
                        checkList(listaPedidos);
                        recyclerPendientes.setAdapter(new pedidosAdapter(listaPedidos));
                        recyclerPendientes.getAdapter().notifyDataSetChanged();
                        databaseReference.child("Pedido").removeEventListener(this);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private class pedidosAdapter extends RecyclerView.Adapter<pedidosAdapter.RecViewHolder> {

        private ArrayList<PedidosMandadero> listaPedidos;

        public pedidosAdapter(ArrayList<PedidosMandadero> listaPedidos) {
            this.listaPedidos = listaPedidos;
        }

        @Override
        public pedidosAdapter.RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_pedido, null);
            return new RecViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecViewHolder holder, int position) {
            final PedidosMandadero modelo = listaPedidos.get(position);
            holder.txtDireccionDestino.setText(modelo.getMandadero());
            holder.txtPedido.setText(modelo.getPedido());
            holder.txtHora.setText(modelo.getHora());
            holder.image.setImageResource(R.drawable.img_reloj);


            final String latorigen = modelo.getLatitudOrigen();
            final String latdestino = modelo.getLatitudDestino();
            final String lngdestino = modelo.getLongitudDestino();
            final String lngorigen = modelo.getLongitudOrigen();
            final String pedidoID = modelo.getId();
            final String idMandadero = modelo.getIdMandadero();


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    latorig = latorigen + "";
                    lngorig = lngorigen + "";
                    latdest = latdestino + "";
                    lngdest = lngdestino + "";

                    double latd=Double.parseDouble(modelo.getLatitudDestino());
                    double lngd=Double.parseDouble(modelo.getLongitudDestino());

                    Location locationA = new Location("Destino");

                    locationA.setLatitude(latd);
                    locationA.setLongitude(lngd);

                    obtenerDireccionRapido(locationA);
                    DetallesPedidoMandadero.distancia = distancia;
                    DetallesPedidoMandadero.direccion = direccion;
                    DetallesPedidoMandadero.key = pedidoID;
                    DetallesPedidoMandadero.idMandadero = idMandadero;
                    DetallesPedidoMandadero.idMandadero = idMandadero;
                    DetallesPedidoMandadero.display(getFragmentManager());
                }
            });
        }

        @Override
        public int getItemCount() {
            return listaPedidos.size();
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

    @Override
    public void onStart() {
        super.onStart();
        if (!checkNetworkConnection.isConnected()){
            recyclerPendientes.setVisibility(View.GONE);
            avion.setImageResource(R.drawable.no_wifi);
            avion.setVisibility(View.VISIBLE);
            textEmpty.setText("No estas conectado a internet");
            textEmpty.setVisibility(View.VISIBLE);
        }
        else{
            isCurrenUser();
            recyclerPendientes.setVisibility(View.VISIBLE);
            avion.setVisibility(View.GONE);
            textEmpty.setVisibility(View.GONE);
        }
    }

    private void checkData(DataSnapshot dataSnapshot){
        if (dataSnapshot.getChildrenCount() < 1){
            recyclerPendientes.setVisibility(View.GONE);
            avion.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.VISIBLE);
        }
        else{
            recyclerPendientes.setVisibility(View.VISIBLE);
            avion.setVisibility(View.GONE);
            textEmpty.setVisibility(View.GONE);
        }
    }

    private void checkList(ArrayList arrayList){
        if (arrayList.size() < 1){
            recyclerPendientes.setVisibility(View.GONE);
            avion.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void inicializarComponentes(View view) {
        recyclerPendientes = view.findViewById(R.id.recyclerPedidosMandaderosPendientes);
        avion = view.findViewById(R.id.imgEmpty);
        textEmpty = view.findViewById(R.id.textEmpty);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PMandaderoPendientes.OnFragmentInteractionListener) {
            mListener = (PMandaderoPendientes.OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()
              //      + " must implement OnFragmentInteractionListener");
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

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setVista_general(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        Toast.makeText(getActivity().getApplicationContext(), "Localización agregada", Toast.LENGTH_SHORT).show();

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    //tvdireccion.setText(" "+ DirCalle.getAddressLine(0));
                    Log.d("direccion",DirCalle.getAddressLine(0));
                    direccion = DirCalle.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ditancia(Location locA){


        Location locationB = new Location("punto B");

        locationB.setLatitude(lat);
        locationB.setLongitude(lng);

        float distance = locA.distanceTo(locationB);
        //tvdistancia.setText(distance+"");
        distancia = distance+"";
    }

    public void obtenerDireccionRapido(Location loc)
    {
        Log.d("dirrapido",latdest+" "+lngdest);
        /*loc.setLatitude(Double.parseDouble(latdest));
        loc.setLongitude(Double.parseDouble(lngdest));
        lat=Double.parseDouble(latdest);//loc.getLatitude();
        lng=Double.parseDouble(lngdest);//loc.getLongitude();*/
        setLocation(loc);
        ditancia(loc);
    }

    public class Localizacion implements LocationListener {
        PMandaderoPendientes vista_detalle;
        public PMandaderoPendientes getDetallesPedidoMandadero() {
            return vista_detalle;
        }
        public void setVista_general(PMandaderoPendientes vista_detalle) {
            this.vista_detalle = vista_detalle;
        }


        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            lat=loc.getLatitude();
            lng=loc.getLongitude();
            String latitud = " "+ lat;
            String longitud=" "+ lng;
            //tvLatitud.setText(latitud);
            //tvLongitud.setText(longitud);
            this.vista_detalle.setLocation(loc);
            //this.vista_detalle.ditancia();

        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            //tvLatitud.setText(" GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            //tvLatitud.setText(" GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
}