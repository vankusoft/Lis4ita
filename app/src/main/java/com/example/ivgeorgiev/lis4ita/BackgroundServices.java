package com.example.ivgeorgiev.lis4ita;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BackgroundServices extends IntentService {

    private static final String TAG = "DBIntentService";

    List<String> players;

    DatabaseReference data;

    public BackgroundServices() {
        super("MyIntentService");

        data=FirebaseDatabase.getInstance().getReference();

        players=new ArrayList<>();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        final DatabaseReference gameSettingsDB=data.child("game_settings");

        gameSettingsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String playersNumber=dataSnapshot.child("players_number").getValue().toString();

                ResultReceiver rec = intent.getParcelableExtra("receiverTag");

                Bundle b= new Bundle();
                b.putString("ServiceTag",playersNumber);
                rec.send(0, b);

                gameSettingsDB.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
