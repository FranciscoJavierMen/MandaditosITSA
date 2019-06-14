package com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.Login.Acceder;
import com.example.administrador.mandaditostec.R;
import com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TodosMandados extends AppCompatActivity implements PMandaderoPendientes.OnFragmentInteractionListener {


    //Instancia a Autentificación de Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos_mandados);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this,
                getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //Inicializa instancia de autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Toast.makeText(this, "User: "+currentUser.getUid(), Toast.LENGTH_SHORT).show();

        //Usuario no logeado regresa a activity de registro
        if (currentUser == null){
            backToWelcome();
        }
    }

    //Método para volver a activitu de logeo y registro
    private void backToWelcome() {
        Intent start = new Intent(TodosMandados.this, Acceder.class);
        startActivity(start);
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}