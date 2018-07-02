package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by vaibhav on 27/6/18.
 */

public class UploadStatus extends Activity {
    private static final String TAG = "UploadStatus";

    private ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter adapter;
    private DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_name);

        listView  = (ListView) findViewById(R.id.listviewNames);
        arrayList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Pratham").child("Offline").child("Update Report");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot school : dataSnapshot.getChildren()) {
                        String curSchool = school.getKey();
                        for (DataSnapshot classSec : school.getChildren()) {
                            String curStatus = curSchool + " Class" + classSec.getKey() + " " + classSec.getValue(String.class);
                            arrayList.add(curStatus);
                        }
                    }

                    adapter = new ArrayAdapter(UploadStatus.this, android.R.layout.simple_expandable_list_item_1, arrayList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
