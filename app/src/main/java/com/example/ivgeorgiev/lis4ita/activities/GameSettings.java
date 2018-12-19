package com.example.ivgeorgiev.lis4ita.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.ivgeorgiev.lis4ita.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameSettings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner wordsSpinner, teamsSpinner ,playersSpinner;

    ArrayAdapter<CharSequence> wordsAdapter, teamsAdapter, playersAdapter;

    DatabaseReference databaseReference;

    String game_room=" ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            game_room = extras.getString("game_room");
        }

        if(!game_room.contentEquals(" ")){
            databaseReference=FirebaseDatabase.getInstance().getReference().child("game_room").child(game_room).child("game_settings");
        }

        init();
    }

    private void init(){
        //WORDS DROP DOWN
        wordsSpinner=findViewById(R.id.wordsSpinner);

        wordsAdapter=ArrayAdapter.createFromResource(this,R.array.word_number, android.R.layout.simple_spinner_item);
        wordsAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        wordsSpinner.setAdapter(wordsAdapter);
        wordsSpinner.setOnItemSelectedListener(this);

        //TEAMS DROP DOWN
        teamsSpinner=findViewById(R.id.teamsSpinner);

        teamsAdapter=ArrayAdapter.createFromResource(this,R.array.teams_number, android.R.layout.simple_spinner_item);
        teamsAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        teamsSpinner.setAdapter(teamsAdapter);
        teamsSpinner.setOnItemSelectedListener(this);

        //PLAYERS DROP DOWN
        playersSpinner=findViewById(R.id.playersSpinner);

        playersAdapter=ArrayAdapter.createFromResource(this,R.array.players_number, android.R.layout.simple_spinner_item);
        playersAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        playersSpinner.setAdapter(playersAdapter);
        playersSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getId() == R.id.wordsSpinner){

            String words= parent.getItemAtPosition(position).toString();
            int words_number=Integer.parseInt(words);

            databaseReference.child("words_number").setValue(words_number);

        }else if(parent.getId() == R.id.teamsSpinner){

            String teams= parent.getItemAtPosition(position).toString();
            int teams_number=Integer.parseInt(teams);

            databaseReference.child("teams_number").setValue(teams_number);

        }else if(parent.getId() == R.id.playersSpinner){

            String players= parent.getItemAtPosition(position).toString();
            int players_number=Integer.parseInt(players);

            databaseReference.child("players_number").setValue(players_number);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void applySettingsBtn(View view) {
        Intent intent=new Intent(GameSettings.this,FetchUsers.class);
        intent.putExtra("game_room",game_room);
        intent.putExtra("start_game","true");
        startActivity(intent);
    }
}
