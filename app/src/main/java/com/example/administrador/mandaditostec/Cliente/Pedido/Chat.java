package com.example.administrador.mandaditostec.Cliente.Pedido;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrador.mandaditostec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private String nombreMandadero, idMandadero;

    private DatabaseReference databaseReference;
    private String current_user;
    private EditText edtmensaje;

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
            nombreMandadero = intent.getStringExtra("mandadero");
        } catch (Exception e){
            e.printStackTrace();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(nombreMandadero);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edtmensaje = findViewById(R.id.edtMensaje);
        setChat();

        FloatingActionButton fab = findViewById(R.id.fabSendMessage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
                edtmensaje.setText("");
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
            messageMap.put("mensaje", mensaje);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);

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
