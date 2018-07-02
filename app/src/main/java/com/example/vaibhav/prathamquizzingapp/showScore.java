package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vaibhav on 3/6/18.
 */

public class showScore extends Activity {
    private static final String TAG = "showScore";

    private TextView blah;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);
        Log.d(TAG, "onCreate: Creeeeeeeeeated");

        blah = (TextView) findViewById(R.id.txtFinalScore);

        Intent intent = getIntent();
        String Score  = intent.getStringExtra("Score");
        String[] Ques = intent.getStringArrayExtra("Questions");
        String[] Ans  = intent.getStringArrayExtra("Answers");

        blah.setText(Score);
    }
}
