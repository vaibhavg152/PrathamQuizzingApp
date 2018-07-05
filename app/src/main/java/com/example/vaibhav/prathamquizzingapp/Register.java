package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.Sections;
import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vaibhav on 1/6/18.
 */

public class Register extends Activity {
    private static final String TAG = "Register";

//    private DatabaseReference reference;
    private EditText schoolID,etName;
    private Button btnDone,btnAddClasses,btnAddSections;
    private ListView listView;
    private ArrayAdapter adapter;
    private String[] allClassesList,allSections = new String[26],clses;
    private boolean[] checkedClasses,checkedSections;
    private ArrayList<String> selectedClasses = new ArrayList<>();
    private ArrayList<Integer> selectedInt = new ArrayList<>(),selectedIntS = new ArrayList<>();
    private final String pathU = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/User/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: created");

        allClassesList = getResources().getStringArray(R.array.allClasses);
        checkedClasses = new boolean[allClassesList.length];

        //reference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

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
                goToNextStep();
            }
        });

    }

    private void addClasses() {

        Log.d(TAG, "addClasses: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this,R.style.Theme_AppCompat_Dialog_Alert);
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
                clses = new String[selectedClasses.size()];
                for (int i=0; i< clses.length; i++)
                    clses[i] = selectedClasses.get(i);
                myapp.setClses(clses);
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
        myapp.clearSections();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int iCls, long l) {

                Log.d(TAG, "onItemClick: blah");
                final String curClass = selectedClasses.get(iCls);

                checkedSections = new boolean[allSections.length];

                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this,R.style.Theme_AppCompat_Light_Dialog_Alert);
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
//                            reference.child("Classes").child(curClass).child(abc).setValue(abc);
                        }
                        if (sec.isEmpty())
                            return;

                        addSectionsToCls(curClass,sec);

                        selectedIntS.clear();
                        selectedClasses.remove(iCls);
                        toastMessage("added sections for Class"+curClass);

                        if (selectedClasses.isEmpty()) {
                            btnDone.setEnabled(true);
                            btnDone.setVisibility(View.VISIBLE);
                        }

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

    private void goToNextStep() {

        Log.d(TAG, "goToNextStep: ");

        String school = schoolID.getText().toString().trim();
        if (school.length() != 0) {

            try {
                int intSchool = Integer.parseInt(school);
                school = createSchool(intSchool);
            } catch (NumberFormatException e) {
                toastMessage("SchoolID should be a number");
                return;
            }
        } else {
            toastMessage("School ID can not be empty");
            return;
        }

        String name = etName.getText().toString().trim();
        if (name.length() == 0) {
            toastMessage("Name can not be empty!");
            return;
        }

        File myDir = new File(pathU);
        if (!myDir.exists()) myDir.mkdirs();
        File file = new File(myDir, "BasicData.txt");
        String data = school + "\n" + name + "\n";

        for (String c : clses) {
            data += c + "\n";
        }
        SaveData(file, data);

        for (String c : clses) {
            myDir = new File(pathU + school + "/" + c);
            if (!myDir.exists()) myDir.mkdirs();
            file = new File(myDir, "Sections.txt");
            String[] sec = myapp.getSections(c);
            data = "";
            for (String s : sec)
                data += s + "\n";
            SaveData(file, data);
        }

        //reference.child("School").setValue(school);
        //reference.child("Name").setValue(name);

        myapp.setSchool(school);
        toastMessage("Details are stored. Now add Students to your classes. :)");

        for (String c: clses){
            myapp.setCls(c);
            Log.d(TAG, "goToNextStep: "+c);
            String[] sec = myapp.getSections(c);
            for (String s: sec){
                myapp.setSec(s);
                Log.d(TAG, "goToNextStep: "+s);
                enterNumStudents(c,s);
            }
        }

    }

    private void enterNumStudents(final String cls, final String sec) {

        Log.d(TAG, "enterNumStudents: ");
        final Dialog dialog = new Dialog(Register.this);
        dialog.setContentView(R.layout.dialog_edit_number);

        dialog.setTitle("Number of students in class "+myapp.getCls()+myapp.getSec());
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

                dialog.dismiss();
                Intent intent = new Intent(Register.this,AddStudents.class);
                intent.putExtra("number",numStudents);
                intent.putExtra("class",cls);
                intent.putExtra("section",sec);
                startActivity(intent);
            }
        });

        dialog.show();

    }

    public void addSectionsToCls(String curClass,ArrayList<String> sec){
        Sections sections = new Sections(curClass);
        sections.setSections(sec);
        myapp.addSection(sections);
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
