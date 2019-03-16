package com.example.game2048;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements CallBackInterface{

    private TextView mScoreTx;
    private GameLayout mGameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScoreTx = findViewById(R.id.socre);
        mGameLayout = findViewById(R.id.game_layout);
        mGameLayout.setRegister(this);
    }

    @Override
    public void setScore(int score) {
        mScoreTx.setText("Score:" + score);
    }

    @Override
    public void setGameOver() {
        new AlertDialog.Builder(this)
                .setTitle("GAME OVER")
                .setMessage("Do you want to try again?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mGameLayout.reStart();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }
}
