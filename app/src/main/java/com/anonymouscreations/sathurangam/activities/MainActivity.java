package com.anonymouscreations.sathurangam.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.database.MyDatabase;
import com.anonymouscreations.sathurangam.chess.Fdn;
import com.anonymouscreations.sathurangam.chess.Mapping;
import com.anonymouscreations.sathurangam.chess.Move;
import com.anonymouscreations.sathurangam.chess.Rules;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Mapping xml elements to java objects
        btnSwitch = findViewById(R.id.btnSwitch);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnSend = findViewById(R.id.btnSend);
        llBoard = findViewById(R.id.llBoard);

        // --- Instantiating user defined classes with context as the constructor parameter
        mapping = new Mapping(getApplicationContext());
        fdn = new Fdn("rnbkqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR w");
        myDatabase = new MyDatabase(getApplicationContext(),"Automated Chess board");
        rules = new Rules();
        move = new Move(getApplicationContext(),myDatabase);

        // --- Setting initial states of the chess board
        mapping.mapPosition(llBoard);
        mapping.mapCoins();
        mapping.arrange(fdn);
        mapping.reset();
        myDatabase.updateFdnString(fdn.getFdn());
//        myDatabase.pullData(mapping,fdn);

        // --- Handling all button clicks
        handleClicks();

        // --- Handling sound
        MediaPlayer.create(this,R.raw.start).start();

    }

    // === Identifying the coin in the clicked position
    String currentCoin(int t){
        char row[][] = fdn.briefFDN();
        char coin = row[t/10][t%10];
        if(mapping.coinMap.contains(String.valueOf(coin)) &&
            ((Character.isUpperCase(coin) && fdn.getFdn().split(" ")[1].equals("w")) ||
            (Character.isLowerCase(coin) && fdn.getFdn().split(" ")[1].equals("b"))))
            return String.valueOf(coin);
        return "1";
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
                mapping.reset();
                manualControl = new int[4];
                btnSend.setText("Send");
                controlString="0";
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
                    }
                });
            }
        }
    }
}