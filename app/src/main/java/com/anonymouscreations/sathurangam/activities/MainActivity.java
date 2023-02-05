package com.anonymouscreations.sathurangam.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.database.MyDatabase;
import com.anonymouscreations.sathurangam.support.Fdn;
import com.anonymouscreations.sathurangam.support.Mapping;
import com.anonymouscreations.sathurangam.support.Rules;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ImageView a[][];
    LinearLayout llBoard;
    TextView btnSwitch, btnLeft, btnRight, btnUp, btnDown, btnSend;
    Drawable coins[];
    Mapping mapping;
    Fdn fdn;
    Rules rules;
    MyDatabase myDatabase;
    int curPos = -1, row = 8, col = 8, manualControl[] = new int[4];
    long hold;
    String coinMap, controlString = "0";
    char user = 'w';


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

        coinMap = mapping.coinMap;
        a = mapping.mapPosition(llBoard);
        coins = mapping.mapCoins();

        mapping.arrange(fdn);
        myDatabase.updateFdnString(fdn.getFdn());
        reset();
        clickCoin();
        controls();

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                manualControl = new int[4];
                btnSend.setText("Send");
                controlString="0";
//                if(btnSwitch.getText().equals("White")) {
//                    btnSwitch.setText("Black");
//                    user = 'b';
//                }
//                else {
//                    btnSwitch.setText("White");
//                    user = 'w';
//                }
//                fdn = new StringBuilder(fdn.split(" ")[0]).reverse().toString()+" "+fdn.split(" ")[1];
//                arrange();
            }
        });

    }

    String currentCoin(int t){
        char row[][] = fdn.briefFDN();
        char coin = row[t/10][t%10];
        if(coinMap.contains(String.valueOf(coin)) &&
            ((Character.isUpperCase(coin) && fdn.getFdn().split(" ")[1].equals("w")) ||
            (Character.isLowerCase(coin) && fdn.getFdn().split(" ")[1].equals("b"))))
            return String.valueOf(coin);
        return "1";
    }

    void moveCoin(int p) {

        char[][] row = fdn.briefFDN();
        reset();
        row[p/10][p%10] = row[curPos/10][curPos%10];
        row[curPos/10][curPos%10] = '1';
        if(fdn.getFdn().split(" ")[1].equals("b"))
            fdn.setFdn(fdn.getFdn().split(" ")[0]+" "+"w");
        else
            fdn.setFdn(fdn.getFdn().split(" ")[0]+" "+"b");
        fdn.mergeFDN(row);
        mapping.arrange(fdn);
        a[curPos/10][curPos%10].setBackground(getResources().getDrawable(((curPos/10)+(curPos%10))%2==0 ? R.drawable.square_border_green : R.drawable.square_border));
        a[p/10][p%10].setBackground(getResources().getDrawable(((p/10)+(p%10))%2==0 ? R.drawable.square_border_green : R.drawable.square_border));
        curPos = -1;
        myDatabase.updateFdnString(fdn.getFdn());
    }

    boolean checkPosition(int t){
        if(currentCoin(t)=="1")
            return false;
        return true;
    }

    void clickCoin(){
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                ImageView temp = a[i][j];
                int tempI = (i*10)+j;
                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String curCoin = currentCoin(tempI);
                        if(!checkPosition(tempI)&&curPos==-1) {
                            return;
                        }else if(curPos == -1 ||
                                curPos == tempI ||
                                Character.isLowerCase(curCoin.charAt(0)) &&
                                        fdn.getFdn().split(" ")[1].equals("b") ||
                                Character.isUpperCase(curCoin.charAt(0)) &&
                                        fdn.getFdn().split(" ")[1].equals("w")) {
                            reset();
                            curPos = tempI;
                            possiblePath(rules.check(fdn,currentCoin(tempI).charAt(0),curPos));
                            temp.setBackgroundColor(getResources().getColor((tempI % 10 + tempI / 10) % 2 == 0 ? R.color.greenHigh : R.color.grey));
                        }else if(validMove(tempI))
                            moveCoin(tempI);
                    }
                });
            }
        }
    }

    void reset(){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                a[i][j].setBackgroundColor(getResources().getColor((i+j)%2==0 ? R.color.green : R.color.white));
            }
        }
    }

    void possiblePath(ArrayList<Integer> pos){
        for(int i=0;i<pos.size();i++){
            int x = pos.get(i)/10, y = pos.get(i)%10;
            a[x][y].setBackground(getResources().getDrawable((x + y) % 2 == 0 ? R.drawable.possible_move_green : R.drawable.possible_move));
        }
        captureAlert(pos);
    }

    void captureAlert(ArrayList<Integer> pos){
        char row[][] = fdn.briefFDN();
        for(int i=0;i<pos.size();i++){
            char temp = row[pos.get(i)/10][pos.get(i)%10];
            int x = pos.get(i)/10, y = pos.get(i)%10;
            if(fdn.getFdn().split(" ")[1].equals("b")&&Character.isUpperCase(temp)) {
                a[x][y].setBackground(getResources().getDrawable((x + y) % 2 == 0 ? R.drawable.captured_square_green : R.drawable.captured_square));
            }
            else if(fdn.getFdn().split(" ")[1].equals("w")&&Character.isLowerCase(temp))
                a[x][y].setBackground(getResources().getDrawable((x + y) % 2 == 0 ? R.drawable.captured_square_green : R.drawable.captured_square));
        }
    }

    boolean validMove(int temp){
        ArrayList<Integer> rul = rules.check(fdn,currentCoin(curPos).charAt(0),curPos);
        String com = "";
        for(int i=0;i< rul.size();i++)
            com+=rul.get(i)+"_";
        if(com.contains(String.valueOf(temp)))
            return true;
        return false;
    }

    // === Manual control button handling
    void controls(){
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
}