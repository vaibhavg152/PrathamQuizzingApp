package com.example.vaibhav.prathamquizzingapp.utilClasses;

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
import java.util.List;

/**
 * Created by vaibhav on 4/7/18.
 */

public class QuizReportAdapter extends ArrayAdapter<QuizReport> {

    private Context mcontext;
    private int     mResource;

    public QuizReportAdapter(@NonNull Context context, int resource, @NonNull ArrayList<QuizReport> objects) {
        super(context, R.layout.listview_quiz_report, resource, objects);
        mcontext  = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String ques = getItem(position).getQuestion();
        String ans  = getItem(position).getCorrectAns();
        int quesNum = getItem(position).getQuesNum();

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate( mResource, parent,false);

        QuizReport report = new QuizReport(quesNum,ques,ans);

        TextView tvNum,tvQues,tvAns;
        tvAns  = (TextView) convertView.findViewById(R.id.tvListCorrectAns);
        tvQues = (TextView) convertView.findViewById(R.id.tvQuestion);
        tvNum  = (TextView) convertView.findViewById(R.id.tvQnum);

        tvNum .setText("Q"+quesNum);
        tvQues.setText(ques);
        tvAns .setText(ans);

        return convertView;
    }
}
