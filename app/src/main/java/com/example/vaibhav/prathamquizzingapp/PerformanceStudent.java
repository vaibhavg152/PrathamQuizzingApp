package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.example.vaibhav.prathamquizzingapp.utilClasses.PersonListAdapter;
import com.example.vaibhav.prathamquizzingapp.utilClasses.Score;

import java.util.ArrayList;

/**
 * Created by vaibhav on 22/6/18.
 */

public class PerformanceStudent extends Activity {
    private static final String TAG = "PerformanceStudent";

    private ListView listView;
    private ArrayList<Score> scores;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.performance);

        listView = (ListView) findViewById(R.id.listScoresstud);
        scores = new ArrayList<>();

        Intent comingIntent = getIntent();
        String[] finScores = comingIntent.getStringArrayExtra("scores");
        String[] avgScores = comingIntent.getStringArrayExtra("avgScores");
        String[] topics    = comingIntent.getStringArrayExtra("topics");
        String   name      = comingIntent.getStringExtra     ("name");

        int count = topics.length;
        for (int i=0; i<count; i++) {
            scores.add(new Score(topics[i], finScores[i], avgScores[i]));
        }
        PersonListAdapter adapter = new PersonListAdapter(this,R.layout.listview_scores,scores);
        listView.setAdapter(adapter);
    }
}
