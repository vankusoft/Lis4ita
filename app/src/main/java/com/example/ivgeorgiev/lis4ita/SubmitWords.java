package com.example.ivgeorgiev.lis4ita;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    DatabaseReference database,db;

    String wordsCount="";
    int countWords;

    Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_words);

        init();

        database=FirebaseDatabase.getInstance().getReference().child("game_settings");
        db=FirebaseDatabase.getInstance().getReference();

        databaseConnection();

        random= new Random();
    }

    private void init(){
        wordsSubmitET=findViewById(R.id.wordSubmitET);
        wordsCounter=findViewById(R.id.wordsCounter);

        wordsContinueButton=findViewById(R.id.wordsContinueButton);
    }

    private void databaseConnection(){

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                wordsCount=dataSnapshot.child("words_number").getValue().toString();

                wordsCounter.setText(wordsCount);

                countWords=Integer.parseInt(wordsCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void wordsSubmitBtnOnClick(View view) {

        if(countWords>0){
            int rann=random.nextInt(5000000);
            String randomString=String.valueOf(rann);

            --countWords;
            database.child("words_number").setValue(countWords);

            String word=wordsSubmitET.getText().toString();
            db.child("words").child(randomString).setValue(word);

            wordsSubmitET.setText(" ");
        }

        if (countWords==0){
            wordsContinueButton.setEnabled(true);
        }
    }

    public void wordsContinueBtnOnClick(View view) {
        Intent intent=new Intent(SubmitWords.this, TeamMembersActivity.class);
        startActivity(intent);
    }
}
