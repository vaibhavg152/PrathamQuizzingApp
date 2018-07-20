package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by vaibhav on 11/6/18.
 */

public class SuperUser extends Activity {
    private static final String TAG = "SuperUser";

    private FirebaseAuth mAuth;
    private Button btnAddQuiz,btnDelQuiz,btnEditQuiz,btnViewStudents,btnViewTeachers,btnUploadStatus,btnSignOut;
    private DatabaseReference reference;
    private String topicNo;
    private boolean isConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user);
        Log.d(TAG, "onCreate: ");

        btnAddQuiz      = (Button)findViewById(R.id.btnAddQuiz);
        btnEditQuiz     = (Button)findViewById(R.id.btnEditQuiz);
        btnDelQuiz      = (Button)findViewById(R.id.btnDeleteQuiz);
        btnViewStudents = (Button)findViewById(R.id.btnStudProgress);
        btnViewTeachers = (Button)findViewById(R.id.btnTeacherProgress);
        btnUploadStatus = (Button)findViewById(R.id.btnStatus);
        btnSignOut      = (Button)findViewById(R.id.btnSignOutSU);

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        isConnected = (networkInfo!=null && networkInfo.isConnectedOrConnecting());

        reference = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        mAuth = FirebaseAuth.getInstance();

        btnAddQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClass();
            }
        });
        btnDelQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select("delete");
            }
        });
        btnEditQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select("edit");
            }
        });
        btnViewStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewStudents();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                finish();
            }
        });

        btnViewTeachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: blow");
                Uri uri = Uri.parse("http://prathamquizzing.herokuapp.com");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                toastMessage("to be written");
            }
        });

        btnUploadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuperUser.this,UploadStatus.class);
                startActivity(intent);
            }
        });
    }

    private void select(final String type) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] clses = new String[(int) dataSnapshot.getChildrenCount()];
                int pos=0;
                for (DataSnapshot cls:dataSnapshot.getChildren()){
                    clses[pos] = cls.getKey();
                    pos++;
                }
                selectClass(type, clses,dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void viewStudents() {

        Log.d(TAG, "viewStudents: ");
        Dialog dialog = new Dialog(SuperUser.this);
        dialog.setContentView(R.layout.dialog_students_record);
        final EditText etSchoolID,etClass,etSection;
        Button btnDone;
        btnDone    = (Button)   dialog.findViewById(R.id.btnDoneStudRecord);
        etSection  = (EditText) dialog.findViewById(R.id.etSectionSR);
        etClass    = (EditText) dialog.findViewById(R.id.etClassSR);
        etSchoolID = (EditText) dialog.findViewById(R.id.etSchoolidSR);

        Log.d(TAG, "viewStudents: ");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sec = etSection.getText().toString().trim();
                final String cls = etClass.getText().toString().trim();
                final String sch = etSchoolID.getText().toString().trim();

                if (cls.length()!=2 || sch.length()!=3 || sec.length()!=1){
                    toastMessage("Invalid length!!");
                    return;
                }

                myapp.setSchool(sch);
                myapp.setCls(cls);
                myapp.setSec(sec);

                Intent intent = new Intent(SuperUser.this,showStudents.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void selectClass() {
        Log.d(TAG, "selectClass: ");

        final String[] array = getResources().getStringArray(R.array.allClasses);
        final AlertDialog.Builder builder = new AlertDialog.Builder(SuperUser.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Class");

        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String curClass = array[i];
                myapp.setCls(curClass);

                Log.d(TAG, "onClick: " + curClass);
                String[] subjects = getResources().getStringArray(R.array.Subjects);
                selectSubject(subjects);
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

    private void selectSubject(final String[] subjects) {
        Log.d(TAG, "selectSubject: ");

        final AlertDialog.Builder builder = new AlertDialog.Builder(SuperUser.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Subject");

        builder.setSingleChoiceItems(subjects, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String curSubject = subjects[i];
                myapp.setSubject(curSubject);

                Log.d(TAG, "onClick: " + curSubject);
                enterNumber();
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void enterNumber() {

        Log.d(TAG, "enterNumber: ");
        final Dialog dialog = new Dialog(SuperUser.this);
        dialog.setContentView(R.layout.dialog_edit_number);

        final EditText et  = (EditText) dialog.findViewById(R.id.etDialogNumber);
        Button btnDone     = (Button)   dialog.findViewById(R.id.btnDialogNumber);
        et.setEnabled(true);
        et.setHint("Number of Questions");
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

                final String cls = myapp.getCls();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: ");
                        if (dataSnapshot.hasChild(cls)) {
                            if (dataSnapshot.child(cls).hasChild(myapp.getSubject())) {

                                getNamePlease(dataSnapshot.child(cls).child(myapp.getSubject()));
                            } else { topicNo = "Topic1";}
                        } else { topicNo = "Topic1";}

                        myapp.setTopic(topicNo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                enterText(numStudents);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void enterText(final int numStudents) {
        Log.d(TAG, "enterText: ");
        
        final Dialog dialog = new Dialog(SuperUser.this);
        dialog.setContentView(R.layout.dialog_text);

        final EditText et  = (EditText) dialog.findViewById(R.id.etDialogText);
        Button btnDone     = (Button)   dialog.findViewById(R.id.btnDialogNumber);
        et.setEnabled(true);
        et.setHint("Title of the new Quiz");
        btnDone.setEnabled(true);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et.getText().toString().trim();
                myapp.setQuizTitle(name);

                dialog.dismiss();
                Intent intent = new Intent(SuperUser.this,AddQuestions.class);
                intent.putExtra("number",numStudents);
                startActivity(intent);
            }
        });

        dialog.show();


    }

    private void selectClass(final String type, final String[] array, final DataSnapshot dataSnapshot) {

        Log.d(TAG, "selectClass: ");
        final AlertDialog.Builder builder = new AlertDialog.Builder(SuperUser.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Class");

        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String curClass = array[i];
                myapp.setCls(curClass);

                Log.d(TAG, "onClick: " + curClass);
                if (type.equals("edit")||type.equals("delete")) {

                    String[] subjects = new String[(int) dataSnapshot.child(curClass).getChildrenCount()];
                    int pos = 0;
                    for (DataSnapshot sub : dataSnapshot.child(curClass).getChildren()) {
                        subjects[pos] = sub.getKey();

                        Log.d(TAG, "onClick: "+subjects[pos]);
                        pos++;
                    }

                    selectSubject(subjects, dataSnapshot.child(curClass), type);
                    dialogInterface.dismiss();
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

    private void selectSubject(final String[] subjects, final DataSnapshot dataSnapshot, final String type) {

        Log.d(TAG, "selectSubject: ");
        
        final AlertDialog.Builder builder = new AlertDialog.Builder(SuperUser.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Subject");
        
        builder.setSingleChoiceItems(subjects, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String curSubject = subjects[i];
                myapp.setSubject(curSubject);

                Log.d(TAG, "onClick: " + curSubject);
                if (type.equals("edit")||type.equals("delete")) {

                    String[] topics = new String[(int) dataSnapshot.child(curSubject).getChildrenCount()];
                    String[] titles = new String[topics.length];
                    int pos = 0;
                    if (topics.length==0){
                        toastMessage("No quiz found");
                        Log.d(TAG, "onClick: empty");
                        return;
                    }
                    Log.d(TAG, "onClick: wow");
                    for (DataSnapshot topic : dataSnapshot.child(curSubject).getChildren()) {
                        titles[pos] = topic.child("Title").getValue(String.class);
                        topics[pos] = topic.getKey();
                        Log.d(TAG, "onClick: "+titles[pos]);
                        pos++;
                    }

                    selectTopic(titles,topics,type);
                    dialogInterface.dismiss();
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

    private void selectTopic(final String[] titles, final String[] topics, final String type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SuperUser.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Select a Topic");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(titles, -1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String curTopic = topics[i],curTitle = titles[i];
                myapp.setTopic(curTopic);
                myapp.setQuizTitle(curTitle);

                if (type.equals("edit")) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(SuperUser.this, EditQuiz.class);
                    startActivity(intent);
                }
                if (type.equals("delete")){
                    deleteQuiz(curTopic);
                    dialogInterface.dismiss();
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

    private void getNamePlease(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getNamePlease: ");

        final ArrayList<String> topics = new ArrayList<>();

        for (DataSnapshot ds:dataSnapshot.getChildren()){
                    topics.add(ds.getKey());
                }
                int i=1;
                do {
                    topicNo="Topic"+i;
                    i+=1;
                }while (topics.contains(topicNo));
                Log.d(TAG, "getNamePlease: result "+topicNo);

    }

    private void deleteQuiz(String curTopic) {
        Log.d(TAG, "deleteQuiz: ");
        reference.child(myapp.getCls()).child(myapp.getSubject()).child(curTopic).removeValue();
        toastMessage("Deleted quiz "+curTopic);
    }

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
