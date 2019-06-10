package com.example.administrador.mandaditostec.Cliente;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.Login.Acceder;
import com.example.administrador.mandaditostec.Cliente.Mandaderos.FragmentMandaderos;
import com.example.administrador.mandaditostec.Cliente.Pedido.FragmentPedidos;
import com.example.administrador.mandaditostec.Cliente.Perfil.FragmentPerfil;
import com.example.administrador.mandaditostec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BottomNavigation extends AppCompatActivity implements
        FragmentPedidos.OnFragmentInteractionListener,
        FragmentMandaderos.OnFragmentInteractionListener,
        FragmentPerfil.OnFragmentInteractionListener{
    //Declaración del menú de navegación
    private BottomNavigationView navigation;

    //Declaración de los fragmentos
    private FragmentPedidos fragmentPedidos;
    private FragmentMandaderos fragmentMandaderos;
    private FragmentPerfil fragmentPerfil;

    //Instancia a Autentificación de Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        //Inicializa instancia de autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        //Creando las instancias de los fragmentos
        fragmentPedidos = new FragmentPedidos();
        fragmentMandaderos = new FragmentMandaderos();
        fragmentPerfil = new FragmentPerfil();

        setFragment(fragmentPedidos);//Establece fragment depor defecto de inicio
        //Instanciando la vista del menú de navegación
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navigationListener);//Listener
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Usuario no logeado regresa a activity de registro
        if (currentUser == null){
            backToWelcome();
        }
    }

    //Método para volver a activitu de logeo y registro
    private void backToWelcome() {
        Intent start = new Intent(BottomNavigation.this, Acceder.class);
        startActivity(start);
        finish();
    }

    //Listener para el menu de navegación
    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_pedidos:
                    setFragment(fragmentPedidos);
                    return true;
                case R.id.navigation_mandaderos:
                    setFragment(fragmentMandaderos);
                    return true;
                case R.id.navigation_perfil:
                    setFragment(fragmentPerfil);
                    return true;
                    default:
                        return false;
            }
        }
    };

    //Método para establecer el fragment seleccionado dentro del FrameLayout
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
