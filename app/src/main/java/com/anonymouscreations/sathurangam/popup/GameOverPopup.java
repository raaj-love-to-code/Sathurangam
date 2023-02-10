package com.anonymouscreations.sathurangam.popup;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.activities.MainActivity;
import com.anonymouscreations.sathurangam.chess.Fdn;

public class GameOverPopup {

    TextView tvGameStatus, tvTotalMoves, tvDuration, btnRestart, btnHome;
    ImageView btnClose;
    View view;
    int duration, halfMove;

    public GameOverPopup(View view){
        this.view = view;
        duration = MainActivity.duration;
        halfMove = MainActivity.halfMove;
    }

    public void gameOver(Fdn fdn){

        // --- Configuring popup layout to display in the main activity
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_game_over,null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,false);

        // --- Mapping the elements of popup layout to the java objects
        tvGameStatus = popupView.findViewById(R.id.tvGameStatus);
        tvTotalMoves = popupView.findViewById(R.id.tvTotalMoves);
        tvDuration = popupView.findViewById(R.id.tvDuration);
        btnRestart = popupView.findViewById(R.id.btnRestart);
        btnHome = popupView.findViewById(R.id.btnHome);
        btnClose = popupView.findViewById(R.id.btnClose);

        if(checkStatus(fdn)) {
            tvGameStatus.setText("Congratulations you won the match!");
            tvGameStatus.setTextColor(view.getResources().getColor(R.color.green));

            // --- Game over sound
            MediaPlayer.create(view.getContext(),R.raw.win).start();
        }else{
            tvGameStatus.setText("Oops! you lost the match");
            tvGameStatus.setTextColor(view.getResources().getColor(R.color.red));

            // --- Game over sound
            MediaPlayer.create(view.getContext(),R.raw.lose).start();
        }

        // --- Assigning the value to the text views
        tvTotalMoves.setText(halfMove/2+"");
        tvDuration.setText(((duration/60 == 0) ? "" : duration/60 + " min : " ) + duration%60 + " sec");

        // --- Close button
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        // --- Restart button
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(),MainActivity.class));
            }
        });

        // --- Show popup window in the activity
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }

    // --- Validating the win or lose status of the game
    boolean checkStatus(Fdn fdn){
        if(fdn.getFdn().split(" ")[1].charAt(0) == MainActivity.userColor)
            return false;
        return true;
    }
}
