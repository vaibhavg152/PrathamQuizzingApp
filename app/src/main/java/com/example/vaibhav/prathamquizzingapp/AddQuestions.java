package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by vaibhav on 5/6/18.
 */

public class AddQuestions extends Activity {
    private static final String TAG = "AddQuestions";

    private EditText etQues,etA,etB,etC,etD,etAns;
    private Button btnDone,btnAddImage,btnUploadImage;
    private TextView txtTitle;
    private Uri uriImage,uriUploaded;
    private ImageView imgvImage;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private int Qno,REQUEST_CODE = 1,numQues;
    private String quizNo,cls,subject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ques);
        Log.d(TAG, "onCreate: created");

        //initialize android stuff
        etAns = (EditText)findViewById(R.id.etAnsAdd);
        etD = (EditText)findViewById(R.id.etDopt);
        etC = (EditText)findViewById(R.id.etCopt);
        etB = (EditText)findViewById(R.id.etBopt);
        etQues = (EditText)findViewById(R.id.etQuesAdd);
        etA = (EditText)findViewById(R.id.etAoptAdd);
        btnDone = (Button)findViewById(R.id.btnDoneAddQuesssss);
        btnAddImage = (Button)findViewById(R.id.btnAddImage);
        btnUploadImage = (Button)findViewById(R.id.btnUploadImg);
        imgvImage = (ImageView)findViewById(R.id.imgview);
        progressBar = (ProgressBar)findViewById(R.id.proBarbro);
        txtTitle = (TextView)findViewById(R.id.txtQno);      Qno=1;   txtTitle.setText("Question "+Qno);
        storageReference = FirebaseStorage.getInstance().getReference();

        //end

        //getting data
        cls     = myapp.getCls();
        quizNo  = myapp.getTopic();
        subject = myapp.getSubject();
        numQues = getIntent().getIntExtra("number",0);

        Log.d(TAG, "onCreate: "+numQues+cls+quizNo+subject);
        //end

        reference = FirebaseDatabase.getInstance().getReference().child("Pratham").child("Offline").child("Quizzes").child(cls)
                .child(subject).child(quizNo);

        reference.child("Title").setValue(myapp.getQuizTitle());
        reference = reference.child("Questions");
        Log.d(TAG, "onCreate: ref");

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
                    Toast.makeText(AddQuestions.this,"Can't be Empty",Toast.LENGTH_SHORT).show();
                    return;
                }else storeData(Ques,optA,optB,optC,optD,Ans);

                updateData();
            }
        });
    //end
    }

    private void storeData(String Ques, String optA, String optB, String optC, String optD, String ans) {
        Log.d(TAG, "storeData: ");

        DatabaseReference refCurQues = reference.child("Q"+Qno);
        refCurQues.child("Question").setValue(Ques);
        refCurQues.child("A").setValue(optA);
        refCurQues.child("B").setValue(optB);
        refCurQues.child("C").setValue(optC);
        refCurQues.child("D").setValue(optD);
        refCurQues.child("Ans").setValue(ans);
        refCurQues.child("Image").setValue(uriUploaded.toString());
    }

    private void updateData() {
        Log.d(TAG, "updateData: ");

        Qno++;
        txtTitle.setText("Question "+Qno);
        etA.setText("");
        etB.setText("");
        etC.setText("");
        etD.setText("");
        etQues.setText("");
        etAns.setText("");
        imgvImage.setImageURI(null);
        btnAddImage.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.INVISIBLE);

        if (Qno==numQues) btnDone.setText("Finish");

        else if(Qno>numQues){
            Toast.makeText(AddQuestions.this,"Quiz successfully added!",Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(AddQuestions.this,SuperUser.class);
            startActivity(intent1);
        }
    }

    private void uploadImage() {
        StorageReference fileReference = storageReference.child("images/"+System.currentTimeMillis()+"."+getExtension(uriImage));
        fileReference.putFile(uriImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        btnDone.setVisibility(View.VISIBLE);
                        btnUploadImage.setVisibility(View.INVISIBLE);
                        Toast.makeText(AddQuestions.this,"Uploaded Successfully",Toast.LENGTH_SHORT).show();
                        uriUploaded = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddQuestions.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
        else Toast.makeText(AddQuestions.this,"Error! :(",Toast.LENGTH_SHORT).show();
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
