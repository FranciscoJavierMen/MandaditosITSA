package com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.Login.Acceder;
import android.support.v4.app.FragmentManager;
import com.example.administrador.mandaditostec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TodosMandados extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    //Instancia a Autentificación de Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos_mandados);

        //Inicializa instancia de autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.view_pagerM);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabsM);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
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
        Intent start = new Intent(TodosMandados.this, Acceder.class);
        startActivity(start);
        finish();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new PMandaderoPendientes();
                case 1:
                    return new PMandaderoAceptados();
                case 2:
                    return new PMandaderoRealizados();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}