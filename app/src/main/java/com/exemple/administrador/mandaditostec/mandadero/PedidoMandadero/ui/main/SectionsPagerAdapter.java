package com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero.ui.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.administrador.mandaditostec.R;
import com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero.PMandaderoAceptados;
import com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero.PMandaderoRealizados;
import com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero.PMandaderoPendientes;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2,R.string.tab_text_3};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment tabFragment = null;

        switch (position){
            case 0:
                tabFragment = new PMandaderoPendientes();
                break;
            case 1:
                tabFragment = new PMandaderoRealizados();
                break;
            case 2:
                tabFragment = new PMandaderoAceptados();
                break;
        }

        return tabFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}