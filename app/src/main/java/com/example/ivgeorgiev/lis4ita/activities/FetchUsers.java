package com.example.ivgeorgiev.lis4ita.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ivgeorgiev.lis4ita.adaptors.MyResultReceiver;
import com.example.ivgeorgiev.lis4ita.R;
import com.example.ivgeorgiev.lis4ita.backgroundServices.BackgroundServices;
import com.example.ivgeorgiev.lis4ita.backgroundServices.TeamsBackgroundService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FetchUsers extends AppCompatActivity implements MyResultReceiver.Receiver {

    ListView listView;
    Button settingsButton;

    DatabaseReference data;
    FirebaseAuth auth;

    ArrayAdapter<String> arrayAdapter;
    List<String> userList;

    MyResultReceiver myResultReceiver;

    ProgressDialog dialog;

    String game_room = " ";
    boolean settings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_users);

        //GETTING GAME ROOM STRING FROM MAIN ACTIVITY
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            game_room = extras.getString("game_room");
        }

        data = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        myResultReceiver = new MyResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);

        getUsersFromDatabase();

        init();

        settingsButtonAvailability();
    }

    private void settingsButtonAvailability() {

        if (auth.getCurrentUser() != null) {

            final String user_id = auth.getCurrentUser().getUid();

            final DatabaseReference databaseReference = data.child("game_room").child(game_room);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String admin_id = dataSnapshot.child("admin").getValue().toString();

                    if (user_id.contentEquals(admin_id)) {
                        settings = true;
                    }

                    if (!settings) {
                        settingsButton.setEnabled(false);
                    } else {
                        settingsButton.setEnabled(true);
                    }

                    databaseReference.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void init() {
        settingsButton = findViewById(R.id.settingsGameButton);
        listView = findViewById(R.id.usersList);
        userList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(arrayAdapter);
    }

    private void getUsersFromDatabase() {

        if (!game_room.contentEquals(" ")) {

            DatabaseReference gameRoomDB = data.child("game_room").child(game_room).child("players");

            gameRoomDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (userList.size() != 0)
                        userList.clear();

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
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultData.getString("ServiceTag") != null) {

            Toast.makeText(FetchUsers.this, "Players:" + resultData.getString("ServiceTag"), Toast.LENGTH_SHORT).show();

            String players = resultData.getString("ServiceTag");
            int numberOfPlayers = Integer.parseInt(players);

            dialog.dismiss();

            if (numberOfPlayers == userList.size()) {

                //Background operation of setting up teams
                Intent teamService = new Intent(getBaseContext(), TeamsBackgroundService.class);
                teamService.putExtra("game_room", game_room);
                startService(teamService);

                //Start activity for submitting words
                Intent i = new Intent(FetchUsers.this, SubmitWords.class);
                i.putExtra("game_room", game_room);
                startActivity(i);
            }
        } else {
            dialog.dismiss();
            Toast.makeText(FetchUsers.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    public void startGameOnClick(View view) {

        final DatabaseReference databaseReference=data.child("game_room").child(game_room);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("game_settings").exists()){
                    dialog = ProgressDialog.show(FetchUsers.this, "Loading", "Please wait...", true);

                    //Background operation of getting players_number from game_settings
                    Intent intentService = new Intent(FetchUsers.this, BackgroundServices.class);
                    intentService.putExtra("receiverTag", myResultReceiver);
                    intentService.putExtra("game_room", game_room);
                    startService(intentService);
                }else{
                    Toast.makeText(FetchUsers.this,"There are not game options selected yet!", Toast.LENGTH_SHORT).show();
                }

                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void settingsGameOnClick(View view) {
        Intent intent = new Intent(FetchUsers.this, GameSettings.class);
        intent.putExtra("game_room", game_room);
        startActivity(intent);
    }
}
