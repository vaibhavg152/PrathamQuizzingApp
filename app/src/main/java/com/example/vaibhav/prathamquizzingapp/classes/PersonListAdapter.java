package com.example.vaibhav.prathamquizzingapp.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vaibhav.prathamquizzingapp.R;
import java.util.ArrayList;

/**
 * Created by vaibhav on 23/6/18.
 */

public class PersonListAdapter extends ArrayAdapter<Score> {

    private Context mcontext;
    private int mresource;

    public PersonListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Score> objects) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String topic = getItem(position).getTopic();
        String score = getItem(position).getScore();
        String avgScore = getItem(position).getAvgScore();

        Score score1 = new Score(topic,score,avgScore);

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mresource,parent,false);

        TextView tvTopic = (TextView) convertView.findViewById(R.id.txtTopic);
        TextView tvScore = (TextView) convertView.findViewById(R.id.txtDispScore);
        TextView tvAvgScore = (TextView) convertView.findViewById(R.id.txtAvgScore);

        tvAvgScore.setText(avgScore);
        tvScore.setText(score);
        tvTopic.setText(topic);

        return convertView;
    }
}
