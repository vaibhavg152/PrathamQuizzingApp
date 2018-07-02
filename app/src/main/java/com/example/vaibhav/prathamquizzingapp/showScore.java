package com.example.vaibhav.prathamquizzingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by vaibhav on 3/6/18.
 */

public class showScore extends AppCompatActivity {
    private static final String TAG = "showScore";

    private TextView blah;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_score_screen);
        Log.d(TAG, "onCreate: Creeeeeeeeeated");

        blah=(TextView) findViewById(R.id.txtFinalScore);

        Intent intent = getIntent();
        String Score = intent.getStringExtra("Score");
        blah.setText(Score);
    }
}
