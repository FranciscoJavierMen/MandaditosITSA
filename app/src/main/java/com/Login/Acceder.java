package com.Login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.Cliente.BottomNavigation;
import com.example.administrador.mandaditostec.Cliente.checkNetworkConnection;
import com.example.administrador.mandaditostec.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Acceder extends AppCompatActivity implements View.OnClickListener{

    private TextInputLayout txtCorreo, txtContrasena;
    private AppCompatButton btnAcceder;
    private TextView txtCrear;
    checkNetworkConnection checkNetworkConnection;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceder);

        checkNetworkConnection = new checkNetworkConnection(this);
        mAuth = FirebaseAuth.getInstance();

        init();
    }

    private void init(){
        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnAcceder = findViewById(R.id.btnAcceder);
        txtCrear = findViewById(R.id.txtCrearCuenta);
        progressDialog = new ProgressDialog(this);

        txtCrear.setOnClickListener(this);
        btnAcceder.setOnClickListener(this);
    }

    private void getData(){
        String email = txtCorreo.getEditText().getText().toString();
        String password = txtContrasena.getEditText().getText().toString();

        if (validate()){
            progressDialog.setTitle("Accediendo");
            progressDialog.setMessage("Accediendo a tu cuenta...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            loginUser(email, password);
        } else {
            Toast.makeText(Acceder.this, "Algunos campos están vacios...", Toast.LENGTH_SHORT).show();
        }
    }

    //Valida que los campos no estén vacios
    private boolean validate(){
        boolean validate;
        String name = txtCorreo.getEditText().getText().toString();
        String password = txtContrasena.getEditText().getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)){
            validate = false;
        } else{
            validate = true;
        }
        return validate;
    }

    //Método para acceder con usuarios registrados
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent inicio = new Intent(Acceder.this, BottomNavigation.class);
                            startActivity(inicio);
                            finish();
                        } else {
                            progressDialog.hide();
                            Toast.makeText(Acceder.this, "No se ha podido acceder", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    //Muestra mensaje de confirmación de recepción
    protected void showDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);

        View view  = this.getLayoutInflater().inflate(R.layout.dialogo_seleccionar_cuenta, null);
        dialog.setContentView(view);

        AppCompatButton btnMandadero = view.findViewById(R.id.btnCuentaMandadero);
        AppCompatButton btnCliente = view.findViewById(R.id.btnCuentaCliente);

        btnMandadero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent i = new Intent(getApplicationContext(), RegistroMandadero.class);
                startActivity(i);
            }
        });

        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent i = new Intent(getApplicationContext(), RegistroCliente.class);
                startActivity(i);
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAcceder:
                if (checkNetworkConnection.isConnected()){
                    getData();
                } else {
                    Toast.makeText(this, "No tienes conexion a internet\nIntentalo más tarde.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txtCrearCuenta:
                showDialog();
                break;
        }
    }
}
