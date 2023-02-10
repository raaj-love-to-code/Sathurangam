package com.anonymouscreations.sathurangam.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymouscreations.sathurangam.LocalData.LocalUserData;
import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.Tools.BitmapHelper;
import com.anonymouscreations.sathurangam.database.MyDatabase;
import com.anonymouscreations.sathurangam.popup.ProfilePopup;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    CardView cvSmartChessBoard, cvPlayOnline, cvSinglePlayerOffline, cvTwoPlayerOffline;
    TextView tvSCTotalMatch, tvSCWinPercentage, tvPOTotalMatch, tvPOWinPercentage, tvSPOTotalMatch, tvSPOWinPercentage,tvTPOTotalMatch, tvTPOWinPercentage;
    ImageView ivProfile;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- Choosing photo from the gallery for edit profile
        resultForProfile();

        // --- Hiding support action bar
        getSupportActionBar().hide();

        // --- Mapping elements to the objects
        ivProfile = findViewById(R.id.ivProfile);
        cvSmartChessBoard = findViewById(R.id.cvSmartChessBoard);
        cvPlayOnline = findViewById(R.id.cvPlayOnline);
        cvSinglePlayerOffline = findViewById(R.id.cvSinglePlayerOffline);
        cvTwoPlayerOffline = findViewById(R.id.cvTwoPlayerOffline);
        tvSCTotalMatch = findViewById(R.id.tvSCTotalMatch);
        tvSCWinPercentage = findViewById(R.id.tvSCWinPercentage);
        tvPOTotalMatch = findViewById(R.id.tvPOTotalMatch);
        tvPOWinPercentage = findViewById(R.id.tvPOWinPercentage);
        tvSPOTotalMatch = findViewById(R.id.tvSPOTotalMatch);
        tvSPOWinPercentage = findViewById(R.id.tvSPOWinPercentage);
        tvTPOTotalMatch = findViewById(R.id.tvTPOTotalMatch);
        tvTPOWinPercentage = findViewById(R.id.tvTPOWinPercentage);

        // --- Button smart chess click event
        cvSmartChessBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));;
            }
        });

        // --- Button profile click event
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // --- Calling popup window for the profile
                new ProfilePopup(view,HomeActivity.this, activityResultLauncher).showProfile();
            }
        });

    }

    // --- Activity result function for the profile picture selection
    void resultForProfile(){
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    // --- Validating the result code of intent
                    if(result.getResultCode() == Activity.RESULT_OK){

                        // --- Storing intent data
                        Intent data = result.getData();

                        // --- Validating the intent data
                        if(data != null && data.getData() != null){
                            Uri uri = data.getData();
                            Bitmap bitmap;

                            // --- Trying to assigning data to bitmap object
                            try{
                                bitmap = MediaStore.Images.Media.getBitmap(HomeActivity.this.getContentResolver(),uri);

                                String sample = BitmapHelper.BitmapToString(bitmap);

                                // --- Calling the static method of popup window to set the image in the popup element
                                ProfilePopup.setProfile(BitmapHelper.StringToBitmap(sample));

                                // --- Calling the database method to store the profile image in the firebase
                                LocalUserData localUserData = new LocalUserData(getApplicationContext());
                                localUserData.storeProfile(sample);

                                // --- Object to store data in the database
//                                new MyDatabase(getApplicationContext(),"user",HomeActivity.this).storeProfile(localUserData.getUserData().getEmail(),sample);
//                                Log.e("Profile Bitmap String", BitmapHelper.BitmapToString(bitmap).length()+"");
                            }catch(Exception e){
                                Toast.makeText(HomeActivity.this, "Try selecting again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

}