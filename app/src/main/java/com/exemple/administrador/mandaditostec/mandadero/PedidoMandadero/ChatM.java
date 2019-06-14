package com.example.administrador.mandaditostec.Cliente.Pedido;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.mandaditostec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private String nombreMandadero, idMandadero;

    private DatabaseReference databaseReference;
    private String current_user;
    private EditText edtmensaje;
    private final List<Mensajes> listaMensajes = new ArrayList<>();;
    private AdaptadorMensajes adapter;
    private RecyclerView recyclerMensajes;
    private SwipeRefreshLayout refreshChat;
    private LinearLayoutManager linearLayoutManager;

    private static final int TOTAL_MESSAGES = 15;
    private int currentPage = 1;
    private int itemPos = 0;
    private String lastKey = "";
    private String prevKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            current_user = user.getUid();
        }

        try{
            Intent intent = getIntent();
            idMandadero = intent.getStringExtra("idMandadero");
            nombreMandadero = intent.getStringExtra("nombreMandadero");

            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(nombreMandadero);
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }

        refreshChat = findViewById(R.id.refreshChat);
        edtmensaje = findViewById(R.id.edtMensaje);
        recyclerMensajes = findViewById(R.id.recyclerMensajes);
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerMensajes.setHasFixedSize(true);
        recyclerMensajes.setLayoutManager(linearLayoutManager);

        cargarMensajes();
        adapter = new AdaptadorMensajes(listaMensajes);
        recyclerMensajes.setAdapter(adapter);
        setChat();

        FloatingActionButton fab = findViewById(R.id.fabSendMessage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
            }
        });

        refreshChat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                itemPos = 0;
                cargarMasMensajes();
            }
        });
    }

    private void cargarMasMensajes() {
        final DatabaseReference reference = databaseReference.child("mensajes").child(current_user).child(idMandadero);
        Query query = reference.orderByKey().endAt(lastKey).limitToLast(TOTAL_MESSAGES);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Mensajes msj = dataSnapshot.getValue(Mensajes.class);
                String messageKey = dataSnapshot.getKey();

                if (!prevKey.equals(messageKey)){
                    listaMensajes.add(itemPos++, msj);
                } else {
                    prevKey = lastKey;
                }

                if (itemPos == 1){
                    lastKey = messageKey;
                }

                adapter.notifyDataSetChanged();
                refreshChat.setRefreshing(false);
                linearLayoutManager.scrollToPositionWithOffset(TOTAL_MESSAGES, 0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cargarMensajes() {
        final DatabaseReference reference = databaseReference.child("mensajes").child(current_user).child(idMandadero);
        Query query = reference.limitToLast(currentPage * TOTAL_MESSAGES);
            query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Mensajes msj = dataSnapshot.getValue(Mensajes.class);

                        itemPos++;
                        if (itemPos == 1){
                            String messageKey = dataSnapshot.getKey();
                            lastKey = messageKey;
                            prevKey = messageKey;
                        }

                        listaMensajes.add(msj);
                        adapter.notifyDataSetChanged();

                        recyclerMensajes.scrollToPosition(listaMensajes.size() -1);
                        refreshChat.setRefreshing(false);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setChat(){
        databaseReference.child("Chat").child(current_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(idMandadero)){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+current_user+"/"+idMandadero, chatAddMap);
                    chatUserMap.put("Chat/"+idMandadero+"/"+current_user, chatAddMap);

                    databaseReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Log.d("CHAT LOG", databaseError.getMessage().toString());
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enviarMensaje() {
        String mensaje = edtmensaje.getText().toString();

        if(!TextUtils.isEmpty(mensaje)){
            String current_user_ref = "mensajes/" + current_user + "/" + idMandadero;
            String chat_user_ref = "mensajes/" + idMandadero + "/" + current_user;

            DatabaseReference user_message_push = databaseReference.child("mensajes")
                    .child(current_user).child(idMandadero).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", mensaje);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", current_user);

            edtmensaje.setText("");

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            databaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null){
                        Log.d("MESSAGE LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }

}
