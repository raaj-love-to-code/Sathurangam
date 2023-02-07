package com.anonymouscreations.sathurangam.chess;

import android.util.Log;
import android.view.FocusFinder;

import java.util.ArrayList;

public class King {

    // === Generating temporary possible move and validating with the king status
    static ArrayList<Integer> kingValidate(Fdn fdn, int move, int curPos){
        char[][] row = fdn.briefFDN();

        // --- Generating temporary FDN object for the simulating the move
        Fdn tempFdn = new Fdn(fdn.getFdn());

        // --- Arraylist to store the position of the opponent coin which could cause check for the King
        ArrayList<Integer> attack = new ArrayList<Integer>();

        // --- Updating the move in the temporary FDN object
        row[move/10][move%10] = row[curPos/10][curPos%10];
        row[curPos/10][curPos%10] = '1';

        // --- Updating the color for next move in the temporary FDN object
        if(fdn.getFdn().split(" ")[1].equals("b"))
            tempFdn.setFdn(fdn.getFdn().split(" ")[0]+" w "+fdn.getFdn().split(" ")[2]);
        else
            tempFdn.setFdn(fdn.getFdn().split(" ")[0]+" b "+fdn.getFdn().split(" ")[2]);

        // --- Updated the simulated possible move in the temporary FDN object
        tempFdn.mergeFDN(row);

        // --- Validating the simulated move with the king status
        if(hasCheck(tempFdn, (Character.isLowerCase(row[move/10][move%10]) ? 'k' : 'K')))
            attack.add(move);

        return attack;
    }

    // === Check whether the possible move is possible or not
    static boolean hasCheck(Fdn fdn, char king){
        char row[][] = fdn.briefFDN();
        ArrayList<Integer> pos;

        // --- Looping through all positions of the board
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){

                // --- Eliminating the same side coins and the empty spaces
                if((Character.isLowerCase(king) && Character.isLowerCase(row[i][j])) ||
                        (Character.isUpperCase(king) && Character.isUpperCase(row[i][j])) ||
                        row[i][j]=='1')
                    continue;

                // --- Generating the possible path for all opponent coins after the simulated move
                pos = new Rules().check(fdn,row[i][j],(i*10)+j,false);

                // --- Looping through each possible ways of the opponent to identify the CHECK for the king
                for(int k=0;k<pos.size();k++)
                    if(row[pos.get(k)/10][pos.get(k)%10] == king)
                        return true;
            }
        }
        return false;
    }

    static boolean checkMate(Fdn fdn){

        char[][] row = fdn.briefFDN();
        ArrayList<Integer> pos;
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if((fdn.getFdn().split(" ")[1].charAt(0) == 'b' && Character.isUpperCase(row[i][j])) ||
                        (fdn.getFdn().split(" ")[1].charAt(0) == 'w' && Character.isLowerCase(row[i][j])))
                    continue;
                pos = new Rules().check(fdn,row[i][j],(i*10)+j);
                if(pos.size()>0)
                    return false;
            }
        }
        return true;
    }

}
