package com.anonymouscreations.sathurangam.support;

import android.graphics.drawable.Drawable;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.anonymouscreations.sathurangam.R;

public class Mapping extends AppCompatActivity {

    // --- Order of chess coins stored in the drawable object array
    String coinMap;

    int row = 8, col = 8;

    // === Constructor
    public Mapping(){
        coinMap = "prnbkqPRNBKQ";
    }

    // === Mapping the 8x8 imageview to the object
    public ImageView[][] mapPosition(LinearLayout ll){

        ImageView[][] a = new ImageView[8][8];
        GridLayout gl;

        for(int i=0;i<row;i++){
            gl = (GridLayout)ll.getChildAt(i);
            for(int j=0;j<col;j++)
                a[i][j] = (ImageView)gl.getChildAt(j);
        }

        return a;
    }

    // === Mapping drawable images to the object
    public Drawable[] mapCoins(){

        Drawable[] coins = new Drawable[12];
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

        return coins;
    }
}
