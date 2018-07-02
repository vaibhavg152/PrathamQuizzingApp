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

import com.example.vaibhav.prathamquizzingapp.classes.myapp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private DatabaseReference reference;
    private final static String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            + "/Pratham/User/";

    private String studID,Gen="";
    int i = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_data);
        Log.d(TAG, "onCreate: created");

        final String school    = myapp.getSchool();
        final String userId    = myapp.getUserId();

        //getting the data from intent
        Intent incomingIntent  = getIntent();
        final String Class     = myapp.getCls();
        final String Section   = myapp.getSec();
        final int NumStudents  = incomingIntent.getIntExtra("number", 0);
        //end

        Log.d(TAG, "onCreate: "+NumStudents);

        //initializing widgets
        reference  = FirebaseDatabase.getInstance().getReference();
        name       = (EditText) findViewById(R.id.etStudName);
        age        = (EditText) findViewById(R.id.etStudage);
        studentID  = (TextView) findViewById(R.id.txtStudentID);
        txtNumber  = (TextView) findViewById(R.id.txtNumSD);
        gender     = (Button)   findViewById(R.id.etStudgender);
        next       = (Button)   findViewById(R.id.btnNextStud);
        //end

        txtNumber.setText("Student "+i+"/"+NumStudents);
        studID = school + Class + Section + "001";
        studentID.setText("ID: " + studID);

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] arrayGenders = {"Male","Female","Others"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddStudents.this);

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

                //getting and checking the current data
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
                //end

                Log.d(TAG, "onClick: got data");

                //
                File myDir,file;
                String finalPath,data;

                finalPath=path+myapp.getSchool()+"/"+Class+"/"+Section+"/"+studID;
                myDir = new File(finalPath);
                if (!myDir.exists()) myDir.mkdirs();

                file = new File(myDir,"Details.txt");
                data = nameS+"\n"+ageS+"\n"+Gen;
                SaveData(file,data);
                //

                
                //all the stuff needed for updating for getting the next student
                //updating the counter
                if (i == NumStudents - 1) {
                    next.setText("Finish");
                    Log.d(TAG, "onClick: finish this!");
                } else if (i == NumStudents) {
                    Log.d(TAG, "onClick: Doneeeee");
                    Intent intent = new Intent(AddStudents.this, HomePage.class);
                    startActivity(intent);
                    Toast.makeText(AddStudents.this, "Data Stored!", Toast.LENGTH_SHORT).show();
                }
                i++;
                //end

                //updating the text fields on screen
                if (i < 10)
                    studID = school + Class + Section + "00" + i;
                else if (i < 100)
                    studID = school + Class + Section + "0" + i;
                else
                    studID = school + Class + Section + i;

                Log.d(TAG, "onClick: "+studID);
                studentID.setText("ID: " + studID);
                txtNumber.setText("Student "+i+"/"+NumStudents);
                Gen="";
                age.setText("");
                name.setText("");
                Log.d(TAG, "onClick: yay");
                //end
             //end
            }
        });

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
