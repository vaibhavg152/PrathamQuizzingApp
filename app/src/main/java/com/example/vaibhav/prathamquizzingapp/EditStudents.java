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

public class EditStudents extends Activity {
    private static final String TAG = "EditStudents";

    private EditText etName, etAge;
    private Button gender, next;
    private TextView studentID,txtNumber;
    private final String pathU = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/User/";
    private String studID,Gen="",cls,Section,school;
    private int NumStudents=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_data);
        Log.d(TAG, "onCreate: ");

        school    = myapp.getSchool();
        cls       = myapp.getCls();
        Section   = myapp.getSec();
        NumStudents  = 1;

        Intent intent = getIntent();
        studID = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        Gen = intent.getStringExtra("gender");

        Log.d(TAG, "onCreate: "+NumStudents);

        //initializing widgets
        etName = (EditText) findViewById(R.id.etStudName);
        etAge = (EditText) findViewById(R.id.etStudage);
        studentID  = (TextView) findViewById(R.id.txtStudentID);
        txtNumber  = (TextView) findViewById(R.id.txtNumSD);
        gender     = (Button)   findViewById(R.id.etStudgender);
        next       = (Button)   findViewById(R.id.btnNextStud);
        //end

        next.setText("Save");
        gender.setText("Gender: "+Gen);
        txtNumber.setText("");
        studentID.setText("ID: " + studID);
        etAge.setText(age);
        etName.setText(name);

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] arrayGenders = {"Male","Female","Others"};
                AlertDialog.Builder builder = new AlertDialog.Builder(EditStudents.this,R.style.Theme_AppCompat_Light_Dialog_Alert);

                builder.setTitle("Choose your gender");
                builder.setCancelable(false);
                builder.setSingleChoiceItems(arrayGenders, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Gen = arrayGenders[i];
                        gender.setText("Selected "+Gen);
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
        String ageS = etAge.getText().toString();
        String nameS = etName.getText().toString();
        if (Gen.length() == 0 || ageS.length() == 0 || nameS.length() == 0) {
            toastMessage("Data incomplete");
            return;
        }
        try {
            intAge = Integer.parseInt(ageS);
        } catch (NumberFormatException e) {
            toastMessage("age must be a number!");
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
        finish();

    }

//    private void updateFeilds() {
//        //updating the counter
//        i++;
//        if (i > NumStudents) {
//            Log.d(TAG, "onClick: Doneeeee");
//
//            toastMessage("Data Stored for class "+cls + Section+" :)");
//            finish();
//        }
//
//        else {
//
//            if (i == NumStudents) {
//                next.setText("Finish");
//                Log.d(TAG, "onClick: finish this!");
//            }
//
//            if (i < 10)
//                studID = school + cls + Section + "00" + i;
//            else if (i < 100)
//                studID = school + cls + Section + "0" + i;
//            else
//                studID = school + cls + Section + i;
//
//            Log.d(TAG, "onClick: " + studID);
//            studentID.setText("ID: " + studID);
//            txtNumber.setText("Student " + i + "/" + NumStudents);
//            Gen = "";
//            age.setText("");
//            etName.setText("");
//            Log.d(TAG, "onClick: yay");
//        }
//
//    }

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
