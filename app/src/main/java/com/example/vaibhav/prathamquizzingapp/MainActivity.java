package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends Activity implements Serializable{

    private static final String TAG = "MainActivity";

    private Button login,btnAdmin,practice,btnDownload;
    private ImageView imageLogo;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private final String pathQ = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/Quizzes/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Created");

        login     = (Button)    findViewById(R.id.btnlogin);
        btnAdmin  = (Button)    findViewById(R.id.btnAdmin);
        btnDownload = (Button)  findViewById(R.id.btnDownloadMain);
        practice  = (Button)    findViewById(R.id.btnPractice);
        imageLogo = (ImageView) findViewById(R.id.imageViewmain);

        imageLogo.setImageResource(R.drawable.logo);

        mAuth=FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null) {
                    Log.d(TAG, "onAuthStateChanged: signed in :" + user.getUid());
                    toastMessage("Successfully signed in as "+user.getEmail());
                    myapp.setUserId(user.getUid());
                    Intent intent = new Intent(MainActivity.this,SuperUser.class);
                    intent.putExtra("user",user.getUid());
                    startActivity(intent);
                }
                else {
                    toastMessage("Signed out. :)");
                    Log.d(TAG, "onAuthStateChanged: signed out");
                    myapp.setUserId("null");
                }
            }
        };

        mAuth.addAuthStateListener(authStateListener);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: going to the login screen");

                Intent intent;
                if (!myapp.getUserId().equals("null"))
                    intent = new Intent(MainActivity.this,HomePage.class);
                else
                    intent = new Intent(MainActivity.this,logIn.class);
                intent.putExtra("user",false);
                startActivity(intent);

            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,logIn.class);
                intent.putExtra("user",true);
                startActivity(intent);
            }
        });

        practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClass("");
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClass("download");
            }
        });

    }

    private void selectClass(final String type) {

        String[] allClasses = getResources().getStringArray(R.array.allClasses);
        ArrayList<String> temp = new ArrayList<>();
        File myDir;
        String finalPath;
        final String[] array;

        if (!type.equals("download")) {
            for (String s : allClasses) {
                finalPath = pathQ + s;
                myDir = new File(finalPath);
                Log.d(TAG, "selectClass: "+finalPath);
                if (myDir.exists()) temp.add(s);
            }
            array = new String[temp.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = temp.get(i);
            }
        }
        else
            array = allClasses;

        Log.d(TAG, "selectClass: "+array.length);

        if (array.length==0){
            toastMessage("No Quizzes downloaded");
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Class");

        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curClass = array[i];
                myapp.setCls(curClass);
                Log.d(TAG, "onClick: "+curClass);
                if (type.equals("download"))
                {
                    Intent intent = new Intent(MainActivity.this,DownloadQuizzes.class);
                    startActivity(intent);
                }
                else
                    selectSubject();
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

    private void selectSubject() {

        final String cls = myapp.getCls();
        Log.d(TAG, "selectSection: ");
        final String[] subjects = getResources().getStringArray(R.array.Subjects);

        ArrayList<String> temp = new ArrayList<>();
        File myDir;
        String finalPath;

        for (String s:subjects) {
            finalPath = pathQ +cls+"/"+s;
            myDir = new File(finalPath);
            if (myDir.exists()) temp.add(s);
        }
        final String[] array = new String[temp.size()];
        for (int i =0 ;i<array.length; i++){
            array[i] = temp.get(i);
        }

        if (array.length==0){
            toastMessage("No Quizzes downloaded for class"+cls);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.Theme_AppCompat_Dialog_Alert);

        builder.setTitle("Select a subject");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(array, -1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curSubject = array[i];
                myapp.setSubject(curSubject);
                Log.d(TAG, "onClick: "+curSubject);
                selectTopic(curSubject);
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

    private void selectTopic(String subject) {

        final String cls = myapp.getCls();
        File myDir = new File(pathQ+cls+"/"+subject);
        if (!myDir.exists()){

            toastMessage("No quizzes doenloaded for subject " + subject + "in class " + cls);
            Log.d(TAG, "onCreate: "+pathQ+cls+"/"+subject);
            return;
        }
        File file = new File(myDir,"topics.txt");
        final String[] topics = readData(file);

        int count = topics.length;
        if (count==0) {
            toastMessage("No quizzes doenloaded for subject " + subject + "in class " + cls);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Topic");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(topics, -1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curTitle = topics[i];
                myapp.setQuizTitle(curTitle);

                dialogInterface.dismiss();
                Intent intent = new Intent(MainActivity.this,QuizActivity.class);
                intent.putExtra("signedIn",false);
                intent.putExtra("childId","");
                startActivity(intent);
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

    private String[] readData(File file) {
        FileInputStream fis;
        String[] result = new String[0];
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null)
            mAuth.removeAuthStateListener(authStateListener);
    }

}
