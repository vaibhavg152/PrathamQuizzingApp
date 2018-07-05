package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;

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

    private Button       btnA,btnB,btnC,btnD,btnPlay;
    private TextView     txtQues,txtScore;
    private ImageView    imageView;
    private MediaPlayer  mediaPlayer,audioPlayer;
    private Animation    animation;
    final String         pathQ = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/Quizzes/";
    final String         pathU = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/User/";
    private String       userId,studentID,quizClass,title,subject;
    private String[]     Question,option_A,option_B,option_C,option_D,correctAns,correctAnsToast, incorrectToast;
    private boolean      isCorrect = false,signedIn;
    private boolean[]    hasImage,hasAudio;
    private int          score=0,q_no=1,teacherScore[],teacherScoreOutof[],numQues=0;
    private ArrayList<Integer> incorrectAnsIndex;

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
        btnPlay     = (Button)    findViewById(R.id.btnPlayAudio);
        imageView   = (ImageView) findViewById(R.id.imageQuiz);
        txtQues     = (TextView)  findViewById(R.id.Question);
        txtScore    = (TextView)  findViewById(R.id.txtScore);

        animation   = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sample_animation);
        userId = myapp.getUserId();
        mediaPlayer = MediaPlayer.create(this,R.raw.song);
        audioPlayer = new MediaPlayer();
        audioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onPrepared: ");
                mediaPlayer.start();
            }
        });


        quizClass = myapp.getCls();
        title     = myapp.getQuizTitle();
        subject   = myapp.getSubject();
        correctAnsToast   = getResources().getStringArray(R.array.CorrectAnsToasts);
        incorrectToast = getResources().getStringArray(R.array.IncorrectAnsToasts);
        incorrectAnsIndex = new ArrayList<>();

        final Intent quizIntent = getIntent();
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
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    audioPlayer.prepare();
                } catch (Exception e) {
                    btnPlay.setEnabled(false);
                    toastMessage("Audio can't be played. :(");
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialize() {
        Log.d(TAG, "initialize: ");

        getNumberofQuestions();
        getAlltheQuestions();
        getTeacherScores();
        if (teacherScore == null) toastMessage("Could not get the Scores :( Try again!");
        if (numQues!=0) setQuestions();
        else {
            toastMessage("Quiz has no questions");
            finish();
        }

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

//            correctAns[i] = readQues[5];
            Question[i]   = readQues[0];
            option_A[i]   = readQues[1];
            option_B[i]   = readQues[2];
            option_C[i]   = readQues[3];
            option_D[i]   = readQues[4];
            hasAudio[i]   = readQues[6].equals("true");
            hasImage[i]   = readQues[7].equals("true");

            Log.d(TAG, "getAlltheQuestions: "+readQues[5]);
            if (readQues[5].equals("A"))
                correctAns[i] = option_A[i];
            else if (readQues[5].equals("B"))
                correctAns[i] = option_B[i];
            else if (readQues[5].equals("C"))
                correctAns[i] = option_C[i];
            else
                correctAns[i] = option_D[i];

            Log.d(TAG, "getAlltheQuestions: "+correctAns[i]);
        }

    }

    private void getTeacherScores() {
        Log.d(TAG, "getTeacherScore: ");

        teacherScoreOutof = new int[numQues];
        teacherScore      = new int[numQues];

        for (int i = 0; i < numQues; i++) {
            teacherScore[i] = 0;
            teacherScoreOutof[i] = 0;
        }
        Log.d(TAG, "getTeacherScores: all zero");

        int[] result = new int[numQues];
        if (!signedIn) {
            Log.d(TAG, "getTeacherScores: not signed in");
            return;
        }

        String finalPath = pathU + quizClass + "/" + subject + "/" + title;
        File myDir = new File(finalPath);
        if (!myDir.exists()) {
            Log.d(TAG, "getTeacherScores: "+finalPath);
            return;
        }

        File file = new File(myDir, "Scores.txt");
        String[] teacherScores = readData(file);

        int count = teacherScores.length;
        Log.d(TAG, "getTeacherScores: "+count);

        if (count < 4) {
            Log.d(TAG, "getTeacherScores: less than 1");
            return;
        }

        int j=0;
        for (int i = 2; i < count-1; i+=3) {

            try {
                Log.d(TAG, "getTeacherScores: array out of"+teacherScores[i+1]);
                teacherScoreOutof[j] = Integer.parseInt(teacherScores[i+1]);
                Log.d(TAG, "getTeacherScores: final out of"+teacherScoreOutof[j]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                teacherScoreOutof[j] = 0;
            }

            try {
                Log.d(TAG, "getTeacherScores: array"+teacherScores[i]);
                teacherScore[j] = Integer.parseInt(teacherScores[i]);
                Log.d(TAG, "getTeacherScores: final"+teacherScore[j]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                teacherScore[j] = 0;
            }

            j++;
        }

    }

    private void setQuestions() {
        Log.d(TAG, "setQuestions: ");

        txtQues.setText("Q: " +Question[q_no-1]);
        btnA.setText("A. " +option_A[q_no-1]);
        btnB.setText("B. " +option_B[q_no-1]);
        btnC.setText("C. " +option_C[q_no-1]);
        btnD.setText("D. " +option_D[q_no-1]);

        if (hasImage[q_no-1])
            loadImage();
        if (hasAudio[q_no-1])
            loadAudio();
    }

    private void loadAudio() {

        Log.d(TAG, "loadAudio: ");

        String downloads = pathQ + "/" + quizClass + "/" + subject + "/"  + title + "/Q" + q_no + "/audio.mp3";
        File file = new File(downloads);
        if (!file.exists()) {
            hasAudio[q_no-1] = false;
            toastMessage("Audio is not Downloaded");
            return;
        }

        Log.d(TAG, "playAudio: "+downloads);

        try {
            FileInputStream fis = new FileInputStream(file);
            audioPlayer.setDataSource(fis.getFD());
            btnPlay.setEnabled(true);

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void choseAns(){

        Log.d(TAG, "choseAns: ");

        final Dialog dialog = new Dialog(QuizActivity.this);
        dialog.setContentView(R.layout.dialog_show_question);

        Button next;
        ImageView imageClip;
        TextView tvIscorrect;

        imageClip    = (ImageView) dialog.findViewById(R.id.imgQuizClip);
        next         = (Button)    dialog.findViewById(R.id.btnNextQues);
        tvIscorrect  = (TextView)  dialog.findViewById(R.id.tvIsCorrect);

        Log.d(TAG, "choseAns: "+isCorrect);

        if (!userId.equals("null") && teacherScoreOutof!=null) {
            teacherScoreOutof[q_no-1]++;
            if (isCorrect)  teacherScore[q_no-1]++;
            Log.d(TAG, "choseAns: " + teacherScoreOutof[q_no-1] + teacherScore[q_no-1]);
        }

        Random random = new Random();
        if (isCorrect){
            int i = random.nextInt(correctAnsToast.length);
            tvIscorrect.setText("Correct!! "+correctAnsToast[i]);
            score++;
            imageClip.setImageResource(R.drawable.thestar);

        }else {
            int i = random.nextInt(incorrectToast.length);
            if (i>0) i--;
            tvIscorrect.setText("Incorrect!! "+incorrectToast[i]);
            incorrectAnsIndex.add(q_no-1);
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

            String data = title + "\n" + score + "\n" + numQues + "\n";
            String filePath = pathU + quizClass + "/" + subject;
            File myDir = new File(filePath);
            if (!myDir.exists()) myDir.mkdirs();

            File file = new File(myDir, "Scores.txt");
            SaveData(file, data,true);

            data = title+"\n";
            for (int i =0;i< teacherScore.length;i++) {
                data += Question[i] + "\n" + teacherScore[i] + "\n" + teacherScoreOutof[i] + "\n";
            }

            filePath += "/" + title;
            myDir = new File(filePath);
            if (!myDir.exists()) myDir.mkdirs();
            file = new File(myDir, "Scores.txt");
            SaveData(file, data,false);
        }//end

        int numIncorrect = incorrectAnsIndex.size();
        String[] incorrectAns  = new String[numIncorrect];
        String[] incorrectQues = new String[numIncorrect];
        int[]    quesNums      = new int   [numIncorrect];
        for (int i =0; i<numIncorrect;i++){
            quesNums[i]      = incorrectAnsIndex.get(i)+1;
            incorrectAns[i]  = correctAns[incorrectAnsIndex.get(i)];
            incorrectQues[i] = Question  [incorrectAnsIndex.get(i)];
        }

        Intent intent = new Intent(QuizActivity.this, showScore.class);
        intent.putExtra("Qnumbers",quesNums);
        intent.putExtra("Questions",incorrectQues);
        intent.putExtra("Answers",incorrectAns);
        intent.putExtra("Score", s);
        startActivity(intent);
        finish();
    }

    private void clickedA(View view) {

        isCorrect = correctAns[q_no-1].equals(option_A[q_no-1]);
        Log.d(TAG, "clickedA: "+isCorrect);
        choseAns();
        btnC.setVisibility(view.INVISIBLE);
        btnB.setVisibility(view.INVISIBLE);
        btnD.setVisibility(view.INVISIBLE);
    }

    private void clickedB(View view) {

        isCorrect = correctAns[q_no-1].equals(option_B[q_no-1]);
        Log.d(TAG, "clickedB: "+isCorrect);
        choseAns();

        btnC.setVisibility(view.INVISIBLE);
        btnA.setVisibility(view.INVISIBLE);
        btnD.setVisibility(view.INVISIBLE);
    }

    private void clickedC(View view) {
        isCorrect = correctAns[q_no-1].equals(option_C[q_no-1]);
        Log.d(TAG, "clickedC: "+isCorrect);
        choseAns();

        btnB.setVisibility(view.INVISIBLE);
        btnA.setVisibility(view.INVISIBLE);
        btnD.setVisibility(view.INVISIBLE);
    }

    private void clickedD(View view) {

        isCorrect = correctAns[q_no-1].equals(option_D[q_no-1]);
        Log.d(TAG, "clickedD: "+isCorrect);
        choseAns();

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
