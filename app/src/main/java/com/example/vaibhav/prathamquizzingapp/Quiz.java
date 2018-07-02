package com.example.vaibhav.prathamquizzingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.classes.myapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by vaibhav on 4/6/18.
 */

public class Quiz extends AppCompatActivity {
    private static final String TAG = "Quiz";

    private Button btnA,btnB,btnC,btnD,next;
    private TextView txtQues,txtScore;
    private ImageView imageView,imageClip;
    private MediaPlayer mediaPlayer;
    private Animation animation;
    private String userId,studentID,correctAns,option_A,option_B,option_C,option_D,Question,quizClass,title,subject;
    private int score=0,q_no=1,teacherScore[],teacherScoreOutof[],i=0,numQues=0;
    private final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/Quizzes/";
    private final String pathU = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/User/";
    private boolean isCorrect = false,signedIn;
    private ArrayList<String> correctAnsToast,incorrectAnsToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_screen);
        Log.d(TAG, "onCreate: ");

        //initialize the members
        next        = (Button)    findViewById(R.id.btnNextQ);
        btnA        = (Button)    findViewById(R.id.btnA);
        btnB        = (Button)    findViewById(R.id.btnB);
        btnC        = (Button)    findViewById(R.id.btnC);
        btnD        = (Button)    findViewById(R.id.btnD);
        imageView   = (ImageView) findViewById(R.id.imageQuiz);
        imageClip   = (ImageView) findViewById(R.id.imageClip);
        txtQues     = (TextView)  findViewById(R.id.Question);
        txtScore    = (TextView)  findViewById(R.id.txtScore);

        mediaPlayer = MediaPlayer.create(this,R.raw.song);
        animation   = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sample_animation);
        userId = myapp.getUserId();


        //get the quiz details from the intent
        final Intent quizIntent = getIntent();
        quizClass = myapp.getCls();
        title     = myapp.getQuizTitle();
        subject   = myapp.getSubject();
        studentID = quizIntent.getStringExtra("childId");
        signedIn  = quizIntent.getBooleanExtra("user",false);

        initialize();

        //onclick listeners for the options
        btnA.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                clickedA(view);
            }

        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                clickedB(view);
            }

        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                clickedC(view);
            }

        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                clickedD(view);
            }

        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQues();
            }
        });
    }

    private void initialize() {

        initToastArrays();

        subject = myapp.getSubject();

        String pathf; File myDir;
        do {
            numQues++;
            pathf = path + quizClass + "/" + subject + "/" + title + "/Q" + numQues;
            Log.d(TAG, "initialize: "+pathf);
            myDir = new File(pathf);
        }while (myDir.exists());
        numQues--;
        Log.d(TAG, "onCreate: "+numQues);

        if (numQues==0){
            Toast.makeText(Quiz.this,"Quiz has no Questions!!",Toast.LENGTH_SHORT).show();
        }

        int[] result = getTeacherScores();

        teacherScore = new int[numQues];
        teacherScoreOutof = new int[numQues];

        if (result.length!=1) {
            for (int i = 0; i < result.length - 1; i += 2) {
                teacherScore[i / 2] = result[i];
                teacherScoreOutof[i / 2] = result[i + 1];
            }
        }

        pathf = path + "/" + quizClass + "/" + subject + "/"  + title + "/Q1/ques.txt";
        getNSetQuestions(pathf,title);
    }

    private void setQuestions() {
        Log.d(TAG, "setQuestions: ");

        txtQues.setText("Question: " +Question);
        btnA.setText("A. " +option_A);
        btnB.setText("B. " +option_B);
        btnC.setText("C. " +option_C);
        btnD.setText("D. " +option_D);
    }

    private void getNSetQuestions(String pathf,String title) {
        Log.d(TAG, "getNSetQuestions: ");

        File file = new File(pathf);
        if (!file.exists())
            FinishQuiz();

        String[] readQues = readData(file);
        if (readQues.equals(null)){
            toastMessage("Error! :(");
            return;
        }
        Question = readQues[0];
        option_A = readQues[1];
        option_B = readQues[2];
        option_C = readQues[3];
        option_D = readQues[4];
        correctAns = readQues[5];
        String hI,hA;
        hA = readQues[6];
        hI = readQues[7];

        if (hI.equals("true"))
            loadImage(title);

        setQuestions();
    }

    private void updateQues() {
        Log.d(TAG, "updateQues: ");

        mediaPlayer.stop();

        if (!userId.equals("null")) {
            teacherScoreOutof[q_no-1]++;
            if (isCorrect)  teacherScore[q_no-1]++;
        }

        q_no++;
        btnD.setVisibility(View.VISIBLE);
        btnC.setVisibility(View.VISIBLE);
        btnB.setVisibility(View.VISIBLE);
        btnA.setVisibility(View.VISIBLE);
        imageClip.setImageURI(null);

        if (q_no>numQues) FinishQuiz();
        else {
            String pathf = path + "/" + quizClass + "/" + subject + "/"  + title + "/Q" + q_no + "/ques.txt";
            getNSetQuestions(pathf, title);
        }
    }

    private void initToastArrays() {
        Log.d(TAG, "initToastArrays: ");

        correctAnsToast = new ArrayList<>();
        incorrectAnsToast = new ArrayList<>();

        correctAnsToast.add("Well Done :)");
        correctAnsToast.add("Great job :)");
        correctAnsToast.add("Keep going :)");
        correctAnsToast.add("Keep it up! :)");
        correctAnsToast.add("Smart! :)");
        correctAnsToast.add("Correct Answer :)");

        incorrectAnsToast.add("Try again :(");
        incorrectAnsToast.add("Nice try :(");
        incorrectAnsToast.add("Dont be sad :(");
        incorrectAnsToast.add("Missed this time :(");

    }

    private void choseAns(Boolean isCorrec){
        Log.d(TAG, "choseAns: ");

        isCorrect = isCorrec;
        if (isCorrect){
            i%=correctAnsToast.size();
            if (i>0) i--;
            toastMessage(correctAnsToast.get(i));
            score++;

            imageClip.setImageResource(R.drawable.clipart_star_award);
        }else {
            i%=incorrectAnsToast.size();
            if (i>0) i--;
            toastMessage(incorrectAnsToast.get(i));
        }
        i+=11;
        txtScore.setText("Score: "+score);
        mediaPlayer.start();
    }

    private void FinishQuiz() {

        Log.d(TAG, "FinishQuiz: ");
        Toast.makeText(Quiz.this, "Quiz has ended", Toast.LENGTH_SHORT).show();

        if (numQues==0) numQues++;
        String s=""+score+"/"+numQues;
        double doubleScore = 100*score/numQues;

        //Store the Student's score
        if (signedIn){

            Log.d(TAG, "FinishQuiz: signedIn");
            String pathScore = pathU + myapp.getSchool()+"/"+myapp.getCls()+"/"+myapp.getSec()+"/"+studentID+"/"+subject;
            File myDir = new File(pathScore);
            if (!myDir.exists()) myDir.mkdirs();
            File file = new File(myDir,"Scores.txt");
            SaveData(file,title+"\n"+s+"\n"+doubleScore+"\n",true);
        }
        //end

        //Store the teacher's scores
        if (signedIn) {
            String data = title+"\n";
            for (int i =0;i< teacherScore.length;i++) {
                data += teacherScore[i] + "\n" + teacherScoreOutof[i] + "\n";
            }

            File myDir = new File(pathU + subject + "/" + title);
            if (!myDir.exists()) myDir.mkdirs();
            File file = new File(myDir, "Scores.txt");
            SaveData(file, data,false);
        }//end

        Intent intent = new Intent(Quiz.this, showScore.class);
        intent.putExtra("Score", s);
        startActivity(intent);
    }

    private void SaveData(File file, String data, boolean append) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file,append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toastMessage("Error! File Not found :(");
            return;
        }

        try {
            try{
                fos.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                toastMessage("Error! :(");
                return;
            }
        }
        finally {
            try { fos.close();}
            catch (IOException e) {
                e.printStackTrace();
                toastMessage("Error! :(");
                return;
            }
            catch (NullPointerException e){
                e.printStackTrace();
                toastMessage("Error! :(");
                return;
            }
        }
    }

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    private String[] readData(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toastMessage("Error! File not found :(");
            return null;
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String test;
        int anzahl=0;
        try
        {
            while ((test=br.readLine()) != null) anzahl++;
        }
        catch (IOException e) {
            e.printStackTrace();
            toastMessage("Error! :(");
            return null;
        }

        try
        {
            fis.getChannel().position(0);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String[] array = new String[anzahl];

        String line;
        int i = 0;
        try
        {
            while((line=br.readLine())!=null)
            {
                array[i] = line;
                i++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return array;

    }

    private void loadImage(String title) {
        Log.d(TAG, "loadImage: ");

        try {

            String photoPath = path + quizClass + "/" + subject + "/" + title + "/Q" + q_no + "/image.jpeg";

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);

            imageView.setImageBitmap(bitmap);

        }catch (Exception e){

            Toast.makeText(Quiz.this,"Error loading image",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void clickedA(View view) {

        Log.d(TAG, "clickedA: ");
        choseAns(option_A.equals(correctAns));

        btnC.setVisibility(view.INVISIBLE);
        btnB.setVisibility(view.INVISIBLE);
        btnD.setVisibility(view.INVISIBLE);
    }

    private void clickedB(View view) {
        Log.d(TAG, "clickedB: ");
        choseAns(option_B.equals(correctAns));

        btnC.setVisibility(view.INVISIBLE);
        btnA.setVisibility(view.INVISIBLE);
        btnD.setVisibility(view.INVISIBLE);
    }

    private void clickedC(View view) {
        Log.d(TAG, "clickedC: ");
        choseAns(option_C.equals(correctAns));

        btnB.setVisibility(view.INVISIBLE);
        btnA.setVisibility(view.INVISIBLE);
        btnD.setVisibility(view.INVISIBLE);
    }

    private void clickedD(View view) {
        Log.d(TAG, "clickedD: ");
        choseAns(option_D.equals(correctAns));

        btnC.setVisibility(view.INVISIBLE);
        btnA.setVisibility(view.INVISIBLE);
        btnB.setVisibility(view.INVISIBLE);
    }

    public int[] getTeacherScores() {

        Log.d(TAG, "getTeacherScore: ");

        int[] scores = new int[numQues];
        for (int i=0;i<numQues;i++)
            scores[i]=0;
        if (!signedIn)
            return scores;

        File myDir = new File(pathU + subject + "/" + title);
        if (!myDir.exists()) return scores;
        File file = new File(myDir, "Scores.txt");
        String[] teacherScores = readData(file);
        if (teacherScores.length==0)
             return scores;

        for (int i = 0; i < numQues; i++) {
            try {
                scores[i] = Integer.parseInt(teacherScores[i+1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return scores;
            }
        }
        return scores;

    }

}
