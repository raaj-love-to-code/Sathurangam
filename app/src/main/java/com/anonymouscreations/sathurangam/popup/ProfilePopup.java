package com.anonymouscreations.sathurangam.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.anonymouscreations.sathurangam.LocalData.LocalUserData;
import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.Tools.BitmapHelper;
import com.anonymouscreations.sathurangam.Tools.DrawableHelper;
import com.anonymouscreations.sathurangam.activities.LoginActivity;
import com.anonymouscreations.sathurangam.database.MyDatabase;
import com.anonymouscreations.sathurangam.database.UserData;
import com.squareup.picasso.Picasso;

public class ProfilePopup extends Activity{

    // --- Declaring object for the popup window elements
    TextView tvWin, tvName, tvCoin, tvPopularity, tvGlobalRank, tvTotalMatch, btnDeactivateAccount;
    EditText etName;
    ImageView btnClose, ivLogo;
    PopupWindow popupWindow;

    // --- Declared static to assign values without creating the object
    static ImageView btnProfile, ivEditName;
    static TextView btnEditPhoto, btnSignOut, tvEditProfile;
    static Uri uri;
    static Bitmap bitmap;
    static View view, popupView;
    static Activity activity;
    ActivityResultLauncher<Intent> activityResultLauncher;

    // === Constructor
    public ProfilePopup(View view, Activity activity, ActivityResultLauncher<Intent> activityResultLauncher){
        this.view = view;
        this.activity = activity;
        this.activityResultLauncher = activityResultLauncher;
    }

    public void showProfile(){

        // --- Configuring the popup window object to display in the home activity
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_profile,null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,false);

        // --- Mapping the popup window elements to objects
        tvWin = popupView.findViewById(R.id.tvWin);
        tvCoin = popupView.findViewById(R.id.tvCoin);
        tvPopularity = popupView.findViewById(R.id.tvPopularity);
        tvGlobalRank = popupView.findViewById(R.id.tvGlobalRank);
        tvTotalMatch = popupView.findViewById(R.id.tvTotalMatch);
        btnDeactivateAccount = popupView.findViewById(R.id.btnDeactivateAccount);
        ivLogo = popupView.findViewById(R.id.ivLogo);
        etName = popupView.findViewById(R.id.etName);
        ivEditName = popupView.findViewById(R.id.ivEditName);
        tvEditProfile = popupView.findViewById(R.id.tvEditProfile);
        tvName = popupView.findViewById(R.id.tvName);
        btnSignOut = popupView.findViewById(R.id.btnSignOut);
        btnEditPhoto = popupView.findViewById(R.id.btnEditPhoto);
        btnProfile = popupView.findViewById(R.id.btnProfile);
        btnClose = popupView.findViewById(R.id.btnClose);

        // --- Show user name and profile
        showDetails();

        // --- Button to close the popup
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        performButtonClicks();

        // --- Show popup activity with the specified location of the activity
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }

    // === Function to load details from the local storage
    public void showDetails(){

        // --- Resetting the color and text of all elements
        btnSignOut.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.greenHigh));
        btnEditPhoto.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.greenHigh));
        btnSignOut.setText("Sign out");
        btnEditPhoto.setText("Edit Profile");
        tvEditProfile.setVisibility(View.GONE);
        ivEditName.setVisibility(View.GONE);
        ivLogo.setVisibility(View.VISIBLE);
        tvName.setVisibility(View.VISIBLE);
        etName.setVisibility(View.GONE);

        uri = null;
        etName.setText(tvName.getText().toString());

        // --- Getting user data from the LocalUserData
        LocalUserData localUserData = new LocalUserData(view.getContext());
        UserData data = localUserData.getUserData();

        // --- Validating for the profile image
        String img = localUserData.getUserData().getProfile();
        if(img.equals(""))
            return;

        // --- Setting user data to the elements of the popup
        tvName.setText(data.getName());
        Picasso.get().load(img).into(btnProfile);
    }

    // === Function called after clicking edit profile
    void editPhoto(){
        tvEditProfile.setVisibility(View.VISIBLE);
        ivEditName.setVisibility(View.VISIBLE);
        ivLogo.setVisibility(View.GONE);
        etName.setText(tvName.getText().toString());

        // --- Updating the color and text of the button
        btnEditPhoto.setBackgroundTintList(view.getResources().getColorStateList(R.color.accept));
        btnSignOut.setBackgroundTintList(view.getResources().getColorStateList(R.color.decline));
        btnEditPhoto.setText("Save Profile");
        btnSignOut.setText("Cancel");
    }

    // === Choose image from the device
    void choosePhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // --- This calls the launcher in the HomeActivity
        activityResultLauncher.launch(intent);
    }

    // === Function which is static, have been called from the HomeActivity
    public static void setProfile(Bitmap bitmap, Uri uri){

        // --- Updating the image in the view
        btnProfile.setImageBitmap(bitmap);
        ProfilePopup.bitmap = bitmap;
        ProfilePopup.uri = uri;
    }

    // === Button click functionality
    void performButtonClicks(){

        // --- Button to sign out the user or cancel
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btnSignOut.getText().equals("Cancel"))
                    showDetails();
                else{
                    new LocalUserData(view.getContext()).clearAll();
                    popupWindow.dismiss();
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                    activity.finish();
                }
            }
        });

        // --- Button edit the profile picture or save changes
        btnEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // --- On clicking edit photo button
                if(btnEditPhoto.getText().equals("Edit Profile"))
                    editPhoto();

                // --- On click save changes button
                else if(btnEditPhoto.getText().equals("Save Profile")) {

                    // --- Validating input fields
                    if(!validateInput())
                        return;

                    // --- Updating name in the user data object
                    UserData data = new LocalUserData(view.getContext()).getUserData();
                    data.setName(etName.getText().toString().trim());

                    // --- Object to store data in the database
                    new MyDatabase(view.getContext(),"user",activity).storeProfile(data,uri,bitmap, activity,ProfilePopup.this);
                    showDetails();
                }
            }
        });

        // --- Button to edit name
        ivEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvName.setVisibility(View.GONE);
                etName.setText(tvName.getText().toString());
                etName.setVisibility(View.VISIBLE);
                popupWindow.setFocusable(true);
                popupWindow.update();
                etName.requestFocus();
            }
        });

        // --- Button to edit profile picture
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

        // --- Button to deactivate account
        btnDeactivateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // --- Creating builder class for alert dialog
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                alertBuilder.setMessage("Your account will be deactivated and all your data will be lost permanently\nAre you sure you want to continue?")
                        .setTitle("Delete account!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // --- Delete all records from the database
                                // --- Code goes here
                                Toast.makeText(view.getContext(), "Wrong decision!\nThink again and come back", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                // --- Creating alert dialog with builder class for confirmation
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.setTitle("Warning!");
                alertDialog.show();
            }
        });

    }

    // === Validating user input for updation
    boolean validateInput(){
        String name = etName.getText().toString().trim();
        if(!(name.length() > 0)) {
            Toast.makeText(view.getContext(), "Invalid user name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
