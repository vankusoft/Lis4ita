package com.example.ivgeorgiev.lis4ita;

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

public class TeamsBackgroundService extends IntentService {

    DatabaseReference data;
    List<String> userList;

    public TeamsBackgroundService() {
        super("TeamsService");

        data=FirebaseDatabase.getInstance().getReference();
        userList=new ArrayList<>();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        getUsersFromDatabase();

        setTeams();

    }

    private void getUsersFromDatabase() {

        DatabaseReference gameRoomDB = data.child("game_room");

        gameRoomDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    userList.add(data.child("nick_name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setTeams() {

        DatabaseReference gameSettings = data.child("game_settings");

        final DatabaseReference dbTeams = data.child("teams");

        gameSettings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String teams_number = dataSnapshot.child("teams_number").getValue().toString();
                int teams = Integer.parseInt(teams_number);

                for (int i = 1; i <= teams; i++) {
                    dbTeams.child("team" + i).setValue(userList.get(i - 1));
                }

                if (userList.size() > teams) {

                    for (int player = teams; player < userList.size(); player++) {

                        int team_index=1;

                        dbTeams.child("team" + team_index).setValue(userList.get(player));

                        if(team_index==teams){
                            team_index=0;
                        }

                        ++team_index;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
