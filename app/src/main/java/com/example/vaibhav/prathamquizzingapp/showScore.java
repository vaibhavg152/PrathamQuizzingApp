package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.QuizReport;
import com.example.vaibhav.prathamquizzingapp.utilClasses.QuizReportAdapter;
import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vaibhav on 3/6/18.
 */

public class showScore extends Activity {
    private static final String TAG = "showScore";

    private TextView textView;
    private ListView listView;
    private Button   btnReset;
    private final String pathU = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/User/";
    private ArrayList<QuizReport> reportArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);
        Log.d(TAG, "onCreate: Created");

        textView = (TextView) findViewById(R.id.txtTitle);
        listView = (ListView) findViewById(R.id.listviewReport);
        btnReset = (Button)   findViewById(R.id.btnReset);
        reportArrayList = new ArrayList<>();

        Intent intent = getIntent();
        final String Score  = intent.getStringExtra("Score");
        String[] Ques = intent.getStringArrayExtra("Questions");
        String[] Ans  = intent.getStringArrayExtra("Answers");
        int[]    Qno  = intent.getIntArrayExtra("Qnumbers");

        int numIncorrect = Ques.length;

        Log.d(TAG, "onCreate: " + numIncorrect);
        for (int i = 0; i < numIncorrect; i++) {
            reportArrayList.add(new QuizReport(Qno[i], "" + Ques[i], "" + Ans[i]));
        }

        QuizReportAdapter adapter = new QuizReportAdapter(this, R.layout.listview_quiz_report, reportArrayList);
        listView.setAdapter(adapter);
        if (!Score.equals("null")) {
            textView.setText("Congratulations!! You scored " + Score + " in the Quiz.:)");
            btnReset.setVisibility(View.INVISIBLE);
        }
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetToZero(true);
            }
        });

    }

    private void resetToZero(boolean isTeacher) {
        if (isTeacher){

            AlertDialog.Builder builder = new AlertDialog.Builder(showScore.this);
            builder.setTitle("Are you sure you want to clear the data for this quiz?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    File myDir,file;
                    String filePath = pathU + myapp.getCls() + "/" + myapp.getSubject() + "/" + myapp.getQuizTitle();
                    myDir = new File(filePath);
                    file = new File(myDir,"Scores.txt");
                    SaveData(file,"");
                    toastMessage("Scores reset to 0.");
                    dialogInterface.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            }
        else {

        }

    }

    private void SaveData(File file, String data) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            try{
                fos.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        finally {
            try { fos.close();}
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            catch (NullPointerException e){
                e.printStackTrace();
                return;
            }
        }
    }

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
