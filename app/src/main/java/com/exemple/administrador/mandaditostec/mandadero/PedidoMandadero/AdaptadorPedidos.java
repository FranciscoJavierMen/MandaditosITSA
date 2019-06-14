package com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrador.mandaditostec.R;

import java.util.ArrayList;

public class AdaptadorPedidos extends RecyclerView.Adapter<AdaptadorPedidos.HolderPedidosMandadero>{

    private ArrayList<PedidosMandadero> listaPedidos;

    public AdaptadorPedidos(ArrayList<PedidosMandadero> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    @Override
    public HolderPedidosMandadero onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_pedido, parent, false);
        return new HolderPedidosMandadero(view, mListener);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(HolderPedidosMandadero holder, int position) {
        PedidosMandadero pedidosmandadero = listaPedidos.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        holder.tvpedido.setText(pedidosmandadero.getPedido());
        holder.tvhora.setText(pedidosmandadero.getHora());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hacer algo
            }
        });
    }

    public void clear(){
        listaPedidos.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public void removeItem(int position){
        listaPedidos.remove(position);
        notifyItemRemoved(position);
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemListener){
        mListener = itemListener;
    }

    public class HolderPedidosMandadero extends RecyclerView.ViewHolder {

        private TextView tvpedido, tvhora;

        public HolderPedidosMandadero(View itemView, final OnItemClickListener listener) {
            super(itemView);

            tvpedido = itemView.findViewById(R.id.tvdescripcionpedido);
            tvhora = itemView.findViewById(R.id.tvhorapedido);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}

