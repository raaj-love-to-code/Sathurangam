package com.anonymouscreations.sathurangam;

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
    GridLayout gl;
    TextView btnSwitch, btnLeft, btnRight, btnUp, btnDown, btnSend;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Automated Chess board");
    int curPos = -1, row = 8, col = 8, manualControl[] = new int[4];
    long hold;
    Drawable coins[];
    String fdn = "rnbkqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR w", coinMap = "prnbkqPRNBKQ", controlString = "0";
    char user = 'w';


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSwitch = findViewById(R.id.btnSwitch);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnSend = findViewById(R.id.btnSend);

        mapPosition();
        mapCoins();
        arrange();
        reset();
        clickCoin();
        controls();
        updateFirebaseFdn();

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

    void arrange(){
        System.out.println(fdn);
        char row[][] = briefFDN();
        for(int i=0;i<this.row;i++){
            for(int j=0;j<col;j++){
                if(row[i][j]=='1')
                    a[i][j].setImageResource(0);
                else
                    a[i][j].setImageDrawable(coins[coinMap.indexOf(String.valueOf(row[i][j]))]);
            }
        }
    }

    char[][] briefFDN(){
        String[] out = fdn.split(" ")[0].split("/");
        char[][] o1 = new char[8][8];
        for(int i=0;i<8;i++) {
            int x = 0;
            for (int j = 0; j < out[i].length(); j++) {
                if (Character.isDigit(out[i].charAt(j))) {
                    int temp = Integer.parseInt(String.valueOf(out[i].charAt(j)));
                    for (int k = 0; k < temp; k++)
                        o1[i][x++] = '1';
                } else
                    o1[i][x++] = out[i].charAt(j);
            }
        }
        return o1;
    }

    void mergeFDN(char[][] r){
        String s = "";
        for(int i=0;i<8;i++){
            int x = 0;
            for(int j=0;j<8;j++) {
                if(r[i][j]!='1'&&x>0) {
                    s += String.valueOf(x);
                    x = 0;
                }
                if(i>1&&i<6&&r[i][j]=='1') {
                    x++;
                    if(j==7)
                        s+=String.valueOf(x);
                }else
                    s += r[i][j];
            }
            s+=i==7 ? ' ' : '/';
        }
        fdn = s+fdn.split(" ")[1];
    }

    String currentCoin(int t){
        char row[][] = briefFDN();
        char coin = row[t/10][t%10];
        if(coinMap.contains(String.valueOf(coin))&&((Character.isUpperCase(coin)&&fdn.split(" ")[1].equals("w"))||(Character.isLowerCase(coin)&&fdn.split(" ")[1].equals("b"))))
            return String.valueOf(coin);
        return "1";
    }

    void moveCoin(int p) {

        char[][] row = briefFDN();
        reset();
        row[p/10][p%10] = row[curPos/10][curPos%10];
        row[curPos/10][curPos%10] = '1';
        if(fdn.split(" ")[1].equals("b"))
            fdn = fdn.split(" ")[0]+" "+"w";
        else
            fdn = fdn.split(" ")[0]+" "+"b";
        mergeFDN(row);
        arrange();
        a[curPos/10][curPos%10].setBackground(getResources().getDrawable(((curPos/10)+(curPos%10))%2==0 ? R.drawable.square_border_green : R.drawable.square_border));
        a[p/10][p%10].setBackground(getResources().getDrawable(((p/10)+(p%10))%2==0 ? R.drawable.square_border_green : R.drawable.square_border));
        curPos = -1;
        updateFirebaseFdn();
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
                        }else if(curPos==-1||curPos==tempI||Character.isLowerCase(curCoin.charAt(0))&&fdn.split(" ")[1].equals("b")||Character.isUpperCase(curCoin.charAt(0))&&fdn.split(" ")[1].equals("w")) {
                            reset();
                            curPos = tempI;
                            possiblePath(rules(currentCoin(tempI).charAt(0)));
                            temp.setBackgroundColor(getResources().getColor((tempI % 10 + tempI / 10) % 2 == 0 ? R.color.greenHigh : R.color.grey));
                        }else if(validMove(tempI))
                            moveCoin(tempI);
                    }
                });
            }
        }
    }

    void mapCoins(){
        coins = new Drawable[12];
        coins[0] = getResources().getDrawable(R.drawable.bp);
        coins[1] = getResources().getDrawable(R.drawable.br);
        coins[2] = getResources().getDrawable(R.drawable.bn);
        coins[3] = getResources().getDrawable(R.drawable.bb);
        coins[4] = getResources().getDrawable(R.drawable.bk);
        coins[5] = getResources().getDrawable(R.drawable.bq);
        coins[6] = getResources().getDrawable(R.drawable.wp);
        coins[7] = getResources().getDrawable(R.drawable.wr);
        coins[8] = getResources().getDrawable(R.drawable.wn);
        coins[9] = getResources().getDrawable(R.drawable.wb);
        coins[10] = getResources().getDrawable(R.drawable.wk);
        coins[11] = getResources().getDrawable(R.drawable.wq);
    }

    void mapPosition(){
        a = new ImageView[8][8];
        llBoard = findViewById(R.id.llBoard);
        for(int i=0;i<row;i++){
            gl = (GridLayout)llBoard.getChildAt(i);
            for(int j=0;j<col;j++)
                a[i][j] = (ImageView)gl.getChildAt(j);
        }
    }

    void reset(){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                a[i][j].setBackgroundColor(getResources().getColor((i+j)%2==0 ? R.color.green : R.color.white));
            }
        }
    }

    ArrayList<Integer> rules(char uc){
        int x = curPos/10, y = curPos%10;
        char c = Character.toLowerCase(uc);
        ArrayList<Integer> pos = new ArrayList<Integer>();
        char[][] row = briefFDN();

        // ======== Pawn ==================== //

        if(c=='p'){

            //  --- Check for pawn in last position
            if((Character.isUpperCase(uc) && x==0) || (Character.isLowerCase(uc) && x==7))
                return pos;

            //  --- Determining the direction for movement
            int s = 1;
            if(Character.isUpperCase(uc))
                s = -1;

            //  --- Position for one step move
            if(row[(x + (1*s))][y]=='1')
                pos.add(((x + (1*s)) * 10) + y);

            //  --- Position for second step move
            if (pos.size() > 0 && ((x == 6 && Character.isUpperCase(uc)) || (x == 1 && Character.isLowerCase(uc))))
                pos.add(((x + (2*s)) * 10) + y);

            //  --- Side position for capturing coin
            int a = x+(1*s);
            if(y+1<=7 && row[a][y+1]!='1' && ( Character.isUpperCase(uc)  && (Character.isLowerCase(row[a][y+1])) || (Character.isLowerCase(uc)  && Character.isUpperCase(row[x+(1*s)][y+1])))){
                pos.add((a * 10) + y + 1);
            }
            if(y-1>=0 && row[a][y-1]!='1' && ( Character.isUpperCase(uc) && (Character.isLowerCase(row[a][y-1])) || (Character.isLowerCase(uc)  && Character.isUpperCase(row[x+(1*s)][y-1])))){
                pos.add((a * 10) + y - 1);
            }
        }

        // ======== knight ==================== //

        else if(c=='n'){
            int temp[] = new int[]{1,-1,2,-2};
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    if(temp[i]==temp[j]||-temp[i]==temp[j])
                        continue;
                    if(x+temp[i]<8&&y+temp[j]<8&&x+temp[i]>-1&&y+temp[j]>-1)
                        pos.add(((x+temp[i])*10)+y+temp[j]);
                }
            }
        }

        // ================== bishop ===================== //

        else if(c=='b'){
//        ========================= Type here ======================= //
        }

        // ========== verifying the same side coins ============= //

        System.out.println("s"+pos);
        for(int i=0;i<pos.size();i++){
            char temp = row[pos.get(i)/10][pos.get(i)%10];
            if(fdn.split(" ")[1].equals("w")&&Character.isUpperCase(temp)) {
                pos.remove(i--);
            }
            else if(fdn.split(" ")[1].equals("b")&&Character.isLowerCase(temp))
                pos.remove(i--);
        }
        return pos;
    }

    void possiblePath(ArrayList<Integer> pos){
        for(int i=0;i<pos.size();i++){
            int x = pos.get(i)/10, y = pos.get(i)%10;
            a[x][y].setBackground(getResources().getDrawable((x + y) % 2 == 0 ? R.drawable.possible_move_green : R.drawable.possible_move));
        }
        captureAlert(pos);
    }

    void captureAlert(ArrayList<Integer> pos){
        char row[][] = briefFDN();
        for(int i=0;i<pos.size();i++){
            char temp = row[pos.get(i)/10][pos.get(i)%10];
            int x = pos.get(i)/10, y = pos.get(i)%10;
            if(fdn.split(" ")[1].equals("b")&&Character.isUpperCase(temp)) {
                a[x][y].setBackground(getResources().getDrawable((x + y) % 2 == 0 ? R.drawable.captured_square_green : R.drawable.captured_square));
            }
            else if(fdn.split(" ")[1].equals("w")&&Character.isLowerCase(temp))
                a[x][y].setBackground(getResources().getDrawable((x + y) % 2 == 0 ? R.drawable.captured_square_green : R.drawable.captured_square));
        }
    }

    boolean validMove(int temp){
        ArrayList<Integer> rul = rules(currentCoin(curPos).charAt(0));
        String com = "";
        for(int i=0;i< rul.size();i++)
            com+=rul.get(i)+"_";
        if(com.contains(String.valueOf(temp)))
            return true;
        return false;
    }

    void controls(){
//        btnUp.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction()==MotionEvent.ACTION_UP) {
//                    long sec = (System.currentTimeMillis()-hold);
//                    Toast.makeText(MainActivity.this, String.valueOf(sec), Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                else if(motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
//                    hold = System.currentTimeMillis();
////                    Toast.makeText(MainActivity.this, "Started click", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                return false;
//            }
//        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateControl();
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

    void updateControl(){
        Toast.makeText(this, controlString, Toast.LENGTH_SHORT).show();
        databaseReference.child("control").setValue(controlString);
    }

    void updateFirebaseFdn(){
        databaseReference.child("coin_position").setValue(fdn);
    }

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
            controlString+=("-r"+manualControl[1]);
        if(manualControl[2]!=0)
            controlString+=("-d"+manualControl[2]);
        if(manualControl[3]!=0)
            controlString+=("-l"+manualControl[3]);

        btnSend.setText(controlString);
    }
}