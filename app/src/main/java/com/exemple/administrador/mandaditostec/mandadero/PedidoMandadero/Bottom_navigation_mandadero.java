package com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.administrador.mandaditostec.R;

public class Bottom_navigation_mandadero extends AppCompatActivity implements PedidosMandaderosFragment.OnFragmentInteractionListener {
    private TextView mTextMessage;
    private PedidosMandaderosFragment FragmentPedidos;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_pedidos_mandaderos:
                    setFragment(FragmentPedidos);
                    return true;
                case R.id.navigation_perfil_mandadero:
                    mTextMessage.setText(R.string.title_dashboard);
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

        FragmentPedidos = new PedidosMandaderosFragment();
        setFragment(FragmentPedidos);
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
