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
    private ArrayList<String> students,avgScores,scores,topics,users;
    private ListView listNames;
    private String cls,school,section;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_select_name);

        cls      = myapp.getCls();
        school   = myapp.getSchool();
        section  = myapp.getSec();
        final boolean teacher = getIntent().getBooleanExtra("teacher",false);
        final String stID=( teacher? getIntent().getStringExtra("id") : "");
        Log.d(TAG, "onCreate: "+cls+school+section);

        listNames = (ListView) findViewById(R.id.listviewNames);
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        scores    = new ArrayList<>();
        avgScores = new ArrayList<>();
        topics    = new ArrayList<>();
        students  = new ArrayList<>();
        users     = new ArrayList<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                students.clear();

                for (DataSnapshot user:dataSnapshot.getChildren()) {

                    if (!(user.child("School").getValue(String.class).equals(school))) {
                        continue;
                    }
                    users.add(user.getKey());

                    Log.d(TAG, "onDataChange: "+user.getKey());
                    if (!user.child("Classes").hasChild(cls)) {
                        toastMessage("No data found for " + cls + " for school " + school);
                        continue;
                    }

                    if (!user.child("Classes").child(cls).hasChild(section)) {
                        toastMessage("No data found for " + cls + " " + section + " in school " + school);
                        continue;
                    }

                    for (DataSnapshot student:user.child("Classes").child(cls).child(section).getChildren()){


                        if (student.hasChild("Name")) {

                            String name = student.child("Name").getValue(String.class);
                            Log.d(TAG, "onDataChange: "+name);
                            students.add(name);

                            if (teacher){
                                if (student.getKey().equals(stID)) {
                                    viewScores(name);
                                    finish();
                                }
                                else continue;
                            }
                        }

                        else Log.d(TAG, "onDataChange: not found");
                    }
                }
                Log.d(TAG, "onDataChange: "+students.size());
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,students);
                listNames.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        listNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                viewScores(students.get(position));
            }
        });
    }

    private void viewScores(final String name) {
        Log.d(TAG, "viewScores: ");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: ");
                for (String userStr:users){

                    Log.d(TAG, "onDataChange: "+userStr);
                    DataSnapshot user = dataSnapshot.child(userStr);

                    String teacherName = user.child("Name").getValue(String.class);

                    Log.d(TAG, "onDataChange: "+teacherName);
                    for (DataSnapshot student:user.child("Classes").child(cls).child(section).getChildren()) {

                        Log.d(TAG, "onDataChange: "+name);
//                        if (!student.child("Name").equals(name))
//                            continue;
//
                        if (student.hasChild("Scores")) {
                            Log.d(TAG, "onDataChange: ");

                            for (DataSnapshot aSubject : student.child("Scores").getChildren()) {
                                String subject = aSubject.getKey();
                                Log.d(TAG, "onDataChange: " + subject);

                                for (DataSnapshot aTopic : aSubject.getChildren()) {
                                    String topic = aTopic.getKey();
                                    topics.add(subject + ": " + topic);
                                    Log.d(TAG, "onDataChange: " + topic);

                                    String score = aTopic.child("%Score").getValue(String.class) + "%";
                                    scores.add(score);
                                    Log.d(TAG, "onDataChange: " + score);

                                    try {
                                        String avgScore = "(" + teacherName + ") " + user.child("Scores").child(cls).child(subject).child(topic)
                                                .child("averageScore").getValue(long.class) + "%";
                                        avgScores.add(avgScore);

                                        Log.d(TAG, "onDataChange: " + avgScore);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        String avgScore = "(" + teacherName + ") " + user.child("Scores").child(cls).child(subject).child(topic)
                                                .child("averageScore").getValue(String.class) + "%";
                                        avgScores.add(avgScore);

                                        Log.d(TAG, "onDataChange: " + avgScore);
                                    }
                                }
                            }
                        }
                    }

                }
                int count = avgScores.size();
                String[] scor = new String[count];
                String[] avgs = new String[count];
                String[] topi = new String[count];
                for (int i=0; i<count; i++){
                    scor[i] = scores.get(i);
                    topi[i] = topics.get(i);
                    avgs[i] = avgScores.get(i);
                }
                Intent intent = new Intent(showStudents.this,PerformanceStudent.class);
                intent.putExtra("topics",topi);
                intent.putExtra("scores",scor);
                intent.putExtra("avgScores",avgs);
                intent.putExtra("name",name);
                startActivity(intent);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
