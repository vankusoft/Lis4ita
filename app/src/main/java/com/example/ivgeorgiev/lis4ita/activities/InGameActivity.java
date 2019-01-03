package com.example.ivgeorgiev.lis4ita.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ivgeorgiev.lis4ita.R;
import com.example.ivgeorgiev.lis4ita.adaptors.MyResultReceiver;
import com.example.ivgeorgiev.lis4ita.adaptors.ViewAdapter;
import com.example.ivgeorgiev.lis4ita.backgroundServices.InGameBackgroundService;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.ivgeorgiev.lis4ita.adaptors.MyResultReceiver.*;

public class InGameActivity extends AppCompatActivity implements Receiver {

    HorizontalInfiniteCycleViewPager viewPager;
    ViewAdapter viewAdapter;

    Button inGameNextBtn;
    ProgressDialog dialog;

    ArrayList<String> list;

    ArrayList<DataSnapshot> teamsList;
    ArrayList<String> temporaryKeys;

    MyResultReceiver myResultReceiver;

    DatabaseReference databaseReference;
    FirebaseAuth auth;

    int team_index = 0;
    int item_position = 0;

    String game_room = " ";
    String teamMemberKey;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            game_room = extras.getString("game_room");
        }

        init();

        //DATABASE REFERENCE TO THE SPECIFIC GAME ROOM
        databaseReference = FirebaseDatabase.getInstance().getReference().child("game_room").child(game_room);
        databaseReference.child("word_index").setValue(0);
        auth = FirebaseAuth.getInstance();

        dialog = ProgressDialog.show(InGameActivity.this, "Loading", "Please wait...", true);

        startIntentService();
    }

    private void init() {
        inGameNextBtn = findViewById(R.id.inGameNextBtn);
        viewPager = findViewById(R.id.horizontalViewPager);

        myResultReceiver = new MyResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);

        teamsList = new ArrayList<>();
        temporaryKeys = new ArrayList<>();
    }

    //TEAM ORDERING BEGINS FROM HERE
    private void getAllTeams() {

        final DatabaseReference teams = databaseReference.child("teams");

        //GET ALL TEAMS
        teams.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot teamsData : dataSnapshot.getChildren()) {
                    teamsList.add(teamsData);
                }

                teamOrder();

                teams.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void teamOrder() {

        int childTeamIndex = team_index + 1;

        if (teamsList.size() != 0) {
            //GET TEAMS FROM THE LIST
            DataSnapshot teamObject = teamsList.get(team_index);
            //ITERATING THROUGH TEAM MEMBERS INSIDE THE TEAM
            Iterable<DataSnapshot> iterateTeam = teamObject.getChildren();

            if (iterateTeam.iterator().hasNext()) {

                DataSnapshot teamMember = iterateTeam.iterator().next();

                teamMemberKey = teamMember.getKey();
                user_id= teamMember.child("id").getValue().toString();

                temporaryKeys.add(teamMemberKey);

                if ((teamMember.child("already_talked").getValue().toString()).contentEquals("false")
                        && (teamMember.child("is_talking").getValue().toString()).contentEquals("false")) {

                    databaseReference.child("teams").child("team" + childTeamIndex).child(teamMemberKey).child("already_talked").setValue("true");
                    databaseReference.child("teams").child("team" + childTeamIndex).child(teamMemberKey).child("is_talking").setValue("true");

                    if (childTeamIndex > 1) {
                        databaseReference.child("teams").child("team" + team_index).child(temporaryKeys.get(team_index - 1)).child("is_talking").setValue("false");
                    }
                }
            }
        }

        isSliderAvailable();
    }

    private void isSliderAvailable() {

        String id = auth.getCurrentUser().getUid();

        if (user_id.contentEquals(id)) {
            inGameNextBtn.setEnabled(false);
            setViewPager();
            sliderChanging();
        }

        dialog.dismiss();
    }

    private void startIntentService() {
        Intent intentService = new Intent(InGameActivity.this, InGameBackgroundService.class);
        intentService.putExtra("game_room", game_room);
        intentService.putExtra("ResultTag", myResultReceiver);
        startService(intentService);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == 0 && resultData.getStringArrayList("list") != null) {
            list = resultData.getStringArrayList("list");
            //setViewPager();
            getAllTeams();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setViewPager() {
        viewAdapter = new ViewAdapter(list, getBaseContext());
        viewPager.setAdapter(viewAdapter);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        viewAdapter.notifyDataSetChanged();
    }

    public void inGameNextBtn(View view) {

        if (list != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String word_index = dataSnapshot.child("word_index").getValue().toString();
                    int index_word = Integer.parseInt(word_index);

                    if (index_word < list.size() - 1) {

                        ++item_position;
                        databaseReference.child("word_index").setValue(item_position);
                        viewPager.setCurrentItem(item_position);

                    } else {
                        Toast.makeText(InGameActivity.this, "No more words!", Toast.LENGTH_SHORT).show();
                    }

                    databaseReference.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void sliderChanging(){

        databaseReference.child("word_index").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewPager.setCurrentItem(item_position);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void passWordBtn(View view) {
    }
}
