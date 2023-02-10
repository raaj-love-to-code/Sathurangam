package com.anonymouscreations.sathurangam.popup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.anonymouscreations.sathurangam.LocalData.LocalUserData;
import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.Tools.BitmapHelper;
import com.anonymouscreations.sathurangam.activities.LoginActivity;
import com.anonymouscreations.sathurangam.database.UserData;

public class ProfilePopup extends Activity{

    // --- Declaring object for the popup window elements
    TextView tvWin, tvName, btnSignOut, btnEditPhoto;
    ImageView btnClose;
    static ImageView btnProfile;

    View view;
    Activity activity;
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
        View popupView = inflater.inflate(R.layout.popup_profile,null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,false);

        // --- Mapping the popup window elements to objects
        tvWin = popupView.findViewById(R.id.tvWin);
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

        // --- Button to sign out the user
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LocalUserData(view.getContext()).clearAll();
                popupWindow.dismiss();
                activity.startActivity(new Intent(activity, LoginActivity.class));
                activity.finish();
            }
        });

        // --- Button edit the profile picture
        btnEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPhoto();
            }
        });

        // --- Show popup activity with the specified location of the activity
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }

    void showDetails(){

        // --- Creating object for the LocalUserData class
        LocalUserData localUserData = new LocalUserData(view.getContext());

        // --- Getting user data from the method in LocalUserData
        UserData data = localUserData.getUserData();

        // --- Setting user name in the element from the object
        tvName.setText(data.getName());

        // --- Validating for the profile image
        String img = localUserData.getProfile();
        if(img.equals(""))
            return;

        // --- Setting image in the element
        btnProfile.setImageBitmap(BitmapHelper.StringToBitmap(img));
    }

    // --- Function called after clicking edit photo
    void editPhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(intent);
    }

    // --- Function which is static called from the HomeActivity
    public static void setProfile(Bitmap bitmap){
        btnProfile.setImageBitmap(bitmap);
    }

}
