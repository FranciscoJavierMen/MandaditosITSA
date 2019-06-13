package com.example.administrador.mandaditostec.Cliente.Pedido;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.administrador.mandaditostec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AdaptadorMensajes extends RecyclerView.Adapter<AdaptadorMensajes.ViewHolder>{

    private List<Mensajes> listMensajes;
    private String current_user_id;

    AdaptadorMensajes(List<Mensajes> listMensajes) {
        this.listMensajes = listMensajes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensajes, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            current_user_id = user.getUid();
        }

        Mensajes m = listMensajes.get(position);

        String from_user = m.getFrom();

        if (from_user.equals(current_user_id)){
            holder.txtMensaje.setBackgroundResource(R.drawable.text_message_background_send);
            holder.txtMensaje.setTextColor(Color.BLACK);
        } else {
            holder.txtMensaje.setBackgroundResource(R.drawable.text_message_background);
            holder.txtMensaje.setTextColor(Color.WHITE);
        }
        holder.txtMensaje.setText(m.getMessage());
    }

    @Override
    public int getItemCount() {
        return listMensajes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMensaje;

        ViewHolder(View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
        }
    }
}
