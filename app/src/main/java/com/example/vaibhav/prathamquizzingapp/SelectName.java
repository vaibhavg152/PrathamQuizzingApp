package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by vaibhav on 4/6/18.
 */

public class SelectName extends Activity {
    private static final String TAG = "SelectName";

    private ListView listNames;
    private final String pathU = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/User/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_name);
        Log.d(TAG, "onCreate: created");

        final String cls    = myapp.getCls();
        final String sec    = myapp.getSec();
        final String School = myapp.getSchool();
        final String title  = myapp.getQuizTitle();

        final boolean type = getIntent().getBooleanExtra("type",false);
        final ArrayList<String> namesarray  = new ArrayList<>();
        final ArrayList<String> genderArray = new ArrayList<>();
        final ArrayList<String> ageArray    = new ArrayList<>();
        final ArrayList<String> arrayId     = new ArrayList<>();
        listNames = (ListView) findViewById(R.id.listviewNames);

        int rollNo = 1;String id = School + cls + sec + (rollNo < 10 ? "00" : "0") + rollNo;

        String finalPath = pathU + School + "/" + cls + "/" + sec + "/" + id;
        Log.d(TAG, "onCreate: "+finalPath);
        File myDir = new File(finalPath);
        while (myDir.exists()) {
            Log.d(TAG, "onCreate: "+id);
            File file = new File(myDir,"Details.txt");
            String[] names = readData(file);
            if (names.length==0){
                toastMessage("Error! :(");
                return;
            }
            namesarray.add(names[0]);
            arrayId.add(id);
            if (type){
                ageArray.add(names[1]);
                genderArray.add(names[2]);
            }
            rollNo++;
            id = School + cls + sec + (rollNo < 10 ? "00" : "0") + rollNo;

            myDir = new File(pathU + School + "/" + cls + "/" + sec + "/" + id);
        }

        ArrayAdapter adapterNames = new ArrayAdapter(SelectName.this, android.R.layout.simple_expandable_list_item_1, namesarray);
        listNames.setAdapter(adapterNames);

        listNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if (type){
                        viewDetails(arrayId.get(i), genderArray.get(i), namesarray.get(i), ageArray.get(i));
                    }
                    else {
                        Intent intent = new Intent(SelectName.this, QuizActivity.class);
                        intent.putExtra("childId", arrayId.get(i));
                        intent.putExtra("user", true);
                        startActivity(intent);
                        finish();
                    }
                }
        });
    }

    private void viewDetails(final String id, final String gen, final String name, final String age) {
        Log.d(TAG, "viewDetails: ");

        AlertDialog.Builder builder = new AlertDialog.Builder(SelectName.this);
        builder.setTitle(id);

        builder.setMessage("Name : "+name+"\n"+
                           "Gender : "+gen+"\n" +
                           "Age : "+age+"\n");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton("View Scores", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SelectName.this,showStudents.class);
                intent.putExtra("teacher",true);
                intent.putExtra("id",id);
                startActivity(intent);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SelectName.this,EditStudents.class);
                intent.putExtra("id",id);
                intent.putExtra("name",name);
                intent.putExtra("gender",gen);
                intent.putExtra("age",age);
                startActivity(intent);
                dialogInterface.dismiss();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
