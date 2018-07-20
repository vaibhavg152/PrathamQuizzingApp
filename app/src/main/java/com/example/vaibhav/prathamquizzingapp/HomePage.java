package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.Sections;
import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by vaibhav on 2/6/18.
 */

public class HomePage extends Activity {

    private static final String TAG = "HomePage";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Button btnUpload, btnDownload, btnStartQuiz, btnSignOut, btnComplete,btnViewScores,btnViewStudents;
    private Boolean isConnected;
    private String userId,school,teacherName;
    private final String pathU = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/User/",
                         pathQ = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/Quizzes/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Log.d(TAG, "onCreate: Created");

        btnUpload      = (Button) findViewById(R.id.btnUploadH);
        btnDownload    = (Button) findViewById(R.id.btnDownloadH);
        btnStartQuiz   = (Button) findViewById(R.id.btnStartQuizH);
        btnSignOut     = (Button) findViewById(R.id.btnSignOut);
        btnComplete    = (Button) findViewById(R.id.btnSyncData);
        btnViewScores  = (Button) findViewById(R.id.btnViewAvgScore);
        btnViewStudents  = (Button) findViewById(R.id.btnViewStudNames);

        school = myapp.getSchool();

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Log.d(TAG, "onCreate: wow");

        getUserData();

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        isConnected = (networkInfo!=null && networkInfo.isConnectedOrConnecting());

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this,Register.class);
                startActivity(intent);
                btnComplete.setVisibility(View.INVISIBLE);
                setBtnVisible();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPratham();
            }
        });

        btnViewStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClass("students");
            }
        });

        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClass("quiz");
            }
        });

        btnViewScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClass("view");
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClass("btnDownload");
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
            }
        });

    }

    private void getUserData() {

        Log.d(TAG, "getUserData: ");
        File myDir = new File(pathU);
        if (!myDir.exists()){
            setInvisible();
            return;
        }
        File file = new File(myDir,"BasicData.txt");
        String[] details = readData(file);
        if (details.length==0){
            toastMessage("!!!Complete the registation first!!!");
            return;
        }

        setBtnVisible();
        btnComplete.setVisibility(View.VISIBLE);
        school = details[0];
        teacherName = details[1];
        int count = details.length-2;
        String[] clas = new String[count];

        Log.d(TAG, "getUserData: "+count);
        for (int i= 0; i< count; i++){
            clas[i] = details[i+2];
            Log.d(TAG, "getUserData: "+clas[i]);
        }

        myapp.setSchool(school);
        myapp.setClses(clas);

        for (String c:clas){
            myDir = new File(pathU + school + "/" + c);
            if (!myDir.exists()) myDir.mkdirs();

            file =new File(myDir,"Sections.txt");
            String[] arraySections = readData(file);
            if (arraySections.length==0){
                setInvisible();
                return;
            }

            ArrayList<String> SectionsList = new ArrayList<>();
            for (int i=0;i<arraySections.length;i++){
                SectionsList.add(arraySections[i]);
            }
            Sections section = new Sections(c);
            section.setSections(SectionsList);
            myapp.addSection(section);
        }

    }

    private void selectClass(final String type) {

        final String[] array = myapp.getClsArray();
        Log.d(TAG, "selectClass: "+array.length);

        if (array.length==0){
            toastMessage("No classes to show!!");
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Class");
        builder.setCancelable(false);

        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curClass = array[i];
                myapp.setCls(curClass);

                dialogInterface.dismiss();
                Log.d(TAG, "onClick: "+curClass);
                if (type.equals("btnDownload")){

                    if (!isConnected){
                        toastMessage("you are not connected to the internet!");
                        return;
                    }
                    Intent intent = new Intent(HomePage.this,DownloadQuizzes.class);
                    startActivity(intent);
                }
                else if (type.equals("view"))
                    selectSubject(type);
                else selectSection(type);
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void selectSection(final String type) {

        final String cls = myapp.getCls();
        Log.d(TAG, "selectSection: ");
        final String[] array = myapp.getSections(cls);

        if (array.length==0){
            toastMessage("No sections added for class " + cls);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a section");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(array, -1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curSection = array[i];
                dialogInterface.dismiss();
                myapp.setSec(curSection);
                Log.d(TAG, "onClick: "+curSection);
                if (type.equals("quiz")){
                    selectSubject("");
                }
                else if (type.equals("students")) {
                    Intent intent = new Intent(HomePage.this,SelectName.class);
                    intent.putExtra("type",true);
                    startActivity(intent);
                }
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectSubject(final String type) {

        Log.d(TAG, "selectSubject: ");
        final String[] array = getResources().getStringArray(R.array.Subjects);

        final AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this,R.style.Theme_AppCompat_Dialog_Alert);

        builder.setTitle("Select a Subject");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(array, -1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curSubject = array[i];
                dialogInterface.dismiss();
                myapp.setSubject(curSubject);
                Log.d(TAG, "onClick: "+curSubject);

                selectTopic(curSubject, type);
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectTopic(String subject, final String type) {

        Log.d(TAG, "selectTopic: view");
        final String cls = myapp.getCls();

        File myDir;
        if (type.equals("view")){
            myDir = new File(pathU + cls + "/" + subject);
            if (!myDir.exists()){
                toastMessage("No data found for this subject");
                return;
            }
        }

        String finalPath = pathQ + cls + "/" + subject;
        myDir = new File(finalPath);
        if (!myDir.exists()){
            toastMessage("No Quizzes found. Download the Quizzes first!");
            Log.d(TAG, "onCreate: "+ finalPath);
            return;
        }

        File file = new File(myDir,"topics.txt");
        final String[] titles = readData(file);

        int count = titles.length;
        if (count==0) {
            toastMessage("Error! :(");
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Topic");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(titles, -1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curTitle = titles[i];
                myapp.setQuizTitle(curTitle);
                Log.d(TAG, "onClick: "+curTitle);
                dialogInterface.dismiss();

                if (type.equals("view"))
                    viewScores();
                else {
                    Intent intent = new Intent(HomePage.this, SelectName.class);
                    startActivity(intent);
                }
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void viewScores() {

        Log.d(TAG, "viewScores: ");
        File myDir,file;
        String filePath = pathU + myapp.getCls() + "/" + myapp.getSubject() + "/" + myapp.getQuizTitle();
        myDir = new File(filePath);
        if (!myDir.exists()){
            toastMessage("No data found for the chosen fields");
            return;
        }
        else {
            file = new File(myDir,"Scores.txt");
            String[] result = readData(file);
            int count = result.length;
            if (count > 2) {
                String[] attempts  = new String[count/3];
                String[] correct   = new String[count/3];
                String[] questions = new String[count/3];
                int[]    qNum = new int[count/3];
                int j=0;
                for (int i = 1; i < count-2; i+=3){
                    qNum[j] = j+1;
                    questions[j] = result[i];
                    correct  [j] = result[i+1] + " correct out of " + result[i+2];
                    attempts [j] = result[i+2];
                    j++;
                }

                Intent intent = new Intent(HomePage.this,showScore.class);
                intent.putExtra("Score","null");
                intent.putExtra("Questions",questions);
                intent.putExtra("Answers"  ,correct);
                intent.putExtra("Qnumbers" ,qNum);
                startActivity(intent);
            }
            else toastMessage("No data stored!");
        }
    }

    private void uploadStatus(String school,String Cls) {


        String dateSt  = new Date().toString();
        Log.d(TAG, "uploadStatus: "+dateSt.length());
        String date    = dateSt.substring(8,10);
        String month   = dateSt.substring(4,7);
        Log.d(TAG, "uploadStatus: "+date+month);
        int year    = new Date().getYear();
        String dateStr = date + "-" + month + "-" + year;

        Log.d(TAG, "uploadStatus: "+dateStr);
        databaseReference.child("Update Report").child(school).child(Cls).setValue(dateStr);

    }

    private void uploadSec(String school, String cls, String sec, String subject) {
        Log.d(TAG, "uploadSec: ");

        String finalPath = pathU + school + "/" + cls + "/" + sec;

        File myDir = new File(finalPath);
        if (!myDir.exists()) {
            return;
        }

        DatabaseReference tempRef = databaseReference.child("users").child(userId).child("Classes").child(cls).child(sec);

        uploadStatus(school, cls + sec);
        uploadStudentScore(tempRef, school, cls, sec, subject);

        Log.d(TAG, "uploadScores: " + finalPath);
    }

    private void verifyPratham(){

        final String key = getResources().getString(R.string.teacherKey);

        final Dialog dialog = new Dialog(HomePage.this);
        dialog.setContentView(R.layout.dialog_text);
        dialog.setTitle("You need to be a pratham teacher to upload data");

        final EditText et  = (EditText) dialog.findViewById(R.id.etDialogText);
        Button btnDone     = (Button)   dialog.findViewById(R.id.btnDialogNumber);
        et.setEnabled(true);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et.setHint("Enter teacher key");
        btnDone.setEnabled(true);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredKey = et.getText().toString().trim();
                if (enteredKey.equals(key)){
                    uploadScores();
                }
                else
                    toastMessage("Incorrect key!!");
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void uploadScores() {

        Log.d(TAG, "uploadScores: ");
        if (!isConnected){
            toastMessage("you are not connected to the internet!");
            return;
        }

        String school = myapp.getSchool();
        String[] clses = myapp.getClsArray();
        DatabaseReference teacherRef = databaseReference.child("users").child(userId).child("Scores");

        for (String cls:clses) {

            String[] subs = getResources().getStringArray(R.array.Subjects);
            for (String subject:subs) {

                uploadTeacherScore(teacherRef,cls,subject);
                toastMessage("Uploaded the teacher Scores. :)");

                String[] sections = myapp.getSections(cls);
                for (String sec : sections) {

                    uploadSec(school, cls, sec, subject);

                }
            }

        }
    }

    private void uploadStudentScore(DatabaseReference reference, String School, String cls, String sec, String subject) {

        int rollNo = 1;String id = School + cls + sec + (rollNo < 10 ? "00" : "0") + rollNo;
        String finalPath = pathU + School + "/" + cls + "/" + sec + "/" + id;

        Log.d(TAG, "uploadStudentScore: "+finalPath);
        File myDir = new File(finalPath);

        while (myDir.exists()) {

            //details
            File file = new File(myDir,"Details.txt");
            String[] result = readData(file);
            int count = result.length;
            if (count!=0) {

                reference.child(id).child("Name").setValue(result[0]);
                reference.child(id).child("Age").setValue(result[1]);
                reference.child(id).child("Gender").setValue(result[2]);
                Log.d(TAG, "uploadStudentScore: user");

                //scores
                file = new File(myDir + "/" + subject,"Scores.txt");
                result = readData(file);
                count = result.length;
                if (result.length!=0) {
                    for (int i = 0; i < count; i += 3) {
                        reference.child(id).child("Scores").child(subject).child(result[i]).child("Score").setValue(result[i + 1]);
                        reference.child(id).child("Scores").child(subject).child(result[i]).child("%Score").setValue(result[i + 2]);
                    }
                }
            }


            rollNo++;
            id = School + cls + sec + (rollNo < 10 ? "00" : "0") + rollNo;
            finalPath = pathU + School + "/" + cls + "/" + sec + "/" + id;
            myDir = new File(finalPath);

        }

        toastMessage("Uploaded all the scores and details. :)");
    }

    private void uploadTeacherScore(DatabaseReference tempRef,String cls, String subject) {

        userId = mAuth.getCurrentUser().getUid();
        databaseReference.child("users").child(userId).child("Name").setValue(teacherName);
        databaseReference.child("users").child(userId).child("School").setValue(school);

        Log.d(TAG, "uploadTeacherScore: ");
        File myDir = new File(pathQ + cls + "/" + subject);
        File file = new File(myDir, "/topics.txt");
        String[] topics = readData(file);

        if (topics.length==0)
            return;

        myDir = new File(pathU + cls + "/" + subject);
        if (myDir.exists()) {

            file  = new File(myDir, "Scores.txt");
            String[] tempScores = readData(file);
            int count = tempScores.length;
            Log.d(TAG, "uploadTeacherScore: "+count);

            if (count > 2) {

                SaveData(file,"");
                for (final String s : topics) {
                    Log.d(TAG, "uploadTeacherScore: " + s);
                    final long[] totalCorrect = {0};
                    final long[] totalAttempts = {0};

                    tempRef = tempRef.child(cls).child(subject);

                    tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(s)) {
                                totalCorrect [0] = dataSnapshot.child(s).child("Correct" ).getValue(long.class);
                                totalAttempts[0] = dataSnapshot.child(s).child("Attempts").getValue(long.class);
                                Log.d(TAG, "onDataChange: "+totalAttempts[0]);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    for (int i = 0; i < count - 2; i += 3) {
                        if (tempScores[i].equals(s))
                        try {
                            totalCorrect [0] += Integer.parseInt(tempScores[i + 1]);
                            totalAttempts[0] += Integer.parseInt(tempScores[i + 2]);
                            Log.d(TAG, "uploadTeacherScore: " + totalAttempts[0] + tempScores[i + 2]);

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    if (totalAttempts[0]!=0) {
                        Log.d(TAG, "uploadTeacherScore: not 0");
                        double avgScore = 100 * totalCorrect[0] / totalAttempts[0];
                        tempRef.child(s).child("Correct").setValue(totalCorrect[0]);
                        tempRef.child(s).child("Attempts").setValue(totalAttempts[0]);
                        tempRef.child(s).child("averageScore").setValue(""+avgScore);
                    }
                }
            }
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

    private String[] readData(File file) {
        FileInputStream fis;
        String[] result = new String[0];
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toastMessage("Error! File not found :(");
            return result;
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
            return result;
        }

        try
        {
            fis.getChannel().position(0);
        }
        catch (IOException e) {
            e.printStackTrace();
            return result;
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
            return result;
        }
        return array;

    }

    private void setBtnVisible() {
        toastMessage("Basic data stored. :)");
        btnUpload      .setVisibility(View.VISIBLE);
        btnDownload    .setVisibility(View.VISIBLE);
        btnStartQuiz   .setVisibility(View.VISIBLE);
        btnViewScores  .setVisibility(View.VISIBLE);

        btnComplete.setVisibility(View.INVISIBLE);
    }

    private void setInvisible() {

        toastMessage("Error! Complete the registration first. :)");
        btnUpload      .setVisibility(View.INVISIBLE);
        btnDownload    .setVisibility(View.INVISIBLE);
        btnStartQuiz   .setVisibility(View.INVISIBLE);
        btnViewScores  .setVisibility(View.INVISIBLE);
    }

}
