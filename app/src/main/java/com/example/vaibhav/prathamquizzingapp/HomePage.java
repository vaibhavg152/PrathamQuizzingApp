package com.example.vaibhav.prathamquizzingapp;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.classes.Sections;
import com.example.vaibhav.prathamquizzingapp.classes.myapp;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by vaibhav on 2/6/18.
 */

public class HomePage extends AppCompatActivity {

    private static final String TAG = "HomePage";

    private Button btnUpload, btnDownload, btnStartQuiz, btnSignOut,btnSyncData,btnAddStudents;
    private FirebaseAuth mAuth;
    private final String pathDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    private final String pathU = pathDownloads + "/Pratham/User/",pathQ = pathDownloads + "/Pratham/Quizzes/";
    private String userId,school;
    private Boolean isConnected;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        Log.d(TAG, "onCreate: Created");

        btnUpload      = (Button) findViewById(R.id.btnUploadH);
        btnDownload    = (Button) findViewById(R.id.btnDownloadH);
        btnStartQuiz   = (Button) findViewById(R.id.btnStartQuizH);
        btnAddStudents = (Button) findViewById(R.id.btnAddStudents);
        btnSignOut     = (Button) findViewById(R.id.btnSignOut);
        btnSyncData    = (Button) findViewById(R.id.btnSyncData);

        school = myapp.getSchool();

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pratham");

        Log.d(TAG, "onCreate: wow");

