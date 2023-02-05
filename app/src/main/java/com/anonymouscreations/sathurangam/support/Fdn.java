package com.anonymouscreations.sathurangam.support;

public class Fdn {

    int row, col;
    String fdn;

    // === Constructor
    public Fdn(String fdn){
        this.fdn = fdn;
        row = 8;
        col = 8;
    }

    // === Fdn getter function
    public String getFdn(){
        return fdn;
    }

    // === Fdn setter function
    public void setFdn(String fdn){
        this.fdn = fdn;
    }

    // === Converting FDN text to 2-D array
    public char[][] briefFDN(){
        String[] out = fdn.split(" ")[0].split("/");
        char[][] o1 = new char[8][8];
        for(int i=0;i<row;i++) {
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

    // === Converting 2-D array to FDN text
    public void mergeFDN(char[][] r){
        String s = "";
        for(int i=0;i<row;i++){
            int x = 0;
            for(int j=0;j<col;j++) {
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

}
