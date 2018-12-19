package com.example.ivgeorgiev.lis4ita.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
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

import java.util.ArrayList;

import static com.example.ivgeorgiev.lis4ita.adaptors.MyResultReceiver.*;

public class InGameActivity extends AppCompatActivity implements Receiver {

    HorizontalInfiniteCycleViewPager viewPager;
    ViewAdapter viewAdapter;

    String game_room = " ";
    int item_position = 0;

    Button inGameNextBtn;
    ProgressDialog dialog;

    ArrayList<String> list;

    MyResultReceiver myResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            game_room = extras.getString("game_room");
        }

        inGameNextBtn = findViewById(R.id.inGameNextBtn);
        viewPager = findViewById(R.id.horizontalViewPager);

        myResultReceiver = new MyResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);

        dialog = ProgressDialog.show(InGameActivity.this, "Loading", "Please wait...", true);

        Intent intentService = new Intent(InGameActivity.this, InGameBackgroundService.class);
        intentService.putExtra("game_room", game_room);
        intentService.putExtra("ResultTag", myResultReceiver);
        startService(intentService);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == 0 && resultData.getStringArrayList("list") != null) {

            list = resultData.getStringArrayList("list");

            setViewPager();
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

        dialog.dismiss();
    }

    public void inGameNextBtn(View view) {

        if (list != null) {
            if (item_position < list.size() - 1) {

                ++item_position;
                viewPager.setCurrentItem(item_position);
            } else {
                Toast.makeText(InGameActivity.this, "No more words!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
