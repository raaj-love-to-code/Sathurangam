package com.anonymouscreations.sathurangam.chess;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anonymouscreations.sathurangam.R;

import java.util.ArrayList;

public class Mapping {

    public ImageView[][] a;
    public Drawable[] coins;
    Context context;
    public String coinMap;
    int row, col;

    // === Constructor
    public Mapping(Context context){

        // --- Mapping context
        this.context = context;

        // --- Order of chess coins stored in the drawable object array
        coinMap = "prnbkqPRNBKQ";

        // --- Initializing variables and objects
        row = 8;
        col = 8;
        a = new ImageView[row][col];
            // ... There are 12 unique coins in chess
        coins = new Drawable[12];
    }

    // === Mapping the 8x8 imageview to the object
    public ImageView[][] mapPosition(LinearLayout ll){

        /*
            Linear layout contains 8 Grid layouts,
            each grid layout contains 8 Imageview
         */
        GridLayout gl;
        for(int i=0;i<row;i++){
            gl = (GridLayout)ll.getChildAt(i);
            for(int j=0;j<col;j++)
                a[i][j] = (ImageView)gl.getChildAt(j);
        }

        return a;
    }

    // === Mapping drawable images to the object
    public Drawable[] mapCoins() {

        coins[0] = context.getResources().getDrawable(R.drawable.bp);
        coins[1] = context.getResources().getDrawable(R.drawable.br);
        coins[2] = context.getResources().getDrawable(R.drawable.bn);
        coins[3] = context.getResources().getDrawable(R.drawable.bb);
        coins[4] = context.getResources().getDrawable(R.drawable.bk);
        coins[5] = context.getResources().getDrawable(R.drawable.bq);
        coins[6] = context.getResources().getDrawable(R.drawable.wp);
        coins[7] = context.getResources().getDrawable(R.drawable.wr);
        coins[8] = context.getResources().getDrawable(R.drawable.wn);
        coins[9] = context.getResources().getDrawable(R.drawable.wb);
        coins[10] = context.getResources().getDrawable(R.drawable.wk);
        coins[11] = context.getResources().getDrawable(R.drawable.wq);

        return coins;
    }

    // === Updating coin positions in the board based on the value of Fdn string
    public void arrange(Fdn fdn){
        System.out.println(fdn.getFdn());

        // --- Validating the status of the king after the last move
        char row[][] = fdn.briefFDN();
        char check = '1';

        // --- Assigning temporary object for validating the king status
        Fdn tempFdn = new Fdn(fdn.getFdn());
        String[] splitFdn = fdn.getFdn().split(" ");
        tempFdn.setFdn(splitFdn[0] +" "+ (splitFdn[1].charAt(0) == 'b' ? 'w' : 'b') +" "+ splitFdn[2]);

        // --- Assigning the opponent King for validation
        char king = splitFdn[1].charAt(0) == 'b' ? 'k' : 'K';

        // --- Validating the possibility for check and assigning the character to the variable for highlighting
        if(King.hasCheck(tempFdn,king)) {
            check = king;
            MediaPlayer.create(context,R.raw.check).start();
        }

        for(int i=0;i<this.row;i++){
            for(int j=0;j<col;j++){

                // --- Highlighting the background for the King in CHECK
                if(check != '1' && row[i][j] == check)
                    a[i][j].setBackground(context.getResources().getDrawable((i + j) % 2 == 0 ? R.drawable.check_alert_green : R.drawable.check_alert));

                // --- Mapping coins in the UI
                if(row[i][j]=='1')
                    a[i][j].setImageResource(0);
                else
                    a[i][j].setImageDrawable(coins[coinMap.indexOf(String.valueOf(row[i][j]))]);
            }
        }

        // updating the king status to the FDN
        fdn.setFdn(splitFdn[0]+" "+splitFdn[1]+" "+check);

        reset(fdn);
    }

    // === Resetting the focus
    public void reset(Fdn fdn){
        char[][] row = fdn.briefFDN();
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){

                // --- Eliminating the background of the king in CHECK
                if(fdn.getFdn().split(" ")[2].charAt(0) == row[i][j] && row[i][j] != '1')
                    continue;

                // --- Reassigning the background for all squares
                a[i][j].setBackgroundColor(context.getResources().getColor((i+j)%2 == 0 ? R.color.green : R.color.white));
            }
        }
    }

    // === Highlighting the possible path for the selected coin
    public void possiblePath(ArrayList<Integer> pos, Fdn fdn){

        // --- Looping through each possible path and highlighting the background
        for(int i=0;i<pos.size();i++){
            int x = pos.get(i)/10, y = pos.get(i)%10;
            a[x][y].setBackground(context.getResources().getDrawable((x + y) % 2 == 0 ? R.drawable.possible_move_green : R.drawable.possible_move));
        }
        captureAlert(pos, fdn);
    }

    // === Highlighting the opponent coins in the possible path
    public void captureAlert(ArrayList<Integer> pos, Fdn fdn){

        char row[][] = fdn.briefFDN();

        // --- Looping through each possible paths
        for(int i=0;i<pos.size();i++){

            // --- Identifying the coin in the path
            char temp = row[pos.get(i)/10][pos.get(i)%10];

            // --- Extracting the coordinates
            int x = pos.get(i)/10, y = pos.get(i)%10;

            // --- Highlighting the opponent coin
            if(fdn.getFdn().split(" ")[1].equals("b") && Character.isUpperCase(temp))
                a[x][y].setBackground(context.getResources().getDrawable((x + y) % 2 == 0 ? R.drawable.captured_square_green : R.drawable.captured_square));
            else if(fdn.getFdn().split(" ")[1].equals("w") && Character.isLowerCase(temp))
                a[x][y].setBackground(context.getResources().getDrawable((x + y) % 2 == 0 ? R.drawable.captured_square_green : R.drawable.captured_square));

        }
    }
}
