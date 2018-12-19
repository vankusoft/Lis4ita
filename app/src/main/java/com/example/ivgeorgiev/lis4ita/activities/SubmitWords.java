package com.example.ivgeorgiev.lis4ita.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ivgeorgiev.lis4ita.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class SubmitWords extends AppCompatActivity {

    Button wordsContinueButton;

    EditText wordsSubmitET;
    TextView wordsCounter;

    DatabaseReference database, db;

    String wordsCount = "";
    int countWords;

    Random random;

    String game_room = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_words);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            game_room = extras.getString("game_room");
        }

        init();

        database = FirebaseDatabase.getInstance().getReference().child("game_room").child(game_room).child("game_settings");
        db = FirebaseDatabase.getInstance().getReference();

        databaseConnection();

        random = new Random();
    }

    private void init() {
        wordsSubmitET = findViewById(R.id.wordSubmitET);
        wordsCounter = findViewById(R.id.wordsCounter);

        wordsContinueButton = findViewById(R.id.wordsContinueButton);
    }

    private void databaseConnection() {

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                wordsCount = dataSnapshot.child("words_number").getValue().toString();

                wordsCounter.setText(wordsCount);

                countWords = Integer.parseInt(wordsCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void wordsSubmitBtnOnClick(View view) {

        if (countWords > 0) {
            int rann = random.nextInt(5000000);
            String randomString = String.valueOf(rann);

            --countWords;
            database.child("words_number").setValue(countWords);

            String word = wordsSubmitET.getText().toString();
            db.child("game_room").child(game_room).child("words").child(randomString).setValue(word);

            wordsSubmitET.setText(" ");
        }

        if (countWords == 0) {
            wordsContinueButton.setEnabled(true);
        }
    }

    public void wordsContinueBtnOnClick(View view) {
        Intent intent = new Intent(SubmitWords.this, TeamMembersActivity.class);
        intent.putExtra("game_room", game_room);
        startActivity(intent);
    }
}
