package com.example.ivgeorgiev.lis4ita;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InGameActivity extends AppCompatActivity {

    HorizontalInfiniteCycleViewPager viewPager;
    ViewAdapter viewAdapter;
    DatabaseReference databaseReference;
    List<String> listWords;
    int item_position=0;
    Button inGameNextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        databaseReference=FirebaseDatabase.getInstance().getReference().child("words");
        listWords=new ArrayList<>();
        inGameNextBtn=findViewById(R.id.inGameNextBtn);

        databaseConnection();
    }

    private void databaseConnection(){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    listWords.add(data.getValue().toString());
                }

                setViewPager();

                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setViewPager(){
        viewPager=findViewById(R.id.horizontalViewPager);

        viewAdapter=new ViewAdapter(listWords,getBaseContext());
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

        if(item_position<listWords.size()-1){

            ++item_position;
            viewPager.setCurrentItem(item_position);
        }
    }
}
