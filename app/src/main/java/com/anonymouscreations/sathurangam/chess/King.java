package com.anonymouscreations.sathurangam.chess;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageView;

import com.anonymouscreations.sathurangam.R;

import java.util.ArrayList;

public class King {

    // --- Comparing the possible paths with King's coordinates for check alert
    static int checkForKing(ArrayList<Integer> pos, char[][] row, char coin){

        // looping through each possible ways
        for(int k=0;k<pos.size();k++){
            int c = pos.get(k);
            if((row[c/10][c%10] == 'k' && Character.isUpperCase(coin)) ||
                    (row[c/10][c%10] == 'K' && Character.isLowerCase(coin)))
                return c;
        }
        return -1;
    }
}
