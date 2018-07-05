package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditQuiz extends Activity {

    private static final String TAG = "EditQuiz";
    private ListView listView;
    private EditText txtTitle;
    private Button btnChange,btnAddQues;
    private ArrayList<String> questions;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);
        Log.d(TAG, "onCreate: ");

        final String cls = myapp.getCls();
        final String topic = myapp.getTopic();
        final String title = myapp.getQuizTitle();
        final String subject = myapp.getSubject();
        reference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(cls).child(subject).child(topic);

        Log.d(TAG, "onCreate: " + cls + subject + topic);
        txtTitle = (EditText) findViewById(R.id.etTileEQs);
        listView = (ListView) findViewById(R.id.listEditQz);
        btnChange = (Button) findViewById(R.id.btnblahplz);
        btnAddQues = (Button) findViewById(R.id.btnAddQuesEQ);
        questions = new ArrayList<>();

        txtTitle.setText(title);

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
                        intent.putExtra("num", questions.get(i));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = txtTitle.getText().toString().trim();
                if (newTitle.length() != 0) {
                    reference.child("Title").setValue(newTitle);
                    toastMessage("Title changed :)");
                } else
                    toastMessage("Title cant be empty");
            }
        });

        btnAddQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQues();
            }
        });

    }

    private void addQues() {

        Log.d(TAG, "enterNumber: ");
        final Dialog dialog = new Dialog(EditQuiz.this);
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
                int numQuestions;
                try {
                    numQuestions = Integer.parseInt(number);
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    toastMessage("Not a number!");
                    return;
                }
                Intent intent = new Intent(EditQuiz.this,AddQuestions.class);
                intent.putExtra("number",numQuestions);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
