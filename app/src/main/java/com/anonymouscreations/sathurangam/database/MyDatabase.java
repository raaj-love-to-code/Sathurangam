package com.anonymouscreations.sathurangam.database;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.anonymouscreations.sathurangam.LocalData.LocalUserData;
import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.activities.HomeActivity;
import com.anonymouscreations.sathurangam.chess.Fdn;
import com.anonymouscreations.sathurangam.chess.Mapping;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyDatabase {

    Context context;
    DatabaseReference databaseReference;
    Activity mainActivity;
    ProgressDialog progressDialog;
    boolean existingUser;
    // === Constructor
    public MyDatabase(Context context, String child, Activity mainActivity){
        existingUser = false;
        this.mainActivity = mainActivity;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference().child(child);
        progressDialog = new ProgressDialog(mainActivity);
    }

    // === Updating Control data in the firebase
    public void updateControlString(String controlString){

        // --- updating data to the key 'control' in google firebase
        databaseReference.child("control").setValue(controlString).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(context,"Data updated successfully "+controlString,Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Something went wrong while updating control value\nTry again!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // === Updating FDN data in the firebase
    public void updateFdnString(String fdn){

        showLoading();
        // --- Updating FDN data to the key 'coin_position' in google firebase
        databaseReference.child("coin_position").setValue(fdn).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context.getApplicationContext(),"Updated FDN successfully\n"+fdn,Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Something went wrong while updating FDN value\nTry again",Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }

    // === pull updated values in the database
    public void pullData(Mapping mapping, Fdn fdn){

        databaseReference.child("coin_position").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MediaPlayer.create(context, R.raw.move).start();
                fdn.setFdn(snapshot.getValue(String.class));
                mapping.arrange(fdn);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"Something went wrong\nWhile pulling data from the database",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // === Function to display progress dialog while updating the data in the database
    void showLoading(){
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // === Validating the user existence for creating a new account
    public void checkAccountExistence(UserData data){

        // --- Calling progress dialog
        showLoading();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData tempData;
                String currentChild = "";
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    tempData = dataSnapshot.getValue(UserData.class);
                    if(tempData.getEmail().equals(data.getEmail())) {
                        currentChild = dataSnapshot.getKey();
                        existingUser = true;
                        break;
                    }
                }

                // --- Validating for the existing user
                if(existingUser) {
                    Toast.makeText(context, "Account already exists, Try logging in with "+data.getEmail(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else
                    createNewAccount(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(context,"Something went wrong, try again",Toast.LENGTH_LONG).show();
            }
        });
    }

    // === Creating new user in the database
    void createNewAccount(UserData data){
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show();
                        new LocalUserData(context).storeLogin(data);
                        mainActivity.startActivity(new Intent(mainActivity,HomeActivity.class));
                        mainActivity.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Something went wrong! try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // === login database function
    public void loginUser(LoginUserData loginUserData){

        // --- Calling function show progress dialog
        showLoading();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData data = new UserData();
                boolean password = false;

                // --- Validating email and password from database
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    data = dataSnapshot.getValue(UserData.class);
                    if(data.getEmail().equals(loginUserData.getEmail())) {
                        existingUser = true;
                        if (data.getPassword().equals(loginUserData.getPassword()))
                            password = true;
                    }
                }
                progressDialog.dismiss();

                // --- Validating for user and password status
                if(existingUser && password){
                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                    new LocalUserData(context).storeLogin(data);
                    mainActivity.startActivity(new Intent(mainActivity, HomeActivity.class));
                    mainActivity.finish();
                }else if(existingUser)
                    Toast.makeText(context, "Incorrect Password! Try again", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Account not found!\nCreate new account", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Something went wrong! Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // === Function to store the profile url
    public void storeProfile(String email, String img){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData data;
                String currentChild = "";
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    data = dataSnapshot.getValue(UserData.class);
                    if(data.getEmail().equals(email)) {
                        currentChild = dataSnapshot.getKey();
                        break;
                    }
                }
                if(!currentChild.equals(""))
                    saveProfile(currentChild,img);
                else
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void saveProfile(String child, String profile){
        showLoading();
        databaseReference.child(child).child("profile").setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }

}
