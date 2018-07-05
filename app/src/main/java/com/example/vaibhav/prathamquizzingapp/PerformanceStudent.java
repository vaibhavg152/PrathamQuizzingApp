package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
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

        scores.add(new Score("plant","45.8","68.9"));
        scores.add(new Score("animal","65.7","78.6"));
        scores.add(new Score("insect","56.4","54.3"));

        PersonListAdapter adapter = new PersonListAdapter(this,R.layout.listview_scores,scores);
        listView.setAdapter(adapter);
    }
}
