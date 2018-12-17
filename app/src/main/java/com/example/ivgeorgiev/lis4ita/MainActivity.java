package com.example.ivgeorgiev.lis4ita;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button homeStartButton, homeHoHButton;
    TextView userText;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference, gameRoomDBRef;

    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");
        gameRoomDBRef=FirebaseDatabase.getInstance().getReference().child("game_room");

        init();

        greetUser();
    }

    private void init(){
        homeStartButton=findViewById(R.id.homeStartBtn);
        homeHoHButton=findViewById(R.id.homeHallOfFameBtn);
        userText=findViewById(R.id.mainUserTextView);
    }

    private void greetUser(){
        user_id =mAuth.getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot data:dataSnapshot.getChildren()){
                    if(data.getKey().equals(user_id)){

                        userText.setText(data.child("nick_name").getValue().toString());
                    }
                }

                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void homeStartOnClick(View view) {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot user :dataSnapshot.getChildren()){

                    if(user.getKey().equals(user_id)){

                        gameRoomDBRef.child(user_id).child("nick_name").setValue(user.child("nick_name").getValue());
                    }
                }

                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


       Intent i = new Intent(MainActivity.this,FetchUsers.class);
       startActivity(i);
    }

    public void homeHallOfFameOnClick(View view) {

    }
}
