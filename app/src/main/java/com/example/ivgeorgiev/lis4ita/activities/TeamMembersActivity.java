package com.example.ivgeorgiev.lis4ita.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.ivgeorgiev.lis4ita.adaptors.ListViewCustomAdapter;
import com.example.ivgeorgiev.lis4ita.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class TeamMembersActivity extends AppCompatActivity {

    ListView listView;
    ListViewCustomAdapter listViewCustomAdapter;

    ArrayList<ArrayList<String>> playersList;
    DatabaseReference databaseReference;

    int index = 0;
    int teamIndex = 1;

    String game_room = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_members);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            game_room = extras.getString("game_room");
        }

        init();

        databaseConnection();
    }

    private void init() {
        listView = findViewById(R.id.playersListView);
        playersList = new ArrayList<>();
    }

    private void databaseConnection() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("game_room").child(game_room).child("teams");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    playersList.add(new ArrayList<String>());

                    ArrayList<String> list = playersList.get(index);

                    Iterable<DataSnapshot> playersIterable = data.getChildren();
                    Iterator<DataSnapshot> playersIterator = playersIterable.iterator();

                    while (playersIterator.hasNext()) {
                        list.add(playersIterator.next().getValue().toString());
                    }

                    ++index;
                    ++teamIndex;

                    if (teamIndex == dataSnapshot.getChildrenCount()) {

                        listViewCustomAdapter = new ListViewCustomAdapter(playersList, getApplicationContext(), R.layout.card_view_layout);

                        listView.setAdapter(listViewCustomAdapter);

                        databaseReference.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void proceedBtnOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), InGameActivity.class);
        intent.putExtra("game_room", game_room);
        startActivity(intent);
    }
}
