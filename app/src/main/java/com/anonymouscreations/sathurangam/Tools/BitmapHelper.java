package com.anonymouscreations.sathurangam.Tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import com.anonymouscreations.sathurangam.activities.HomeActivity;
import com.anonymouscreations.sathurangam.popup.ProfilePopup;

import java.io.ByteArrayOutputStream;

public class BitmapHelper {

    // === Converting the bitmap to string
    static public String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,baos);
        byte[] data = baos.toByteArray();
        return Base64.encodeToString(data,Base64.DEFAULT);
    }

    // === Converting the string to bitmap
    static public Bitmap stringToBitmap(String encodedData){
        try{
            byte[] data = Base64.decode(encodedData,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            return bitmap;
        }catch (Exception e){
            return null;
        }
    }
}
