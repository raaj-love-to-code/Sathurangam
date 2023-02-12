package com.anonymouscreations.sathurangam.Tools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Log;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class DrawableHelper {

    // === Function to change the background of drawable
    static public void changeTextDrawableBackground(Context context, TextView textView, int newBackground){
        Drawable drawable = textView.getBackground();
        if(drawable instanceof ShapeDrawable){
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(context,newBackground));
        }else if(drawable instanceof GradientDrawable){
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(ContextCompat.getColor(context,newBackground));
        }
    }
}
