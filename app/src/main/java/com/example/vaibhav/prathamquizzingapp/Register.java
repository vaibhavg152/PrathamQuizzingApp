package com.example.vaibhav.prathamquizzingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.classes.Sections;
import com.example.vaibhav.prathamquizzingapp.classes.myapp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by vaibhav on 1/6/18.
 */

public class Register extends AppCompatActivity{
    private static final String TAG = "Register";

    private DatabaseReference reference;
    private EditText schoolID,etName;
    private Button btnDone,btnAddClasses,btnAddSections;
    private ListView listView;
    private ArrayAdapter adapter;
    private String[] allClassesList,allSections = new String[26];
    private boolean[] checkedClasses,checkedSections;
    private ArrayList<String> selectedClasses = new ArrayList<>();
    private ArrayList<Integer> selectedInt = new ArrayList<>(),selectedIntS = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Log.d(TAG, "onCreate: created");

        final String userId = myapp.getUserId();
        allClassesList = getResources().getStringArray(R.array.allClasses);
        checkedClasses = new boolean[allClassesList.length];

        reference = FirebaseDatabase.getInstance().getReference().child("Pratham").child("users").child(userId);

        char ch = 'A';
        for (int i=0;i<26;i++,ch++){
            allSections[i] = ""+ch;
        }
        checkedSections = new boolean[allSections.length];

        schoolID        = (EditText) findViewById(R.id.etSchoolID);
        etName          = (EditText) findViewById(R.id.etTeacherName);
        btnAddClasses   = (Button)   findViewById(R.id.btnAddClasses);
        btnAddSections  = (Button)   findViewById(R.id.btnAddSections);
        btnDone         = (Button)   findViewById(R.id.btnDoneR);
        listView        = (ListView) findViewById(R.id.listRegister);

        btnAddClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClasses();
            }
        });

        btnAddSections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSections();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextStep(userId);
            }
        });

    }

    private void addClasses() {

        Log.d(TAG, "addClasses: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setTitle("Select all of your classes");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(allClassesList, checkedClasses, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked){
                    if (selectedClasses.contains(allClassesList[position])){
                        selectedClasses.remove(allClassesList[position]);
                    }
                    else selectedInt.add(position);
                }else if (selectedClasses.contains(allClassesList[position])){
                    selectedClasses.remove(allClassesList[position]);
                }
            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                Log.d(TAG, "onClick: blkv");
                for(int i = 0;i<selectedInt.size();i++) {
                    String curClass = allClassesList[selectedInt.get(i)];
                    selectedClasses.add(curClass);
                }
                myapp.setClses(selectedClasses);
                btnAddSections.setEnabled((selectedClasses.size()!=0));
                btnAddClasses.setEnabled(!(selectedClasses.size()!=0));
            }
        });

        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void addSections(){

        btnAddSections.setVisibility(View.GONE);
        Log.d(TAG, "addSections: ");
        adapter = new ArrayAdapter(Register.this,android.R.layout.simple_expandable_list_item_1,selectedClasses);
        listView.setAdapter(adapter);
        toastMessage("Add Sections by choosing a Class");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int iCls, long l) {

                Log.d(TAG, "onItemClick: blah");
                final String curClass = selectedClasses.get(iCls);

                checkedSections = new boolean[allSections.length];

                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                builder.setTitle("Select section for class"+curClass);
                builder.setCancelable(false);

                builder.setMultiChoiceItems(allSections, checkedSections, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked){
                            if (selectedIntS.contains(position)){
                                selectedIntS.remove(position);
                            }
                            else selectedIntS.add(position);
                        }
                    }
                });

                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {

                        ArrayList<String> sec = new ArrayList<>();
                        for (int i = 0; i < selectedIntS.size(); i++) {
                            String abc = allSections[selectedIntS.get(i)];
                            sec.add(abc);
                            Log.d(TAG, "onClick: "+abc);
                            reference.child("Classes").child(curClass).child(abc).setValue(abc);
                        }
                        if (sec.isEmpty())
                            return;

                        addSectionsToCls(curClass,sec);

                        selectedIntS.clear();
                        selectedClasses.remove(iCls);
                        toastMessage("added sections for Class"+curClass);

                        if (selectedClasses.isEmpty())
                            btnDone.setVisibility(View.VISIBLE);

                        adapter = new ArrayAdapter(Register.this, android.R.layout.simple_expandable_list_item_1, selectedClasses);
                        listView.setAdapter(adapter);

                    }

                });

                builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

    }

    private void goToNextStep(String userId) {

        Log.d(TAG, "goToNextStep: ");

        String school = schoolID.getText().toString().trim();
        if(school.length()!=0) {

            try {
                int intSchool = Integer.parseInt(school);
                school = createSchool(intSchool);
            } catch (NumberFormatException e) {
                toastMessage("SchoolID should be a number");
                return;
            }
        }
        else {
            toastMessage("School ID can not be empty");
            return;
        }

        String name = etName.getText().toString().trim();
        if (name.length()==0){
            toastMessage("Name can not be empty!");
            return;
        }

        reference.child("School").setValue(school);
        reference.child("Name").setValue(name);

        myapp.setSchool(school);
        myapp.setUserId(userId);

        Intent intent = new Intent(Register.this,HomePage.class);
        startActivity(intent);

    }

    public void addSectionsToCls(String curClass,ArrayList<String> sec){
        Sections sections = new Sections(curClass);
        sections.setSections(sec);
        myapp.addSection(sections);
    }

    private void toastMessage(String msg) {
        Toast.makeText(Register.this, msg, Toast.LENGTH_SHORT).show();
    }

    private String createSchool(int intSchool) {
        if (intSchool<10)
            return "00"+intSchool;
        else if (intSchool<100)
            return "0"+intSchool;
        else return ""+intSchool;
    }

}
