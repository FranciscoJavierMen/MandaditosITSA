package com.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.administrador.mandaditostec.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistroCliente extends AppCompatActivity implements View.OnClickListener{

    private TextInputLayout txtNombre, txtCorreo, txtContrasena, txtConfirmar;
    private FloatingActionButton fabRegistrarMandadero;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cliente);
        Toolbar toolbar = findViewById(R.id.toolbarRegistroCliente);
        toolbar.setTitle("Registrarse como cliente");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        init();
    }

    private void getData(){
        String name = txtNombre.getEditText().getText().toString();
        String email = txtCorreo.getEditText().getText().toString();
        String password = txtContrasena.getEditText().getText().toString();

        if (validate()){
            progressDialog.setTitle("Registrando usuario");
            progressDialog.setMessage("Se esta creando tu cuenta");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            regiterUser(email, password);
        } else {
            Toast.makeText(RegistroCliente.this, "Algunos campos están vacios...", Toast.LENGTH_SHORT).show();
        }
    }

    //Registra un nuevo usuario
    private void regiterUser( String email, String pass){
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistroCliente.this, "Registro realizado con éxito", Toast.LENGTH_SHORT).show();
                            Intent inicio = new Intent(RegistroCliente.this, Acceder.class);
                            startActivity(inicio);
                            finish();
                        } else {
                            progressDialog.hide();
                            Toast.makeText(RegistroCliente.this, "No se ha podido realizar el registro, intentalo más tarde.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Valida que los campos no estén vacios
    private boolean validate(){
        boolean validate;
        String name = txtNombre.getEditText().getText().toString();
        String email = txtCorreo.getEditText().getText().toString();
        String password = txtContrasena.getEditText().getText().toString();
        String confirm_password = txtContrasena.getEditText().getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            validate = false;
        } else{
            validate = true;
        }

        if (!password.equals(confirm_password)){
            validate = false;
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }
        return validate;
    }

    private void init() {
        txtNombre = findViewById(R.id.txtNombreClienteR);
        txtCorreo = findViewById(R.id.txtCorreoClienteR);
        txtContrasena = findViewById(R.id.txtContrasenaCliente);
        txtConfirmar = findViewById(R.id.txtConfirmarContrasenaC);
        fabRegistrarMandadero = findViewById(R.id.fabRegistrarCliente);
        progressDialog = new ProgressDialog(this);

        fabRegistrarMandadero.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabRegistrarCliente:
                getData();
                break;
        }
    }
}
