package com.example.administrador.mandaditostec.Cliente.Pedido.EstadoPedido;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.Info.AppInfo;
import com.Info.Desarrolladores;
import com.Info.Terminos;
import com.Login.Acceder;
import com.example.administrador.mandaditostec.Cliente.BottomNavigation;
import com.example.administrador.mandaditostec.Cliente.Pedido.FormDialog;
import com.example.administrador.mandaditostec.Cliente.checkNetworkConnection;
import com.example.administrador.mandaditostec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TabPedidos extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    //Instancia a Autentificación de Firebase
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    //Botón flotante
    private FloatingActionButton fabPedido;

    private com.example.administrador.mandaditostec.Cliente.checkNetworkConnection checkNetworkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_pedidos);

        checkNetworkConnection = new checkNetworkConnection(this);

        //Inicializa instancia de autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(TabPedidos.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        fabPedido = findViewById(R.id.fabPedido);
        setButtonListener();

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    private void setButtonListener(){
        if (checkNetworkConnection.isConnected()){
            fabPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    abrirDialogo();
                }
            });
        } else {
            fabPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(TabPedidos.this, "No estas conectado a internet\nIntentalo más tarde.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //Abre el dialogo con el formulario de pedidos
    private void abrirDialogo(){
        FormDialog.display(getSupportFragmentManager());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_pedidos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_about:
                startActivity(new Intent(TabPedidos.this, AppInfo.class));
                break;
            case R.id.action_devs:
                startActivity(new Intent(TabPedidos.this, Desarrolladores.class));
                break;
            case R.id.action_terms:
                startActivity(new Intent(TabPedidos.this, Terminos.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new Pendiente();
                case 1:
                    return new Aceptado();
                case 2:
                    return new Finalizado();
                case 3:
                    return new Rechazado();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }
}
