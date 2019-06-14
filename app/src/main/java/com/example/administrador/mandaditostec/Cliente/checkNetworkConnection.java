package com.example.administrador.mandaditostec.Cliente;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//Clase para verificar el estado de la conexión a internet
public class checkNetworkConnection {

    Context context;

    public checkNetworkConnection(Context context){
        this.context = context;
    }

    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null){
                return networkInfo.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }
}
