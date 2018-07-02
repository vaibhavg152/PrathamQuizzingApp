package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.Random;

/**
 * Created by vaibhav on 4/6/18.
 */

public class QuizActivity extends Activity {
    private static final String TAG = "QuizActivity";

    private Button       btnA,btnB,btnC,btnD;
    private TextView     txtQues,txtScore;
    private ImageView    imageView;
    private MediaPlayer  mediaPlayer;
    private Animation    animation;
    final String         pathQ = getResources().getString(R.string.pathQuizzes);
    final String         pathU = getResources().getString(R.string.pathUser);
    private String       userId,studentID,quizClass,title,subject;
    private String[]     Question,option_A,option_B,option_C,option_D,correctAns,correctAnsToast, incorrectToast;
    private boolean      isCorrect = false,signedIn;
    private boolean[]    hasImage,hasAudio;
    private int          score=0,q_no=1,teacherScore[],teacherScoreOutof[],numQues=0;
    private ArrayList<Integer> incorrectAns;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Log.d(TAG, "onCreate: ");

        //initialize the members
        btnA        = (Button)    findViewById(R.id.btnA);
        btnB        = (Button)    findViewById(R.id.btnB);
        btnC        = (Button)    findViewById(R.id.btnC);
        btnD        = (Button)    findViewById(R.id.btnD);
        imageView   = (ImageView) findViewById(R.id.imageQuiz);
        txtQues     = (TextView)  findViewById(R.id.Question);
        txtScore    = (TextView)  findViewById(R.id.txtScore);

