package com.anonymouscreations.sathurangam.chess;

import android.util.Log;

import java.util.ArrayList;

public class Rules {

    Fdn fdn;
    ArrayList<Integer> pos;
    char[][] row;
    char currentCoin;
    int x,y;
    boolean strongValidation = true;

    // === Constructor
    public Rules(){
        pos = new ArrayList<Integer>();
    }

    // === Overloaded function to check without validation with same side coins
    public ArrayList<Integer> check(Fdn fdn, char currentCoin, int curPos, boolean strongValidation){
        this.strongValidation = strongValidation;
        return check(fdn,currentCoin,curPos);
    }

    // === Check for rules
    public ArrayList<Integer> check(Fdn fdn, char currentCoin, int curPos){

        pos.clear();
        this.fdn = fdn;
        this.currentCoin = currentCoin;
        row = fdn.briefFDN();
        // --- Extracting coordinates of current position
        x = curPos/10;
        y = curPos%10;

        if(Character.toLowerCase(currentCoin) == 'p')
            pawn();
        else if(Character.toLowerCase(currentCoin) == 'n')
            knight();
        else if(Character.toLowerCase(currentCoin) == 'b')
            bishop();
        else if(Character.toLowerCase(currentCoin) == 'r')
            rook();
        else if(Character.toLowerCase(currentCoin) == 'q')
            queen();
        else if(Character.toLowerCase(currentCoin) == 'k')
            king();

        if(strongValidation)
            validate();
        return pos;
    }

    // === Validating the result for the same side coins
    void validate(){
        for(int i=0;i<pos.size();i++){
            char temp = row[pos.get(i)/10][pos.get(i)%10];
            if(fdn.getFdn().split(" ")[1].equals("w") && Character.isUpperCase(temp))
                pos.remove(i--);
            else if(fdn.getFdn().split(" ")[1].equals("b") && Character.isLowerCase(temp))
                pos.remove(i--);
        }
    }

    // === Rules for pawn
    void pawn(){

        //  --- Check for pawn in last position
        if((Character.isUpperCase(currentCoin) && x == 0) ||
                (Character.isLowerCase(currentCoin) && x == 7))
            return;

        //  --- Determining the direction for movement
        int s = 1;
        if(Character.isUpperCase(currentCoin))
            s = -1;

        //  --- Position for one step move
        if(row[(x + (1*s))][y] == '1')
            pos.add(((x + (1*s)) * 10) + y);

        //  --- Position for second step move
        if (pos.size() > 0 &&
                row[(x + (2*s))][y] == '1' &&
                ((x == 6 && Character.isUpperCase(currentCoin)) ||
                        (x == 1 && Character.isLowerCase(currentCoin))))
            pos.add(((x + (2*s)) * 10) + y);

        //  --- Side position for capturing coin
        int a = x+(1*s);
        if(y+1<=7 && row[a][y+1]!='1' &&
                ( Character.isUpperCase(currentCoin)  &&
                        (Character.isLowerCase(row[a][y+1])) ||
                        (Character.isLowerCase(currentCoin)  &&
                                Character.isUpperCase(row[x+(1*s)][y+1])))){
            pos.add((a * 10) + y + 1);
        }
        if(y-1>=0 && row[a][y-1]!='1' &&
                ( Character.isUpperCase(currentCoin) &&
                        (Character.isLowerCase(row[a][y-1])) ||
                        (Character.isLowerCase(currentCoin)  &&
                                Character.isUpperCase(row[x+(1*s)][y-1])))){
            pos.add((a * 10) + y - 1);
        }
    }

    // === Rules for knight
    void knight(){

        // --- Knight moves only total of 3 steps in any direction in 'L' shape
        int temp[] = new int[]{1,-1,2,-2};

        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if(temp[i]==temp[j] || -temp[i]==temp[j])
                    continue;
                if(x+temp[i]<8 && y+temp[j]<8 && x+temp[i]>-1 && y+temp[j]>-1)
                    pos.add(((x+temp[i])*10)+y+temp[j]);
            }
        }
    }

    // === Rules for Bishop
    void bishop(){

        // --- Right down diagonal
        for(int i=1;i<8;i++){
            if(x+i < 8 && y+i < 8) {
                pos.add(((x + i) * 10) + y + i);
                if (row[x + i][y + i] != '1')
                    break;
            }
        }

        // --- Left up diagonal
        for(int i=1;i<8;i++){
            if(x-i >= 0 && y-i >= 0) {
                pos.add(((x - i) * 10) + y - i);
                if (row[x - i][y - i] != '1')
                    break;
            }
        }

        // --- Right up diagonal
        for(int i=1;i<8;i++){
            if(x-i >= 0 && y+i < 8) {
                pos.add(((x - i) * 10) + y + i);
                if (row[x - i][y + i] != '1')
                    break;
            }
        }

        // --- Left down up diagonal
        for(int i=1;i<8;i++){
            if(x+i < 8 && y-i >= 0) {
                pos.add(((x + i) * 10) + y - i);
                if (row[x + i][y - i] != '1')
                    break;
            }
        }
    }

    // === Rules for Rook
    void rook(){
        boolean up = true, down = true, right = true, left = true;

        // --- Rook moves only in vertical or horizontal direction without jumping
        for(int i=1;i<8;i++){
            if(up && x-i >= 0) {
                pos.add(((x - i) * 10) + y);
                if(row[x-i][y] != '1')
                    up = false;
            }
            if(down && x+i < 8) {
                pos.add(((x + i) * 10) + y);
                if (row[x + i][y] != '1')
                    down = false;
            }
            if(right && y+i < 8) {
                pos.add((x * 10) + y + i);
                if(row[x][y+i] != '1')
                    right = false;
            }
            if(left && y-i >= 0) {
                pos.add((x * 10) + y - i);
                if(row[x][y-i] != '1')
                    left = false;
            }
        }
    }

    // === Rules for Queen
    void queen(){

        // --- Queen has both the characters of bishop and rook
        bishop();
        rook();
    }

    // === Rules for King
    void king(){
        if(x+1 < 8)
            pos.add(((x+1)*10)+y);
        if(x-1 >= 0)
            pos.add(((x-1)*10)+y);
        if(y+1 < 8)
            pos.add((x*10)+y+1);
        if(y-1 >= 0)
            pos.add((x*10)+y-1);
        if(x+1 < 8 && y+1 < 8)
            pos.add(((x+1)*10)+y+1);
        if(x-1 >= 0 && y-1 >= 0)
            pos.add(((x-1)*10)+y-1);
        if(x-1 >= 0 && y+1 < 8)
            pos.add(((x-1)*10)+y+1);
        if(x+1 < 8 && y-1 >= 0)
            pos.add(((x+1)*10)+y-1);
    }


}
