package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by vaibhav on 11/6/18.
 */

public class showStudents extends Activity {
    private static final String TAG = "showStudents";

    private DatabaseReference reference;
    private ArrayList<String> students;
    private ListView listNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_select_name);

        final String cls     = myapp.getCls();
        final String school  = myapp.getSchool();
        final String section = myapp.getSec();

        Log.d(TAG, "onCreate: "+cls+school+section);

        listNames = (ListView) findViewById(R.id.listviewNames);
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        students = new ArrayList<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot user:dataSnapshot.getChildren()) {

                    if (!(user.child("School").getValue(String.class).equals(school))) {
                        continue;
                    }

                    if (!user.child("Classes").hasChild(cls)) {
                        toastMessage("No data found for " + cls + " for school " + school);
                        return;
                    }

                    if (!user.child("Classes").child(cls).hasChild(section)) {
                        toastMessage("No data found for " + cls + " " + section + "in school " + school);
                    }

                    students.clear();
                    for (DataSnapshot student:user.child(school).child(cls).child(section).getChildren()){
                        if (student.hasChild("Name")) {
                            String name = student.child("Name").getValue(String.class);
                            students.add(name);
                            Log.d(TAG, "onDataChange: "+name);
                        }
                        else Log.d(TAG, "onDataChange: not found");
                    }
                    ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,students);
                    listNames.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        listNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = students.get(i);
                Intent intent = new Intent(showStudents.this,PerformanceStudent.class);
                startActivity(intent);
            }
        });
    }

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
