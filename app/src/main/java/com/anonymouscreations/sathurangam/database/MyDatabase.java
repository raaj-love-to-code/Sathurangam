package com.anonymouscreations.sathurangam.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.activities.MainActivity;
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
    MainActivity mainActivity;
    ProgressDialog progressDialog;

    // === Constructor
    public MyDatabase(Context context, String child, MainActivity mainActivity){
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

    void showLoading(){
        progressDialog.setMessage("Upadating...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

}
