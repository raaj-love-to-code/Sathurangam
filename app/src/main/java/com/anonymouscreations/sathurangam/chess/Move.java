package com.anonymouscreations.sathurangam.chess;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageView;

import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.database.MyDatabase;

import java.util.ArrayList;

public class Move {

    Context context;
    MyDatabase myDatabase;
    Mapping mapping;
    Fdn fdn;
    Rules rules;

    // === Constructor
    public Move(Context context, MyDatabase myDatabase){
        this.context = context;
        this.myDatabase = myDatabase;
    }

    // === Function to validate move
    boolean validMove(int temp, ArrayList<Integer> rul){

        // --- Check existence of the coordinate in the possible path of the coin
        for(int i=0;i<rul.size();i++)
            if(rul.get(i) == temp)
                return true;
        return false;
    }

    // === Updating the fdn in the board and database after movement of the coin
    public void moveCoin(int p, Mapping mapping, Fdn fdn, int curPos) {

        MediaPlayer.create(context,R.raw.move).start();

        char[][] row = fdn.briefFDN();
        mapping.reset();
        row[p/10][p%10] = row[curPos/10][curPos%10];
        row[curPos/10][curPos%10] = '1';
        fdn.mergeFDN(row);

        // --- Validating king CHECK for the move
        /*
           We need just the changes in the position alone, not the color, because
           we need to check for the opponent king CHECK, Hence after this validation process
           we can change the opponent turn in FDN text
         */
//        new King().checkForKing(rules.check(fdn,row[p/10][p%10],p),row, mapping.a, context, row[p/10][p%10]);

        // --- Changing the color to denote the opponent turn
        if(fdn.getFdn().split(" ")[1].equals("b"))
            fdn.setFdn(fdn.getFdn().split(" ")[0]+" "+"w");
        else
            fdn.setFdn(fdn.getFdn().split(" ")[0]+" "+"b");
        mapping.arrange(fdn);
        Log.e("MoveFdn",fdn.getFdn());
        mapping.a[curPos/10][curPos%10].setBackground(context.getResources().getDrawable(((curPos/10)+(curPos%10))%2==0 ? R.drawable.square_border_green : R.drawable.square_border));
        mapping.a[p/10][p%10].setBackground(context.getResources().getDrawable(((p/10)+(p%10))%2==0 ? R.drawable.square_border_green : R.drawable.square_border));
        myDatabase.updateFdnString(fdn.getFdn());

    }

    // === Handling click
    public int clickCoin(Mapping mapping, Rules rules, Fdn fdn, ImageView temp, int tempI, int curPos){
        this.mapping = mapping;
        this.fdn = fdn;
        this.rules = rules;

        String curCoin = currentCoin(tempI);

        // --- Validating click on empty position
        if(currentCoin(tempI).equals("1") && curPos == -1)
            return curPos;

        // --- Setting possible path for the clicked coin
        else if(curPos == -1 ||
                curPos == tempI ||
                Character.isLowerCase(curCoin.charAt(0)) &&
                        fdn.getFdn().split(" ")[1].equals("b") ||
                Character.isUpperCase(curCoin.charAt(0)) &&
                        fdn.getFdn().split(" ")[1].equals("w")) {
            mapping.reset();
            curPos = tempI;
            mapping.possiblePath(rules.check(fdn,currentCoin(tempI).charAt(0),curPos),fdn);
            temp.setBackgroundColor(context.getResources().getColor((tempI % 10 + tempI / 10) % 2 == 0 ? R.color.greenHigh : R.color.grey));

        }

        // --- Validating the destination and calling moveCoin method
        else if(validMove(tempI, rules.check(fdn,currentCoin(curPos).charAt(0),curPos))) {
            moveCoin(tempI, mapping, fdn, curPos);
            curPos = -1;
        }

        // --- Updating the current position in the MainActivity
        return curPos;
    }

    // === Identifying the coin in the clicked position
    String currentCoin(int t){
        char row[][] = fdn.briefFDN();

        // --- Verifying the clicked position
        char coin = row[t/10][t%10];
        if(mapping.coinMap.contains(String.valueOf(coin)) &&
                ((Character.isUpperCase(coin) && fdn.getFdn().split(" ")[1].equals("w")) ||
                        (Character.isLowerCase(coin) && fdn.getFdn().split(" ")[1].equals("b"))))
            return String.valueOf(coin);
        return "1";
    }

}