        getUserData();
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        isConnected = (networkInfo!=null && networkInfo.isConnectedOrConnecting());

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadScores();
            }
        });

        btnSyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncData();
            }
        });

        btnAddStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {selectClass("addStudents");
            }
        });

        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClass("quiz");
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected){
                    toastMessage("you are not connected to the internet!");
                    return;
                }

                selectClass("btnDownload");
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                Intent intent= new Intent(HomePage.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getUserData() {

        Log.d(TAG, "getUserData: ");
        File myDir = new File(pathU);
        if (!myDir.exists()){
            toastMessage("No data found. Download the data first!");
            return;
        }
        File file = new File(myDir,"BasicData.txt");
        String[] details = readData(file);
        if (details.length==0){
            toastMessage("Error! Download the data first:(");
            return;
        }

        school = details[0];
        ArrayList<String> clas = new ArrayList<>();
        for (int i=2;i<details.length;i++){
            clas.add(details[i]);
            Log.d(TAG, "getUserData: "+details[i]);
        }

        myapp.setSchool(school);
        myapp.setClses(clas);

        for (String c:clas){
            myDir = new File(pathU + school + "/" + c);
            if (!myDir.exists()) myDir.mkdirs();

            file =new File(myDir,"Sections.txt");
            String[] arraySections = readData(file);
            if (arraySections.length==0){
                toastMessage("Error! Download the data first:(");
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Class");

        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curClass = array[i];
                myapp.setCls(curClass);
                dialogInterface.dismiss();
                Log.d(TAG, "onClick: "+curClass);
                if (type.equals("btnDownload")){
                    Intent intent = new Intent(HomePage.this,DownloadQuizzes.class);
                    startActivity(intent);
                }
                else selectSection(type);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Class");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(array, -1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curSection = array[i];
                dialogInterface.dismiss();
                myapp.setSec(curSection);
                Log.d(TAG, "onClick: "+curSection);
                if (type.equals("quiz")){
                    selectSubject();
                }
                else enterNumStudents();
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

        Log.d(TAG, "selectSection: ");
        final String[] array = getResources().getStringArray(R.array.Subjects);

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this,R.style.Theme_AppCompat_Dialog_Alert);

        builder.setTitle("Select a Class");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(array, -1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curSubject = array[i];
                dialogInterface.dismiss();
                myapp.setSubject(curSubject);
                Log.d(TAG, "onClick: "+curSubject);
                selectTopic(curSubject);
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
            toastMessage("No Quizzes. Download the Quizzes first!");
            Log.d(TAG, "onCreate: "+pathQ+cls);
            return;
        }
        File file = new File(myDir,"topics.txt");
        final String[] topics = readData(file);

        int count = topics.length;
        if (count==0) {
            toastMessage("Error! :(");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Topic");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(topics, -1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String curTitle = topics[i];
                myapp.setQuizTitle(curTitle);
                dialogInterface.dismiss();
                Log.d(TAG, "onClick: "+curTitle);
                Intent intent = new Intent(HomePage.this,SelectName.class);
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

    private void enterNumStudents() {

        Log.d(TAG, "enterNumStudents: ");
        Dialog dialog = new Dialog(HomePage.this);
        dialog.setContentView(R.layout.dialog_edit_number);

        final EditText et  = (EditText) dialog.findViewById(R.id.etDialogNumber);
        Button btnDone     = (Button)   dialog.findViewById(R.id.btnDialogNumber);
        et.setEnabled(true);
        btnDone.setEnabled(true);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = et.getText().toString().trim();
                int numStudents;
                try {
                    numStudents = Integer.parseInt(number);

                }catch (NumberFormatException e){
                    e.printStackTrace();
                    toastMessage("Not a number!");
                    return;
                }

                Intent intent = new Intent(HomePage.this,AddStudents.class);
                intent.putExtra("number",numStudents);
                startActivity(intent);
            }
        });

        dialog.show();

    }

    private void uploadStatus(String school,String Cls) {

        Log.d(TAG, "uploadStatus: ");

        String dateSt  = new Date().toString();
        String date    = dateSt.substring(8,10);
        String month   = dateSt.substring(4,7);
        String year    = dateSt.substring(30,34);
        String dateStr = date + "-" + month + "-" + year;

        Log.d(TAG, "uploadStatus: "+dateStr);
        databaseReference.child("Offline").child("Update Report").child(school).child(Cls).setValue(dateStr);

    }

    private boolean uploadStatusClass(String cls, String school) {

        Log.d(TAG, "uploadStatusClass: ");

        String[] subs = getResources().getStringArray(R.array.Subjects);
        for (String subject:subs) {

            String finalPath = pathQ + cls + "/" + subject;
            Log.d(TAG, "uploadStatusClass: "+finalPath);
            File myDir = new File(finalPath);
            if (!myDir.exists()) continue;

            Log.d(TAG, "uploadStatusClass: exists");

            String[] sections = myapp.getSections(cls);
            for (String sec : sections) {
                if (uploadStatusSec(school, cls, sec, subject, myDir)) continue;
            }
        }
        return false;
    }

    private boolean uploadStatusSec(String school, String cls, String sec, String subject, File myDir) {

        Log.d(TAG, "uploadStatusSec: ");

        File file = new File(myDir, "/topics.txt");
        String[] topics = readData(file);

        if (topics.length == 0)
            return true;

        String section = "" + sec;

        String finalPath = pathU + school + "/" + cls + "/" + sec;

        File myDire = new File(finalPath);
        if (!myDire.exists()) {
            return true;
        }

        DatabaseReference tempRef = databaseReference.child("Offline").child("Schools").child(school)
                .child(cls).child(section);

        uploadStatus(school, cls + sec);
        uploadTeacherScore(tempRef, topics, subject);
        uploadStudentScore(tempRef, school, cls, section, subject);

        Log.d(TAG, "uploadScores: " + finalPath);
        return false;
    }

    private void uploadScores() {

        Log.d(TAG, "uploadScores: ");
        if (!isConnected){
            toastMessage("you are not connected to the internet!");
            return;
        }

        String school = myapp.getSchool();
        String[] clses = myapp.getClsArray();
        for (String cls:clses) {
            if(uploadStatusClass(cls,school)) continue;
        }
    }

    private void uploadStudentScore(DatabaseReference reference, String School, String cls, String sec, String subject) {

        int rollNo = 1;String id = School + cls + sec + (rollNo < 10 ? "00" : "0") + rollNo;
        String finalPath = pathU + School + "/" + cls + "/" + sec + "/" + id;
        Log.d(TAG, "uploadStudentScore: "+finalPath);
        File myDir = new File(finalPath);
        while (myDir.exists()) {

            //scores
            File file = new File(myDir + "/" + subject,"Scores.txt");
            String[] result = readData(file);
            int count = result.length;
            if (result.length!=0) {
                for (int i = 0; i < count; i += 3) {
                    reference.child(id).child("Scores").child(result[i]).child("Score").setValue(result[i + 1]);
                    reference.child(id).child("Scores").child(result[i]).child("%Score").setValue(result[i + 2]);
                }
            }
            //


            //details
            file = new File(myDir,"Details.txt");
            result = readData(file);
            count = result.length;
            if (count==0){
                toastMessage("Error! :(");
                return;
            }
            //

            //storing the data in firebase
            DatabaseReference tempRef = databaseReference.child("users").child(userId).child("Classes").child(cls).child(sec).child(id);
            tempRef.child("Name").setValue(result[0]);
            tempRef.child("Age").setValue(result[1]);
            tempRef.child("Gender").setValue(result[2]);
            Log.d(TAG, "uploadStudentScore: user");

            tempRef = databaseReference.child("Offline").child("Schools").child(school).child(cls).child(sec).child(id);
            tempRef.child("Name").setValue(result[0]);
            tempRef.child("Age").setValue(result[1]);
            tempRef.child("Gender").setValue(result[2]);
            Log.d(TAG, "uploadStudentScore: offline");
            //

            rollNo++;
            id = School + cls + sec + (rollNo < 10 ? "00" : "0") + rollNo;
            finalPath = pathU + School + "/" + cls + "/" + sec + "/" + id;
            myDir = new File(finalPath);

        }
    }

    private void uploadTeacherScore(DatabaseReference tempRef, String[] topics, String subject) {

        Log.d(TAG, "uploadTeacherScore: ");
        File myDir,file;
        if (topics.length==0)
            return;

        for (String s:topics){
            Log.d(TAG, "uploadTeacherScore: "+s);
            myDir = new File(pathU + subject + "/" + s);
            if (myDir.exists()){

                Log.d(TAG, "uploadTeacherScore: exists");
                file = new File(myDir, "Scores.txt");
                String[] scores = readData(file);
                int numQues = scores.length;
                if (numQues==0) {
                    toastMessage("Error! :(");
                    return;
                }

                for (int q_no=1;q_no<numQues;q_no+=2) {
                    String score = scores[q_no] + "/" + scores[q_no+1];
                    tempRef.child("Teacher").child(s).child("Q" + q_no).setValue(score);
                }
            }
            else continue;
        }
    }

    private void syncData() {

        Log.d(TAG, "syncData: ");
        if (!isConnected){
            toastMessage("you are not connected to the internet!");
            return;
        }
        DatabaseReference tempref = databaseReference.child("users").child(userId);
        tempref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Save Teacher's basic info
                myapp.clearSections();

                ArrayList<String> classes = new ArrayList<>();
                for(DataSnapshot aClass: dataSnapshot.child("Classes").getChildren()){
                    String curClass = aClass.getKey();
                    classes.add(curClass);
                    ArrayList<String> Sec = new ArrayList<>();
                    for (DataSnapshot aSec: aClass.getChildren()){
                        Sec.add(aSec.getKey());
                    }
                    Sections section = new Sections(curClass);
                    section.setSections(Sec);
                    myapp.addSection(section);
                }

                String School  = dataSnapshot.child("School").getValue(String.class);

                Log.d(TAG, "onDataChange: teacher added"+School);
                myapp.setSchool(School);
                myapp.setClses(classes);

                File myDir = new File(pathU);
                if (!myDir.exists()) myDir.mkdirs();
                File file = new File(myDir,"BasicData.txt");
                String data = School+"\n";
                for (String c:classes){
                    data += c +"\n";
                }
                SaveData(file,data);
                //

                //Save teacher's scores
                if (dataSnapshot.hasChild("Scores")) {
                    for (DataSnapshot snapshot : dataSnapshot.child("Scores").getChildren()) {
                        String topic = snapshot.child("Title").getValue(String.class);
                        myDir = new File(pathU + topic);
                        if (!myDir.exists()) myDir.mkdirs();
                        file = new File(myDir, "Scores.txt");
                        data = "";
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            data += ds.getValue(String.class) + "\n";
                        }
                        SaveData(file, data);
                    }
                }//
                syncStudents(dataSnapshot,School);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getUserData();

    }

    private void syncStudents(DataSnapshot dataSnapshot, String school) {
        Log.d(TAG, "onDataChange: teacher scores added");

        File file, myDir;

        //Save Students' data
        for (DataSnapshot aClass : dataSnapshot.child("Classes").getChildren()) {

            String curClass = aClass.getKey();
            String sections = "", data;
            Log.d(TAG, "syncStudents: " + curClass);

            if (aClass.getChildrenCount()==0)
                continue;
            for (DataSnapshot aSec : aClass.getChildren()) {

                String curSection = aSec.getKey();
                sections += curSection+"\n";
                Log.d(TAG, "syncStudents: " + curSection);

                if (aSec.getChildrenCount()==0)
                    continue;
                for (DataSnapshot aChild : aSec.getChildren()) {

                    //Save Students' basic info
                    String id = aChild.getKey();
                    Log.d(TAG, "syncStudents: "+id);

                    String age = aChild.child("Age").getValue(String.class);
                    String stName = aChild.child("Name").getValue(String.class);
                    String gender = aChild.child("Gender").getValue(String.class);
                    myDir = new File(pathU + school + "/" + curClass + "/" + curSection + "/" + id);
                    if (!myDir.exists()) myDir.mkdirs();
                    data = stName + "\n" + age + "\n" + gender + "\n";

                    file = new File(myDir, "Details.txt");
                    SaveData(file, data);
                    //

                    Log.d(TAG, "onDataChange: Students added");
                    //Save Students' scores

                    if (aChild.hasChild("Scores")) {
                        Log.d(TAG, "onDataChange: has scores");

                        for (DataSnapshot aSubject : aChild.child("Scores").getChildren()) {

                            String curSubject = aSubject.getKey();
                            myDir = new File(myDir+"/"+curSubject);
                            if (!myDir.exists()) myDir.mkdirs();
                            file = new File(myDir, "Scores.txt");
                            data = "";

                            for (DataSnapshot ds:aSubject.getChildren())
                                data += ds.getKey() + "\n" + ds.getValue() + "\n";

                            SaveData(file, data);
                            Log.d(TAG, "onDataChange: Students scores added");

                        }
                    }

                }

            }

            myDir = new File(pathU + school + "/" + curClass);
            if (!myDir.exists()) myDir.mkdirs();

            file = new File(myDir, "Sections.txt");
            SaveData(file, sections);
            Log.d(TAG, "syncStudents: saved for " + curClass);
        }
    }

    private void SaveData(File file, String data) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
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

}
