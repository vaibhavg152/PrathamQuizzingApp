package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by vaibhav on 1/6/18.
 */

public class AddStudents extends Activity {
    private static final String TAG = "AddStudents";

    private EditText name, age;
    private Button gender, next;
    private TextView studentID,txtNumber;
    private final String pathU = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/User/";
    private String studID,Gen="",cls,Section,school;
    private int i = 1,NumStudents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_data);
        Log.d(TAG, "onCreate: created");

        school    = myapp.getSchool();

        Intent intent = getIntent();
        cls       = intent.getStringExtra("class");
        Section   = intent.getStringExtra("section");
        NumStudents  = intent.getIntExtra("number", 0);

        Log.d(TAG, "onCreate: "+NumStudents);

        //initializing widgets
        name       = (EditText) findViewById(R.id.etStudName);
        age        = (EditText) findViewById(R.id.etStudage);
        studentID  = (TextView) findViewById(R.id.txtStudentID);
        txtNumber  = (TextView) findViewById(R.id.txtNumSD);
        gender     = (Button)   findViewById(R.id.etStudgender);
        next       = (Button)   findViewById(R.id.btnNextStud);
        //end

        txtNumber.setText("Student "+i+"/"+NumStudents);
        studID = school + cls + Section + "001";
        studentID.setText("ID: " + studID);

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] arrayGenders = {"Male","Female","Others"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddStudents.this,R.style.Theme_AppCompat_Light_Dialog_Alert);

                builder.setTitle("Choose your gender");
                builder.setCancelable(false);
                builder.setSingleChoiceItems(arrayGenders, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Gen = arrayGenders[i];
                        next.setVisibility(View.VISIBLE);
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeData();
            }
        });

    }

    private void storeData() {
        Log.d(TAG, "storeData: ");

        int intAge;
        String ageS = age.getText().toString();
        String nameS = name.getText().toString();
        if (Gen.length() == 0 || ageS.length() == 0 || nameS.length() == 0) {
            Toast.makeText(AddStudents.this, "Data incomplete", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            intAge = Integer.parseInt(ageS);
        } catch (NumberFormatException e) {
            Toast.makeText(AddStudents.this, "age must be a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "onClick: got data");

        //store the data
        File myDir,file;
        String finalPath,data;

        finalPath= pathU +myapp.getSchool()+"/"+cls+"/"+Section+"/"+studID;
        myDir = new File(finalPath);
        if (!myDir.exists()) myDir.mkdirs();

        file = new File(myDir,"Details.txt");
        data = nameS+"\n"+ageS+"\n"+Gen;
        SaveData(file,data);

        updateFeilds();
    }

    private void updateFeilds() {
        //updating the counter
        i++;
        if (i > NumStudents) {
            Log.d(TAG, "onClick: Doneeeee");

            toastMessage("Data Stored for class "+cls + Section+" :)");
            finish();
        }

        else {

            if (i == NumStudents) {
                next.setText("Finish");
                Log.d(TAG, "onClick: finish this!");
            }

            if (i < 10)
                studID = school + cls + Section + "00" + i;
            else if (i < 100)
                studID = school + cls + Section + "0" + i;
            else
                studID = school + cls + Section + i;

            Log.d(TAG, "onClick: " + studID);
            studentID.setText("ID: " + studID);
            txtNumber.setText("Student " + i + "/" + NumStudents);
            Gen = "";
            age.setText("");
            name.setText("");
            Log.d(TAG, "onClick: yay");
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

}
