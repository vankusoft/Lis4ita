package com.example.ivgeorgiev.lis4ita;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FetchUsers extends AppCompatActivity implements MyResultReceiver.Receiver {

    ListView listView;

    DatabaseReference data;

    ArrayAdapter<String> arrayAdapter;
    List<String> userList;

    MyResultReceiver myResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_users);

        data = FirebaseDatabase.getInstance().getReference();

        init();

        myResultReceiver = new MyResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);

        getUsersFromDatabase();
    }

    private void init() {

        listView = findViewById(R.id.usersList);
        userList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(arrayAdapter);
    }

    private void getUsersFromDatabase() {

        DatabaseReference gameRoomDB = data.child("game_room");

        gameRoomDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    userList.add(data.child("nick_name").getValue().toString());
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultData.getString("ServiceTag") != null) {

            Toast.makeText(FetchUsers.this, resultData.getString("ServiceTag"), Toast.LENGTH_SHORT).show();

            String players = resultData.getString("ServiceTag");
            int numberOfPlayers = Integer.parseInt(players);

            if (numberOfPlayers == userList.size()) {

                Intent teamService = new Intent(getBaseContext(), TeamsBackgroundService.class);
                startService(teamService);

                Intent i = new Intent(FetchUsers.this, SubmitWords.class);
                startActivity(i);
            }
        }
    }



    public void startGameOnClick(View view) {

        Intent intentService = new Intent(FetchUsers.this, BackgroundServices.class);
        intentService.putExtra("receiverTag", myResultReceiver);
        startService(intentService);
    }

    public void settingsGameOnClick(View view) {
        Intent intent = new Intent(FetchUsers.this, GameSettings.class);
        startActivity(intent);
    }
}
