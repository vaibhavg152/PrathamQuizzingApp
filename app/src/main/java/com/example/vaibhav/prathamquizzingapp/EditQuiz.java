package com.example.vaibhav.prathamquizzingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.classes.myapp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditQuiz extends AppCompatActivity {

    private static final String TAG = "EditQuiz";
    private ListView listView;
    private EditText txtTitle;
    private Button btnChange;
    private ArrayList<String> questions;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);
        Log.d(TAG, "onCreate: ");

        final String cls = myapp.getCls();
        final String topic = getIntent().getStringExtra("topic");
        final String title = getIntent().getStringExtra("title");
        final String subject = getIntent().getStringExtra("subject");
        reference = FirebaseDatabase.getInstance().getReference().child("Pratham").child("Offline").child("Quizzes")
                .child(cls).child(subject).child(topic);

        Log.d(TAG, "onCreate: "+cls+subject+topic);
        txtTitle  = (EditText) findViewById(R.id.etTileEQs);
        listView  = (ListView) findViewById(R.id.listEditQz);
        btnChange = (Button)   findViewById(R.id.btnblahplz);
        questions = new ArrayList<>();

        txtTitle.setText(title);

        try {

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    questions.clear();
                    for (DataSnapshot ds : dataSnapshot.child("Questions").getChildren()) {
                        questions.add(ds.getKey());

                    }
                    ArrayAdapter adapter = new ArrayAdapter(EditQuiz.this, android.R.layout.simple_expandable_list_item_1, questions);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(EditQuiz.this, EditQues.class);
                            intent.putExtra("class", cls);
                            intent.putExtra("topic", topic);
                            intent.putExtra("subject",subject);
                            intent.putExtra("num", questions.get(i));
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = txtTitle.getText().toString().trim();
                if (newTitle.length()!=0){
                    reference.child("Title").setValue(newTitle);
                    Toast.makeText(EditQuiz.this,"Title changed :)",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(EditQuiz.this,"Title cant be empty",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
