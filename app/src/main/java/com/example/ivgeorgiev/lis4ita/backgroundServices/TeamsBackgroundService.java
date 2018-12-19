package com.example.ivgeorgiev.lis4ita.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeamsBackgroundService extends IntentService {

    DatabaseReference data;
    List<String> userList;
    Random random;

    public TeamsBackgroundService() {
        super("TeamsService");

        data = FirebaseDatabase.getInstance().getReference();
        userList = new ArrayList<>();

        random = new Random();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent.getStringExtra("game_room") != null) {

            String game_room = intent.getStringExtra("game_room");

            getUsersFromDatabase(game_room);

            setTeams(game_room);
        }
    }

    private void getUsersFromDatabase(String game_room) {

        final DatabaseReference gameRoomDB = data.child("game_room").child(game_room).child("players");

        gameRoomDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    userList.add(data.child("nick_name").getValue().toString());
                }

                gameRoomDB.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTeams(String game_room) {

        final DatabaseReference gameSettings = data.child("game_room").child(game_room).child("game_settings");

        final DatabaseReference dbTeams = data.child("game_room").child(game_room).child("teams");

        gameSettings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String teams_number = dataSnapshot.child("teams_number").getValue().toString();
                int teams = Integer.parseInt(teams_number);

                for (int i = 1; i <= teams; i++) {

                    int rann = random.nextInt(5000000);
                    String childRandomString = String.valueOf(rann);

                    dbTeams.child("team" + i).child(childRandomString).setValue(userList.get(i - 1));
                }

                if (userList.size() > teams) {

                    int team_index = 1;
                    int playerIndex = teams;

                    while (playerIndex < userList.size()) {

                        int rann = random.nextInt(5000000);
                        String childRandomString = String.valueOf(rann);

                        dbTeams.child("team" + team_index).child(childRandomString).setValue(userList.get(playerIndex));

                        if (team_index == teams)
                            team_index = 1;
                        else
                            ++team_index;

                        ++playerIndex;
                    }
                }

                gameSettings.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
