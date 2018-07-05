package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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

import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;
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
 * Created by vaibhav on 5/6/18.
 */

public class AddQuestions extends Activity {
    private static final String TAG = "AddQuestions";

    private EditText etQues,etA,etB,etC,etD;
    private Button btnDone,btnAddImage,btnUploadImage,btnAddAudio,btnAns;
    private TextView txtTitle;
    private Uri uriImage,uriUploaded,uriAudio;
    private ImageView imgvImage;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private int Qno=1,REQUEST_CODE = 1,numQues;
    private String quizNo,cls,subject,answer="";
    private final int AUDIO_REQUEST=5;
    private boolean hasImage=false,hasAudio=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ques);
        Log.d(TAG, "onCreate: created");

        //initialize android stuff
        etQues          = (EditText)    findViewById(R.id.etQuesAdd);
        etA             = (EditText)    findViewById(R.id.etAoptAdd);
        etB             = (EditText)    findViewById(R.id.etBopt);
        etC             = (EditText)    findViewById(R.id.etCopt);
        etD             = (EditText)    findViewById(R.id.etDopt);
        btnAns          = (Button)      findViewById(R.id.btnChooseAns);
        btnAddImage     = (Button)      findViewById(R.id.btnAddImage);
        btnAddAudio     = (Button)      findViewById(R.id.btnAddAudio);
        btnUploadImage  = (Button)      findViewById(R.id.btnUploadImg);
        btnDone         = (Button)      findViewById(R.id.btnDoneAddQuesssss);
        imgvImage       = (ImageView)   findViewById(R.id.imgview);
        txtTitle        = (TextView)    findViewById(R.id.txtQno);
        progressBar     = (ProgressBar) findViewById(R.id.proBarbro);
        storageReference = FirebaseStorage.getInstance().getReference();

        //getting data
        cls     = myapp.getCls();
        quizNo  = myapp.getTopic();
        subject = myapp.getSubject();
        numQues = getIntent().getIntExtra("number",0);

        Log.d(TAG, "onCreate: "+numQues+cls+quizNo+subject);
        //end

        reference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(cls).child(subject);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(quizNo)){
                    Qno = ((int) dataSnapshot.child(quizNo).child("Questions").getChildrenCount());
                    numQues+=Qno;
                    Qno++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        txtTitle.setText("Question "+Qno);

        reference.child(quizNo).child("Title").setValue(myapp.getQuizTitle());
        reference = reference.child(quizNo).child("Questions");
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

        btnAddAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAudio();
            }
        });

        btnAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseAns();
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

                if (Ques.length()==0||optD.length()==0||optC.length()==0||optB.length()==0||optA.length()==0){
                    toastMessage("Can't be Empty");
                    return;
                }else storeData(Ques,optA,optB,optC,optD,answer);

                updateData();
            }
        });
    //end
    }

    private void chooseAns() {
        Log.d(TAG, "chooseAns: ");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Answer");
        builder.setCancelable(false);
        final String[] array = {"A","B","C","D"};

        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                answer = array[i];
                Log.d(TAG, "onClick: "+answer);
                dialogInterface.dismiss();
                btnDone.setEnabled(true);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void uploadAudio() {

        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,AUDIO_REQUEST);

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
        answer="";
        imgvImage.setImageURI(null);
        btnAddImage.setVisibility(View.VISIBLE);
        btnDone.setEnabled(false);

        if (Qno==numQues) btnDone.setText("Finish");

        else if(Qno>numQues){
            toastMessage("Quiz successfully added!");
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
                        toastMessage("Uploaded Successfully");
                        uriUploaded = taskSnapshot.getDownloadUrl();

                        hasImage = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMessage(e.getMessage());
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

        if (resultCode == RESULT_OK && data!=null && data.getData()!=null) {

            if (requestCode == REQUEST_CODE) {

                Log.d(TAG, "onActivityResult: image");
                uriImage = data.getData();
                imgvImage.setImageURI(uriImage);
                btnUploadImage.setVisibility(View.VISIBLE);
            }

            else if (requestCode == AUDIO_REQUEST) {
                uriAudio = data.getData();
                Log.d(TAG, "onActivityResult: audio");
                StorageReference ref = storageReference.child("audios/" + cls +"/" + subject + "/" + myapp.getQuizTitle() + "Q"+Qno + ".mp3");

                ref.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.child(("Q"+Qno)).child("Audio").setValue(taskSnapshot.getDownloadUrl().toString());
                        hasAudio = true;
                        toastMessage("Audio successfully added! :)");

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        }
        toastMessage("Error! :(");
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

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
