package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.app.AlertDialog;
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
 * Created by vaibhav on 12/6/18.
 */

public class EditQues extends Activity {
    private static final String TAG = "EditQues";

    private EditText etQues,etA,etB,etC,etD;
    private Button btnDone,btnAddImage,btnUploadImage,btnAns,btnAudio;
    private TextView txtTitle;
    private Uri uriImage,uriAudio;
    private ImageView imgvImage;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private final int REQUEST_CODE = 1,AUDIO_REQUEST=5;
    private String quizNo,cls,Qno,subject,title,uriUploaded="",answer="";

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
        btnAudio        = (Button)      findViewById(R.id.btnAddAudio);
        btnAddImage     = (Button)      findViewById(R.id.btnAddImage);
        btnUploadImage  = (Button)      findViewById(R.id.btnUploadImg);
        btnDone         = (Button)      findViewById(R.id.btnDoneAddQuesssss);
        imgvImage       = (ImageView)   findViewById(R.id.imgview);
        progressBar     = (ProgressBar) findViewById(R.id.proBarbro);
        txtTitle        = (TextView)    findViewById(R.id.txtQno);
        btnDone.setVisibility(View.VISIBLE);
        btnAddImage.setText("Change Image");
        //end

        //getting data from the intent
        final Intent intent = getIntent();
        Qno     = intent.getStringExtra("num");
        cls     = myapp.getCls();
        quizNo  = myapp.getTopic();
        title   = myapp.getQuizTitle();
        subject = myapp.getSubject();

        txtTitle.setText("Question "+Qno);
        //end

        storageReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(cls).child(subject).child(quizNo)
                .child("Questions").child(Qno);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                etQues.setText(dataSnapshot.child("Question").getValue(String.class));
                etA.setText(dataSnapshot.child("A").getValue(String.class));
                etB.setText(dataSnapshot.child("B").getValue(String.class));
                etC.setText(dataSnapshot.child("C").getValue(String.class));
                etD.setText(dataSnapshot.child("D").getValue(String.class));
                answer = dataSnapshot.child("Ans").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseAns();
            }
        });

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAudio();
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

                if (Ques.length()==0||optD.length()==0||optC.length()==0||optB.length()==0||optA.length()==0){
                    toastMessage("Can't be Empty");
                    return;
                }else storeData(Ques,optA,optB,optC,optD,answer, reference);

                finish();
            }
        });
        //end
    }

    private void uploadAudio() {

        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,AUDIO_REQUEST);

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
                        btnUploadImage.setVisibility(View.INVISIBLE);
                        toastMessage("Uploaded Successfully");
                        uriUploaded = taskSnapshot.getDownloadUrl().toString();
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
