package com.example.vaibhav.prathamquizzingapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.classes.myapp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by vaibhav on 12/6/18.
 */

public class EditQues extends AppCompatActivity {
    private static final String TAG = "EditQues";

    private EditText etQues,etA,etB,etC,etD,etAns;
    private Button btnDone,btnAddImage,btnUploadImage;
    private TextView txtTitle;
    private Uri uriImage;
    private ImageView imgvImage;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private final int REQUEST_CODE = 1;
    private String quizNo,cls,Qno,subject,title,uriUploaded="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ques_screen);
        Log.d(TAG, "onCreate: created");

        //initialize android stuff
        etAns = (EditText)findViewById(R.id.etAnsAdd);
        etD = (EditText)findViewById(R.id.etDopt);
        etC = (EditText)findViewById(R.id.etCopt);
        etB = (EditText)findViewById(R.id.etBopt);
        etQues = (EditText)findViewById(R.id.etQuesAdd);
        etA = (EditText)findViewById(R.id.etAoptAdd);
        btnDone = (Button)findViewById(R.id.btnDoneAddQuesssss);btnDone.setVisibility(View.VISIBLE);
        btnAddImage = (Button)findViewById(R.id.btnAddImage);
        btnAddImage.setText("Change Image");
        btnUploadImage = (Button)findViewById(R.id.btnUploadImg);
        imgvImage = (ImageView)findViewById(R.id.imgview);
        progressBar = (ProgressBar)findViewById(R.id.proBarbro);
        txtTitle = (TextView)findViewById(R.id.txtQno);
        storageReference = FirebaseStorage.getInstance().getReference();
        //end

        //getting data from the intent
        final Intent intent = getIntent();
        cls     = myapp.getCls();
        Qno     = intent.getStringExtra("num");
        quizNo  = myapp.getTopic();
        title   = myapp.getQuizTitle();
        subject = myapp.getSubject();

        txtTitle.setText("Question "+Qno);
        //end

        reference = FirebaseDatabase.getInstance().getReference().child("Pratham").child("Offline").child("Quizzes").
                child(cls).child(subject).child(quizNo).child("Questions").child(Qno);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                etA.setText(dataSnapshot.child("A").getValue(String.class));
                etAns.setText(dataSnapshot.child("Ans").getValue(String.class));
                etB.setText(dataSnapshot.child("B").getValue(String.class));
                etC.setText(dataSnapshot.child("C").getValue(String.class));
                etQues.setText(dataSnapshot.child("Question").getValue(String.class));
                etD.setText(dataSnapshot.child("D").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        //store the entered data
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //extract and check the strings
                String Ques  = etQues.getText().toString().trim();
                String optA  = etA.getText().toString().trim();
                String optB  = etB.getText().toString().trim();
                String optC  = etC.getText().toString().trim();
                String optD  = etD.getText().toString().trim();
                String Ans   = etAns.getText().toString().trim();

                if (Ques.length()==0||optD.length()==0||optC.length()==0||optB.length()==0||optA.length()==0||Ans.length()==0){
                    Toast.makeText(EditQues.this,"Can't be Empty",Toast.LENGTH_SHORT).show();
                    return;
                }else storeData(Ques,optA,optB,optC,optD,Ans, reference);

                goBack();
            }
        });
        //end
    }

    private void goBack() {
        Toast.makeText(EditQues.this,"Updating data. :)",Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(EditQues.this,EditQuiz.class);
        intent1.putExtra("class",cls);
        intent1.putExtra("topic",quizNo);
        startActivity(intent1);
    }

    private void storeData(String Ques, String optA, String optB, String optC, String optD, String ans, DatabaseReference refCurQues) {
        Log.d(TAG, "storeData: ");

        refCurQues.child("Question").setValue(Ques);
        refCurQues.child("A").setValue(optA);
        refCurQues.child("B").setValue(optB);
        refCurQues.child("C").setValue(optC);
        refCurQues.child("D").setValue(optD);
        refCurQues.child("Ans").setValue(ans);
        if (uriUploaded.length()!=0)
            refCurQues.child("Image").setValue(uriUploaded);
    }

    private void uploadImage() {

        StorageReference fileReference = storageReference.child("images/"+System.currentTimeMillis()+"."+getExtension(uriImage));
        fileReference.putFile(uriImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        btnUploadImage.setVisibility(View.INVISIBLE);
                        Toast.makeText(EditQues.this,"Uploaded Successfully",Toast.LENGTH_SHORT).show();
                        uriUploaded = taskSnapshot.getDownloadUrl().toString();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditQues.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                })
        ;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            uriImage = data.getData();
            imgvImage.setImageURI(uriImage);
            btnUploadImage.setVisibility(View.VISIBLE);
            btnAddImage.setVisibility(View.INVISIBLE);
        }
        else Toast.makeText(EditQues.this,"Error! :(",Toast.LENGTH_SHORT).show();
    }

    private void openFileChooser() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageIntent,REQUEST_CODE);
    }

    private String getExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cR.getType(uri));
    }

}
