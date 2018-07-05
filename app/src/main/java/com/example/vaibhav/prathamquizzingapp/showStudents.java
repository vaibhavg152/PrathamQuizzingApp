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
    private ArrayList<String> students,avgScores,scores,topics;
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

        scores    = new ArrayList<>();
        avgScores = new ArrayList<>();
        topics    = new ArrayList<>();
        students  = new ArrayList<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                students.clear();

                for (DataSnapshot user:dataSnapshot.getChildren()) {

                    if (!(user.child("School").getValue(String.class).equals(school))) {
                        continue;
                    }

                    Log.d(TAG, "onDataChange: "+user.getKey());
                    if (!user.child("Classes").hasChild(cls)) {
                        toastMessage("No data found for " + cls + " for school " + school);
                        continue;
                    }

                    if (!user.child("Classes").child(cls).hasChild(section)) {
                        toastMessage("No data found for " + cls + " " + section + " in school " + school);
                        continue;
                    }

//                    if (user.hasChild("Name")){
                    String teacherName = user.child("Name").getValue(String.class);

                    for (DataSnapshot student:user.child("Classes").child(cls).child(section).getChildren()){

                        if (student.hasChild("Name")) {
                            String name = student.child("Name").getValue(String.class);
                            Log.d(TAG, "onDataChange: "+name);
                            students.add(name);
                        }

                        if (student.hasChild("Scores")){
                            for (DataSnapshot aSubject:student.child("Scores").getChildren()){
                                String subject = aSubject.getKey();
                                Log.d(TAG, "onDataChange: "+subject);

                                for (DataSnapshot aTopic:aSubject.getChildren()){
                                    String topic = aTopic.getKey();
                                    topics.add(subject+": "+topic);
                                    Log.d(TAG, "onDataChange: "+topic);

                                    String score = aTopic.child("%Score").getValue(String.class) + "%";
                                    scores.add(score);
                                    Log.d(TAG, "onDataChange: "+score);

                                    String avgScore = "("+teacherName + ") " + user.child("Scores").child(cls).child(subject).child(topic)
                                            .child("averageScore").getValue(long.class) + "%";
                                    avgScores.add(avgScore);
                                    Log.d(TAG, "onDataChange: "+avgScore);
                                }
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

                String name = students.get(position);
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
        });
    }

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
