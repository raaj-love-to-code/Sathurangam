package com.anonymouscreations.sathurangam.LocalData;

import android.content.Context;
import android.content.SharedPreferences;

import com.anonymouscreations.sathurangam.database.UserData;
import com.google.gson.Gson;

public class LocalUserData {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;

    public LocalUserData(Context context){
        sharedPreferences = context.getSharedPreferences("Sathurangam",context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
    }

    public void storeLogin(UserData userData){
        String data = new Gson().toJson(userData);
        edit.putString("userData",data);
        edit.commit();
    }

    public UserData getUserData(){
        return new Gson().fromJson(sharedPreferences.getString("userData",""),UserData.class);
    }

    public void clearAll(){
        edit.putString("userData","");
        edit.commit();
    }
}
