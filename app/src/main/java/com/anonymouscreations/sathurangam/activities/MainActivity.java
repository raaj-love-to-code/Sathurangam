package com.anonymouscreations.sathurangam.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.database.MyDatabase;
import com.anonymouscreations.sathurangam.chess.Fdn;
import com.anonymouscreations.sathurangam.chess.Mapping;
import com.anonymouscreations.sathurangam.chess.Move;
import com.anonymouscreations.sathurangam.chess.Rules;
import com.anonymouscreations.sathurangam.popup.GameOverPopup;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LinearLayout llBoard;
    TextView btnSwitch, btnLeft, btnRight, btnUp, btnDown, btnSend;
    Mapping mapping;
    Fdn fdn;
    Rules rules;
    Move move;
    MyDatabase myDatabase;
    int row = 8, col = 8, manualControl[] = new int[4];
    public int curPos = -1;
    String controlString = "0";
    public static boolean gameOver;
    public static int duration, halfMove;
    public static char userColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Code to hide the support action bar
        getSupportActionBar().hide();

        // --- Mapping xml elements to java objects
        btnSwitch = findViewById(R.id.btnSwitch);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnSend = findViewById(R.id.btnSend);
        llBoard = findViewById(R.id.llBoard);

        // --- Initializing variables
        gameOver = false;
        duration = halfMove = 0;
        userColor = 'w';

        // --- Instantiating user defined classes with context as the constructor parameter
        mapping = new Mapping(getApplicationContext());
        fdn = new Fdn("rnbkqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR w 1");
        myDatabase = new MyDatabase(getApplicationContext(),"Automated Chess board", this);
        rules = new Rules();
        move = new Move(getApplicationContext(),myDatabase);

        // --- Setting initial states of the chess board
        mapping.mapPosition(llBoard);
        mapping.mapCoins();
        mapping.arrange(fdn);
        mapping.reset(fdn);
        myDatabase.updateFdnString(fdn.getFdn());
//        myDatabase.pullData(mapping,fdn);

        // --- Start duration for match
        startDuration();

        // --- Handling all button clicks
        handleClicks();

        // --- Handling sound
        MediaPlayer.create(this,R.raw.start).start();

    }

    // === Manual Control value processing
    void addControl(char z){

        controlString = "";

        if(z=='u')
            manualControl[0]++;
        else if(z=='d')
            manualControl[2]++;
        else if(z=='l')
            manualControl[3]++;
        else if(z=='r')
            manualControl[1]++;

        if(manualControl[0]!=0)
            controlString+=("u"+manualControl[0]);
        if(manualControl[1]!=0)
            controlString+=("r"+manualControl[1]);
        if(manualControl[2]!=0)
            controlString+=("d"+manualControl[2]);
        if(manualControl[3]!=0)
            controlString+=("l"+manualControl[3]);

        btnSend.setText(controlString);
    }

    // === Function to handle button clicks
    void handleClicks(){
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapping.reset(fdn);
                manualControl = new int[4];
                btnSend.setText("Send");
                controlString="0";
                if(llBoard.getRotation() == 180)
                    llBoard.setRotation(0);
                else
                    llBoard.setRotation(180);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDatabase.updateControlString(controlString);
            }
        });
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addControl('u');
            }
        });
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addControl('d');
            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addControl('l');
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addControl('r');
            }
        });

        // --- Handling clicks of 64 boxes (Imageview)
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                ImageView temp = mapping.a[i][j];
                int tempI = (i*10)+j;
                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        curPos = move.clickCoin(mapping,rules,fdn,temp,tempI,curPos);

                        // --- Validating for game over
                        if(curPos == -2) {
                            new GameOverPopup(view, MainActivity.this).gameOver(fdn);
                            gameOver = true;
                            curPos = -1;
                        }
                    }
                });
            }
        }
    }

    // --- Countdown to tract the duration of the match
    void startDuration(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(!gameOver) {
                    duration++;
                    handler.postDelayed(this,1000);
                }

                Log.e("Time",duration+"");
            }
        });
    }

    // --- Stopping the timer function while exiting the application
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gameOver = true;
        curPos = -1;
        finish();
    }
}