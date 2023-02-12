package com.anonymouscreations.sathurangam.database;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.anonymouscreations.sathurangam.LocalData.LocalUserData;
import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.Tools.BitmapHelper;
import com.anonymouscreations.sathurangam.activities.HomeActivity;
import com.anonymouscreations.sathurangam.chess.Fdn;
import com.anonymouscreations.sathurangam.chess.Mapping;
import com.anonymouscreations.sathurangam.popup.ProfilePopup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MyDatabase {

    Context context;
    DatabaseReference databaseReference;
    Activity mainActivity;
    ProfilePopup profilePopup;
    ProgressDialog progressDialog;
    boolean existingUser;
    String currentChild;
    Bitmap bitmap;

    // === Constructor
    public MyDatabase(Context context, String child, Activity mainActivity){
        existingUser = false;
        this.mainActivity = mainActivity;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference().child(child);
        progressDialog = new ProgressDialog(mainActivity);
        currentChild = "";
        bitmap = null;
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

        showLoading("Please wait");
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

    // === Function to display progress dialog while updating the data in the database
    void showLoading(String status){
        progressDialog.setMessage(status);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // === Validating the user existence for creating a new account
    public void checkAccountExistence(UserData data){

        // --- Calling progress dialog
        showLoading("Please wait...");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData tempData;

                // --- Looping through all users for existing user account
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    tempData = dataSnapshot.getValue(UserData.class);
                    if(tempData.getEmail().equals(data.getEmail())) {
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
    private void createNewAccount(UserData data){
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
        showLoading("Please wait...");
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

                    // --- Store user data in local storage
                    new LocalUserData(context).storeLogin(data);

                    // --- Navigating to home activity
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
    public void storeProfile(UserData currentUserData, Uri uri, Bitmap bitmap, Activity activity, ProfilePopup profilePopup){
        this.bitmap = bitmap;
        mainActivity = activity;
        this.profilePopup = profilePopup;
        // --- Display loading progress dialog
        showLoading("Updating profile in the server");

        // --- Extracting key for the user in firebase Real time Database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData data = null;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    data = dataSnapshot.getValue(UserData.class);
                    if(data.getEmail().equals(currentUserData.getEmail())) {
                        currentChild = dataSnapshot.getKey();
                        break;
                    }
                }
                // --- Validating for the key and data in the uri
                if(!currentChild.equals(""))
                    if(uri == null)
                        storeProfileInRTDB(currentUserData);
                    else
                        saveProfileInDatabaseStorage(currentUserData ,uri);
                else
                    Toast.makeText(context, "Something went wrong!\nKindly sign in again", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // === Function to upload image in the firebase cloud storage
    private void saveProfileInDatabaseStorage(UserData currentUserData, Uri uri){

        // --- Store image in firebase cloud storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user/profile/"+currentUserData.getName()+"_"+currentChild);
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // --- Fetching the download URL of the uploaded image
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // Updating download url in the object
                        currentUserData.setProfile(uri.toString());

                        // --- After successful upload get download url and store it in RTDB
                        storeProfileInRTDB(currentUserData);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Something went wrong!\nTry again!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, "Failed to upload data, Try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // === Store the download URL of the profile in the Real time database of the existing user account
    private void storeProfileInRTDB(UserData currentUserData){
        databaseReference.child(currentChild).setValue(currentUserData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                // --- Updating local storage after successfully stored in the database
                new LocalUserData(context).storeLogin(currentUserData);

                // --- Updating in the image views of popup window and home activity
                profilePopup.showDetails();
                ImageView profile = mainActivity.findViewById(R.id.ivProfile);
                Picasso.get().load(currentUserData.getProfile()).into(profile);

                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Something went wrong! Try again!", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }

    // === Deactivate account


}
