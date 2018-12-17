package com.example.ivgeorgiev.lis4ita;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeamMembersActivity extends AppCompatActivity {

    ListView listView;
    ListViewCustomAdapter listViewCustomAdapter;

    ArrayList<String> playersList;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_members);

        init();

        databaseConnection();
    }

    private void init(){
        listView=findViewById(R.id.playersListView);
        playersList=new ArrayList<>();
    }

    private void databaseConnection(){

        databaseReference=FirebaseDatabase.getInstance().getReference().child("game_room");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){

                    playersList.add(data.child("nick_name").getValue().toString());

                    listViewCustomAdapter=new ListViewCustomAdapter(playersList, getApplicationContext(), R.layout.card_view_layout);

                    listView.setAdapter(listViewCustomAdapter);

                    databaseReference.removeEventListener(this);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