        mediaPlayer = MediaPlayer.create(this,R.raw.song);
        animation   = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sample_animation);
        userId = myapp.getUserId();


        final Intent quizIntent = getIntent();
        quizClass = myapp.getCls();
        title     = myapp.getQuizTitle();
        subject   = myapp.getSubject();
        correctAnsToast   = getResources().getStringArray(R.array.CorrectAnsToasts);
        incorrectToast = getResources().getStringArray(R.array.IncorrectAnsToasts);
        incorrectAns = new ArrayList<>();
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
    }

    private void initialize() {
        Log.d(TAG, "initialize: ");

        getNumberofQuestions();
        getAlltheQuestions();
        getTeacherScores();
        if (teacherScore.length==0) toastMessage("Could not get the Scores :( Try again!");
        setQuestions();

    }

    private void getNumberofQuestions() {

        String pathf; File myDir;
        do {
            numQues++;
            pathf = pathQ + quizClass + "/" + subject + "/" + title + "/Q" + numQues;
            Log.d(TAG, "initialize: "+pathf);
            myDir = new File(pathf);
        }while (myDir.exists());

        numQues--;
        Log.d(TAG, "onCreate: "+numQues);

        if (numQues==0){
            toastMessage("Quiz has no Questions!!");
        }

    }

    private void getAlltheQuestions() {

        Log.d(TAG, "getAlltheQuestions: ");
        Question   = new String [numQues];
        option_A   = new String [numQues];
        option_B   = new String [numQues];
        option_C   = new String [numQues];
        option_D   = new String [numQues];
        correctAns = new String [numQues];
        hasImage   = new boolean[numQues];
        hasAudio   = new boolean[numQues];

        for (int i = 0; i < numQues; i++){

            Log.d(TAG, "getAlltheQuestions: "+i);
            int j=i+1;
            String pathf = pathQ + "/" + quizClass + "/" + subject + "/"  + title + "/Q" + j + "/ques.txt";
            File file = new File(pathf);
            String[] readQues = readData(file);

            if (readQues.length==0){
                toastMessage("Error! :(");
                return;
            }

            correctAns[i] = readQues[5];
            Question[i]   = readQues[0];
            option_A[i]   = readQues[1];
            option_B[i]   = readQues[2];
            option_C[i]   = readQues[3];
            option_D[i]   = readQues[4];
            hasAudio[i]   = readQues[6].equals("true");
            hasImage[i]   = readQues[7].equals("true");
        }

    }

    public void getTeacherScores() {
        Log.d(TAG, "getTeacherScore: ");

        int[] result = new int[numQues];
        if (!signedIn)
            return;

        File myDir = new File(pathU + subject + "/" + title);
        if (!myDir.exists()) return;
        File file = new File(myDir, "Scores.txt");
        String[] teacherScores = readData(file);
        if (teacherScores.length==0)
            return;

        for (int i = 0; i < numQues; i++) {
            try {
                result[i] = Integer.parseInt(teacherScores[i+1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
        }

        teacherScore      = new int[numQues];
        teacherScoreOutof = new int[numQues];

        if (result.length!=1) {
            for (int i = 0; i < result.length - 1; i += 2) {
                teacherScore[i / 2]      = result[i];
                teacherScoreOutof[i / 2] = result[i + 1];
            }
        }
    }

    private void setQuestions() {
        Log.d(TAG, "setQuestions: ");

        txtQues.setText("Question: " +Question[q_no-1]);
        btnA.setText("A. " +option_A[q_no-1]);
        btnB.setText("B. " +option_B[q_no-1]);
        btnC.setText("C. " +option_C[q_no-1]);
        btnD.setText("D. " +option_D[q_no-1]);

        if (hasImage[q_no-1])
            loadImage();
    }

    private void loadImage() {
        Log.d(TAG, "loadImage: ");

        try {

            String photoPath = pathQ + quizClass + "/" + subject + "/" + title + "/Q" + q_no + "/image.jpeg";

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);

            imageView.setImageBitmap(bitmap);

        }catch (Exception e){
            toastMessage("Error loading image");
            e.printStackTrace();
        }
    }

    private void choseAns(Boolean isCorrec){

        Log.d(TAG, "choseAns: ");

        final Dialog dialog = new Dialog(QuizActivity.this);
        dialog.setContentView(R.layout.dialog_show_question);

        Button next;
        ImageView imageClip;
        TextView tvIscorrect,tvCorrectAns;

        imageClip    = (ImageView) dialog.findViewById(R.id.imgQuizClip);
        next         = (Button)    dialog.findViewById(R.id.btnNextQues);
        tvIscorrect  = (TextView)  dialog.findViewById(R.id.tvIsCorrect);
        tvCorrectAns = (TextView)  dialog.findViewById(R.id.tvCorrectAns);

        isCorrect = isCorrec;

        if (!userId.equals("null")) {
            teacherScoreOutof[q_no-1]++;
            if (isCorrect)  teacherScore[q_no-1]++;
        }

        Random random = new Random();
        if (isCorrect){
            int i = random.nextInt(correctAnsToast.length);
            tvIscorrect.setText(correctAnsToast[i]);
            score++;
            imageClip.setImageResource(R.drawable.clipart_star_award);

        }else {
            int i = random.nextInt(incorrectToast.length);
            if (i>0) i--;
            tvIscorrect.setText(incorrectToast[i]);
            tvCorrectAns.setText(correctAns[q_no-1]);

            incorrectAns.add(q_no-1);
        }

        txtScore.setText("Score: "+score);
        mediaPlayer.start();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQues();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateQues() {
        Log.d(TAG, "updateQues: ");

        mediaPlayer.stop();

        q_no++;
        btnD.setVisibility(View.VISIBLE);
        btnC.setVisibility(View.VISIBLE);
        btnB.setVisibility(View.VISIBLE);
        btnA.setVisibility(View.VISIBLE);

        if (q_no>numQues) FinishQuiz();
        else setQuestions();
    }

    private void FinishQuiz() {

        Log.d(TAG, "FinishQuiz: ");
        toastMessage("Quiz has ended :)");

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

        Intent intent = new Intent(QuizActivity.this, showScore.class);
        intent.putExtra("Questions",incorrectAns.toArray());
        intent.putExtra("Score", s);
        startActivity(intent);
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
        FileInputStream fis;

        String[] empty = new String[0];
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toastMessage("Error! File not found :(");
            return empty;
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
            return empty;
        }

        try
        {
            fis.getChannel().position(0);
        }
        catch (IOException e) {
            e.printStackTrace();
            return empty;
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
            return empty;
        }
        return array;

    }

}
