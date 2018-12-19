package com.example.ivgeorgiev.lis4ita.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivgeorgiev.lis4ita.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button homeStartButton, homeHoHButton;
    TextView userText;
    EditText createGameRoomET, enterGameRoomET;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference, gameRoomDBRef;

    String user_id;
    String user_name = " ";

    Dialog dialogCreateGR, dialogEnterGR;

    private boolean createRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        gameRoomDBRef = FirebaseDatabase.getInstance().getReference().child("game_room");

        init();

        setUpDialogWindow();

        setUpDialogWindow();

        greetUser();
    }

    private void init() {
        homeStartButton = findViewById(R.id.homeStartBtn);
        homeHoHButton = findViewById(R.id.homeHallOfFameBtn);
        userText = findViewById(R.id.mainUserTextView);
    }

    private void setUpDialogWindow() {
        //CREATE GAME ROOM WINDOW
        dialogCreateGR = new Dialog(MainActivity.this);
        dialogCreateGR.setContentView(R.layout.dialog_create_game_room_layout);
        dialogCreateGR.setTitle("Create Game Room");


        createGameRoomET = dialogCreateGR.findViewById(R.id.createGameRoomET);

        //ENTER GAME ROOM WINDOW
        dialogEnterGR = new Dialog(MainActivity.this);
        dialogEnterGR.setContentView(R.layout.dialog_enter_game_room_layout);
        dialogEnterGR.setTitle("Enter Game Room");

        enterGameRoomET = dialogEnterGR.findViewById(R.id.enterGameRoomET);
    }

    private void greetUser() {
        user_id = mAuth.getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals(user_id)) {

                        user_name = data.child("nick_name").getValue().toString();
                        userText.setText(user_name);
                    }
                }
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void homeGameRoomOnClick(View view) {
        dialogCreateGR.show();
    }

    public void homeHallOfFameOnClick(View view) {

    }

    public void createEnterGameRoomBtn(View view) {
        dialogCreateGR.dismiss();
        dialogEnterGR.show();
    }

    public void createGameRoomBtn(View view) {
        if (createGameRoomET.getText() != null) {
            final String gameRoomName = createGameRoomET.getText().toString();

            createRoom=true;

            //SEARCH FOR DUPLICATED GAME ROOM
            gameRoomDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot gameRoom : dataSnapshot.getChildren()) {

                        if (gameRoomName.contentEquals(gameRoom.getKey()) && !user_name.contentEquals(" ")) {
                            createRoom=false;
                            break;
                        }
                    }

                    //CREATE GAME ROOM IF THERE IS NO OTHER ONE WITH THE SAME NAME
                    if(createRoom){

                        databaseReference.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot user : dataSnapshot.getChildren()) {

                                    if (user.getKey().equals(user_id)) {

                                        gameRoomDBRef.child(gameRoomName).child("players").child(user_id).child("nick_name").setValue(user.child("nick_name").getValue());
                                        gameRoomDBRef.child(gameRoomName).child("admin").setValue(user_id);
                                    }
                                }

                                databaseReference.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Intent i = new Intent(MainActivity.this, FetchUsers.class);
                        i.putExtra("game_room", gameRoomName);
                        startActivity(i);

                        dialogCreateGR.dismiss();
                    }else{
                        Toast.makeText(MainActivity.this, "Game Room with this name already exist!", Toast.LENGTH_SHORT).show();
                    }

                    gameRoomDBRef.removeEventListener(this);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void enterGameRoomBtn(View view) {
        if (enterGameRoomET.getText() != null) {
            final String gameRoomName = enterGameRoomET.getText().toString();

            gameRoomDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot gameRoom : dataSnapshot.getChildren()) {

                        if (gameRoomName.contentEquals(gameRoom.getKey()) && !user_name.contentEquals(" ")) {

                            gameRoomDBRef.child(gameRoomName).child("players").child(user_id).child("nick_name").setValue(user_name);

                            gameRoomDBRef.removeEventListener(this);

                            Intent i = new Intent(MainActivity.this, FetchUsers.class);
                            i.putExtra("game_room", gameRoomName);
                            startActivity(i);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
