package com.example.ivgeorgiev.lis4ita.backgroundServices;

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

public class InGameBackgroundService extends IntentService {

    DatabaseReference databaseReference;
    ArrayList<String> listWords;

    public InGameBackgroundService() {
        super("InGameBackgroundService");

        listWords = new ArrayList<>();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        if (intent.getStringExtra("game_room") != null) {

            String game_room = intent.getStringExtra("game_room");

            databaseReference = FirebaseDatabase.getInstance().getReference().child("game_room").child(game_room).child("words");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        listWords.add(data.getValue().toString());
                    }

                    ResultReceiver rec = intent.getParcelableExtra("ResultTag");
                    Bundle b = new Bundle();
                    b.putStringArrayList("list", listWords);
                    rec.send(0, b);

                    databaseReference.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
