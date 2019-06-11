package com.example.administrador.mandaditostec.Cliente.Pedido;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrador.mandaditostec.Cliente.Mapa.Maps;
import com.example.administrador.mandaditostec.Cliente.checkNetworkConnection;
import com.example.administrador.mandaditostec.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public class FormDialog extends DialogFragment implements View.OnClickListener{

    public static final String TAG = "Nuevo pedido";
    private final static int MAP_POINT1 = 999;
    private final static int MAP_POINT2 = 998;
    private Toolbar toolbar;
    private String latO, lonO, latD, lonD;
    
    private FloatingActionButton fabEnviarPedido;
    private AppCompatButton btnMandadero, btnDireccionOrigen, btnDireccionDestino;
    private TextView txtMandadero, txtDireccionOrigen, txtDireccionDestino, txtPrevMandadero,
            txtPrevPedido, txtPrevOrigen, txtPrevDestino;
    private EditText edtDescripcionPedido;

    private DatabaseReference databaseReference;
    private com.example.administrador.mandaditostec.Cliente.checkNetworkConnection checkNetworkConnection;

    public static FormDialog display(FragmentManager fragmentManager) {
        FormDialog formDialog = new FormDialog();
        formDialog.show(fragmentManager, TAG);
        return formDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(FormDialog.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
        checkNetworkConnection = new checkNetworkConnection(getContext());

    }

    @Override
    public void onStart() {
        super.onStart();
        
        if (checkNetworkConnection.isConnected()){
            Dialog dialog = getDialog();
            if (dialog != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
            }
        } else {
            Toast.makeText(getContext(), "No puedes realizar mandados sin conexión.\nConectate a internet y vuelve a intentar.", Toast.LENGTH_SHORT).show();
            dismiss();
        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_form_dialog, container, false);

        toolbar = view.findViewById(R.id.toolbarForm);
        inicializar(view);
        inicializarFirebase();

        fabEnviarPedido.setOnClickListener(this);
        btnMandadero.setOnClickListener(this);
        btnDireccionDestino.setOnClickListener(this);
        btnDireccionOrigen.setOnClickListener(this);
        return view;
    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(getActivity().getApplicationContext());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void inicializar(View view) {
        fabEnviarPedido = view.findViewById(R.id.fabEnviarPedido);
        btnMandadero = view.findViewById(R.id.btnSeleccionarMandadero);
        btnDireccionDestino = view.findViewById(R.id.btnSeleccionarDestino);
        btnDireccionOrigen = view.findViewById(R.id.btnSeleccionarOrigen);
        txtMandadero = view.findViewById(R.id.txtMandadero);
        txtDireccionOrigen = view.findViewById(R.id.txtDireccionOrigen);
        txtDireccionDestino = view.findViewById(R.id.txtDireccionDestino);
        edtDescripcionPedido = view.findViewById(R.id.edtDescripcionPedido);
    }

    private void seleccionarOrigen(){
        Intent origen = new Intent(getActivity(), Maps.class);
        startActivityForResult(origen, MAP_POINT1);
    }

    private void seleccionarDestino(){
        Intent destino = new Intent(getActivity(), Maps.class);
        startActivityForResult(destino, MAP_POINT2);
    }

    //Obtiene la dirección origen de las coordenadas dadas
    private void setDireccionOrigen(double lat, double lon) {
        String cityName = "";
        String addresName = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try{
            addresses = geocoder.getFromLocation(lat, lon, 10);
            if (addresses.size() > 0){
                for (Address adr: addresses){
                    if (adr.getLocality() != null && adr.getLocality().length() > 0){
                        cityName = adr.getLocality();
                        addresName = adr.getAddressLine(0);
                        txtDireccionOrigen.setText(addresName+""+cityName);
                        break;
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Obtiene la dirección origen de las coordenadas dadas
    private void setDireccionDestino(double lat, double lon){
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
                        txtDireccionDestino.setText(addresName+""+cityName);
                        break;
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Establece los datos de la vista previa del madado
    private void setValuesPreview(View view){

        txtPrevMandadero = view.findViewById(R.id.txtPrevMandadero);
        txtPrevPedido = view.findViewById(R.id.txtPrevPedido);
        txtPrevOrigen = view.findViewById(R.id.txtPrevOrigen);
        txtPrevDestino = view.findViewById(R.id.txtPrevDestino);

        String mandadero = txtMandadero.getText().toString();
        String pedido = edtDescripcionPedido.getText().toString();
        String origen = txtDireccionOrigen.getText().toString();
        String destino = txtDireccionDestino.getText().toString();

        txtPrevMandadero.setText(mandadero);
        txtPrevPedido.setText(pedido);
        txtPrevOrigen.setText(origen);
        txtPrevDestino.setText(destino);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MAP_POINT1){
            try{
                LatLng latLng = data.getParcelableExtra("punto_seleccionado");
                setDireccionOrigen(latLng.latitude, latLng.longitude);
                latO = String.valueOf(latLng.latitude);
                lonO = String.valueOf(latLng.longitude);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if (requestCode == MAP_POINT2){
            try{
                LatLng latLng = data.getParcelableExtra("punto_seleccionado");
                setDireccionDestino(latLng.latitude, latLng.longitude);
                latD = String.valueOf(latLng.latitude);
                lonD = String.valueOf(latLng.longitude);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Pedido cancelado...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        try{
            switch (view.getId()){
                case R.id.fabEnviarPedido:
                    if (validarPedido()){
                        showDialogConfirmacion();
                    }
                    break;
                case R.id.btnSeleccionarMandadero:
                    Toast.makeText(getActivity().getApplicationContext(), "Seleccionar mandadero", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnSeleccionarDestino:
                    seleccionarDestino();
                    break;
                case R.id.btnSeleccionarOrigen:
                    seleccionarOrigen();
                    break;
                    default:
                        break;

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Obtiene la fecha y hora acual
    private String getdateTime(){
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String currentHour = format.format(calendar.getTime());
        return currentDate +" - "+ currentHour;
    }

    private void enviarPedido() {
        if(validarPedido()){
            String pedido, mandadero;

            pedido = edtDescripcionPedido.getText().toString();
            mandadero = txtMandadero.getText().toString();
            Long tsLong = System.currentTimeMillis()/1000;
            String timestamps = tsLong.toString();

            ModeloPedidos modelo = new ModeloPedidos();

            modelo.setId(timestamps);
            modelo.setMandadero(mandadero);
            modelo.setLatitudOrigen(latO);
            modelo.setLongitudOrigen(lonO);
            modelo.setLatitudDestino(latD);
            modelo.setLongitudDestino(lonD);
            modelo.setPedido(pedido);
            modelo.setHora(getdateTime());
            modelo.setEstado("pendiente");

            databaseReference.child("Pedido").child(modelo.getId()).setValue(modelo);

            showDialog();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Llenar todos los campos requeridos", Toast.LENGTH_SHORT).show();
        }
    }

    protected void showDialog(){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view  = this.getLayoutInflater().inflate(R.layout.dialog_pedido_enviado, null);
        dialog.setContentView(view);

        AppCompatButton btnOk = view.findViewById(R.id.btnBotonOK);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    protected void showDialogConfirmacion(){

        final Dialog dialogConfirm = new Dialog(getActivity());
        dialogConfirm.setCancelable(true);

        View view  = this.getLayoutInflater().inflate(R.layout.dialog_pregunta_pedido, null);
        dialogConfirm.setContentView(view);
        setValuesPreview(view);

        AppCompatButton btnEnviar = view.findViewById(R.id.btnBotonEnviar);
        AppCompatButton btnEditar = view.findViewById(R.id.btnBotonEditar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarPedido();
                dismiss();
                dialogConfirm.dismiss();
            }
        });
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
            }
        });

        dialogConfirm.show();
    }

    private boolean validarPedido() {
        boolean validar = true;
        String pedido, mandadero, direccionOrigen, direccionDestino;

        pedido = edtDescripcionPedido.getText().toString();
        mandadero = txtMandadero.getText().toString();
        direccionOrigen = txtDireccionOrigen.getText().toString();
        direccionDestino = txtDireccionDestino.getText().toString();

        if (pedido.equals("") || mandadero.equals("AMandadero no seleccionado") 
                || direccionOrigen.equals("Dirección no seleccionada") 
                || direccionDestino.equals("Dirección no seleccionada")){
            validar = false;
        }

        if (pedido.equals("")){
            edtDescripcionPedido.setError("Campo requerido");
        }

        if (mandadero.equals("Nombre del mandaderos")){
            txtMandadero.setError("Requerido");
            txtMandadero.setText("Debe seleccionar un mandadero");
            txtMandadero.setTextColor(getResources().getColor(R.color.rojo, null));
        } else {
            txtMandadero.setTextColor(getResources().getColor(R.color.negro, null));
        }

        if (direccionOrigen.equals("Dirección no seleccionada")){
            txtDireccionDestino.setError("Requerido");
            txtDireccionDestino.setText("Debe seleccionar dirección ");
            txtDireccionDestino.setTextColor(getResources().getColor(R.color.rojo, null));
        } else {
            txtDireccionDestino.setTextColor(getResources().getColor(R.color.negro, null));
        }
        
        if (direccionDestino.equals("Dirección no seleccionada")){
            txtDireccionDestino.setError("Requerido");
            txtDireccionDestino.setText("Debe seleccionar dirección ");
            txtDireccionDestino.setTextColor(getResources().getColor(R.color.rojo, null));
        } else {
            txtDireccionDestino.setTextColor(getResources().getColor(R.color.negro, null));
        }

        return validar;
    }
}
