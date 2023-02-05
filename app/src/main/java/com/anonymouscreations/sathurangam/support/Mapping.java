package com.anonymouscreations.sathurangam.support;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.anonymouscreations.sathurangam.R;

public class Mapping {

    ImageView[][] a;
    Drawable[] coins;
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
        char row[][] = fdn.briefFDN();
        for(int i=0;i<this.row;i++){
            for(int j=0;j<col;j++){
                if(row[i][j]=='1')
                    a[i][j].setImageResource(0);
                else
                    a[i][j].setImageDrawable(coins[coinMap.indexOf(String.valueOf(row[i][j]))]);
            }
        }
    }
}
