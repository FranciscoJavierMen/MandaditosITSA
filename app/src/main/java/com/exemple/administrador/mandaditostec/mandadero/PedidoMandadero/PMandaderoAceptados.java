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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.Cliente.Pedido.FormDialog;
import com.example.administrador.mandaditostec.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class PMandaderoAceptados extends Fragment {


    public PMandaderoAceptados() {
        // Required empty public constructor
    }


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FloatingActionButton fabPedido;

    LocationManager mlocManager;
    Localizacion Local;
    Location lastLocation;

    Double lat = 0.0, lng = 0.0;
    String latorig="0.0", lngorig="0.0", latdest="0.0", lngdest="0.0", distancia="0.0", direccion="0.0";

    //Lista y modelo
    private RecyclerView recyclerAceptados;
    private AdaptadorPedidos adaptadorPedidos;
    private ArrayList<PedidosMandadero> listaPedidos = new ArrayList<>();
    private SwipeRefreshLayout refreshPedidos;
    private CoordinatorLayout coordinatorLayout;

    //Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference  referenceAceptados;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pmandadero_aceptados, container, false);
        //inicializarFirebase();
        inicializarComponentes(view);

        referenceAceptados = FirebaseDatabase.getInstance().getReference().child("Pedido");

        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getActivity());
        ((LinearLayoutManager) nLayoutManager).setReverseLayout(true);
        ((LinearLayoutManager) nLayoutManager).setStackFromEnd(false);

        recyclerAceptados.setLayoutManager(nLayoutManager);

        return view;
    }

    public void Aceptados() {
        FirebaseRecyclerOptions<PedidosMandadero> opciones
                = new FirebaseRecyclerOptions.Builder<PedidosMandadero>()
                .setQuery(referenceAceptados.orderByChild("estado").equalTo("aceptado"), PedidosMandadero.class)
                .build();

        FirebaseRecyclerAdapter<PedidosMandadero, PMandaderoAceptados.PedidosMandaderoAceptadoViewHolder> adapter
                = new FirebaseRecyclerAdapter<PedidosMandadero, PedidosMandaderoAceptadoViewHolder>(opciones) {
            @Override
            protected void onBindViewHolder(final PedidosMandaderoAceptadoViewHolder holder, int position, PedidosMandadero model) {
                final String pedidoID = getRef(position).getKey();

                referenceAceptados.child(pedidoID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            //inal String direccionD = dataSnapshot.child("mandadero").getValue().toString();
                            final String pedido = dataSnapshot.child("pedido").getValue().toString();
                            final String fecha = dataSnapshot.child("hora").getValue().toString();
                            final String latorigen = dataSnapshot.child("latitudOrigen").getValue().toString();
                            final String latdestino = dataSnapshot.child("latitudDestino").getValue().toString();
                            final String lngdestino = dataSnapshot.child("longitudDestino").getValue().toString();
                            final String lngorigen = dataSnapshot.child("longitudOrigen").getValue().toString();

                            holder.tvpedido.setText(pedido);
                            holder.tvhora.setText(fecha);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    latorig = latorigen + "";
                                    lngorig = lngorigen + "";
                                    latdest = latdestino + "";
                                    lngdest = lngdestino + "";

                                    double latd=Double.parseDouble(latdestino);
                                    double lngd=Double.parseDouble(lngdestino);

                                    Location locationA = new Location("Destino");

                                    locationA.setLatitude(latd);
                                    locationA.setLongitude(lngd);

                                    obtenerDireccionRapido(locationA);
                                    DetallesPedidoMandadero.distancia = distancia;
                                    DetallesPedidoMandadero.direccion = direccion;
                                    DetallesPedidoMandadero.key = pedidoID;
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
            public PMandaderoAceptados.PedidosMandaderoAceptadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_pedido, parent, false);
                return new PMandaderoAceptados.PedidosMandaderoAceptadoViewHolder(view);
            }
        };
        recyclerAceptados.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public void onStart() {
        super.onStart();

        Aceptados();

    }

    private static class PedidosMandaderoAceptadoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvpedido, tvhora;

        public PedidosMandaderoAceptadoViewHolder(View itemView) {
            super(itemView);

            tvpedido = itemView.findViewById(R.id.tvdescripcionpedido);
            tvhora = itemView.findViewById(R.id.tvhorapedido);
        }
    }

    private void inicializarComponentes(View view) {

        recyclerAceptados = view.findViewById(R.id.recyclerPedidosMandaderosAceptados);
        //refreshPedidos = view.findViewById(R.id.refreshPedidos);
        //fabPedido = view.findViewById(R.id.fabPedido);
        //coordinatorLayout = view.findViewById(R.id.fragmentpedidos);





    }

    private void abrirDialogo() {
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
        if (context instanceof PMandaderoAceptados.OnFragmentInteractionListener) {
            mListener = (PMandaderoAceptados.OnFragmentInteractionListener) context;
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
        PMandaderoAceptados vista_detalle;
        public PMandaderoAceptados getDetallesPedidoMandadero() {
            return vista_detalle;
        }
        public void setVista_general(PMandaderoAceptados vista_detalle) {
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
            //this.vista_detalle.setLocation(loc);
            this.vista_detalle.ditancia(loc);

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

    //termina



}

