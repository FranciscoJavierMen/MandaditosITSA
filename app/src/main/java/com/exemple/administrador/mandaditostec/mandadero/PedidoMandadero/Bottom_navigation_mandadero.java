package com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.Login.Acceder;
import com.example.administrador.mandaditostec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Bottom_navigation_mandadero extends AppCompatActivity implements PMandaderoPendientes.OnFragmentInteractionListener {
    private TextView mTextMessage;
    private PMandaderoPendientes FragmentPedidos;
    //Instancia a Autentificación de Firebase
    private FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_pedidos_mandaderos:
                    setFragment(FragmentPedidos);
                    return true;
                case R.id.navigation_perfil_mandadero:
                    FirebaseAuth.getInstance().signOut();
                    backToWelcome();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_navigation_mandadero);
        BottomNavigationView navView = findViewById(R.id.navigation_mandadero);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Inicializa instancia de autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        FragmentPedidos = new PMandaderoPendientes();
        setFragment(FragmentPedidos);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Usuario no logeado regresa a activity de registro
        if (currentUser == null){
            backToWelcome();
        }
    }

    //Método para volver a activitu de logeo y registro
    private void backToWelcome() {
        Intent start = new Intent(Bottom_navigation_mandadero.this, Acceder.class);
        startActivity(start);
        finish();
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
