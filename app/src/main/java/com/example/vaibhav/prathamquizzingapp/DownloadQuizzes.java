package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by vaibhav on 7/6/18.
 */

public class DownloadQuizzes extends Activity {
    private static final String TAG = "DownloadQuizzes";

    private DatabaseReference databaseReference;
    private StorageReference  storageReference;
    private ProgressBar pbImage,pbAudio;
    private TextView tvDetails;
    private final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/Pratham/Quizzes/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        Log.d(TAG, "onCreate: created");

        pbAudio   = (ProgressBar) findViewById(R.id.proBarDownloadAudio);
        pbImage   = (ProgressBar) findViewById(R.id.proBarDownloadImage);
        tvDetails = (TextView)    findViewById(R.id.txtDownloadDetails);

        final String cls = myapp.getCls();
        storageReference  = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(cls);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: ");
                for (DataSnapshot aSubject:dataSnapshot.getChildren()) {
                    String subject = aSubject.getKey();
                    String topics = "";
                    for (DataSnapshot aTopic : aSubject.getChildren()) {

                        final String title = aTopic.child("Title").getValue(String.class);
                        topics += title + "\n";

                        tvDetails.setText(subject +" "+ title);
                        for (DataSnapshot aQues : aTopic.child("Questions").getChildren()) {

                            final String Qno = aQues.getKey();
                            final String location = path + cls + "/" + subject + "/" + title + "/" + Qno + "/";

                                Log.d(TAG, "location: " + location);
                            downloadStrings(aQues, location);

                            pbAudio.setProgress(0);
                            pbImage.setProgress(0);
                            if (aQues.hasChild("Audio"))
                                downloadAudios(title,Qno,location,subject);

                            if (aQues.hasChild("Image")) {
                                final String urlImage  = aQues.child("Image").getValue(String.class);
                                downloadImages(urlImage, location);
                            }
                        }
                    }

                    File myDir = new File(path + cls + "/" +subject);
                    if (!myDir.exists()) myDir.mkdirs();
                    File file = new File(myDir, "topics.txt");
                    SaveData(file, topics);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void downloadAudios(final String title, final String Qno, final String location, String subject) {

        Log.d(TAG, "downloadAudios: ");

        File myDir = new File(location);
        File file  = new File(myDir, "audio.mp3");

        String aaudioPath = "audios/" + myapp.getCls() + "/" + subject + "/" + title + Qno + ".mp3";
        StorageReference ref = storageReference.child(aaudioPath);
        Log.d(TAG, "downloadAudios: "+aaudioPath);

        ref.getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                Log.d(TAG, "onComplete: ");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failed");
                e.printStackTrace();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                pbAudio.setProgress(((int) progress));
            }
        });

    }

    private void downloadStrings(DataSnapshot snapshot, String finalpath) {
        Log.d(TAG, "downloadStrings: ");

        String hA="false",hI="false";
        if (snapshot.hasChild("Audio"))
            hA = "true";
        if (snapshot.hasChild("Image"))
            hI="true";
        String A    = snapshot.child("A").getValue(String.class);
        String B    = snapshot.child("B").getValue(String.class);
        String C    = snapshot.child("C").getValue(String.class);
        String D    = snapshot.child("D").getValue(String.class);
        String Ques = snapshot.child("Question").getValue(String.class);
        String Ans  = snapshot.child("Ans").getValue(String.class);

        File myDir = new File(finalpath);
        if (!myDir.exists()) myDir.mkdirs();

        File file = new File(myDir,"ques.txt");
        String data = Ques +"\n"+A+"\n"+B+"\n"+C+"\n"+D+"\n"+Ans+"\n"+hA+"\n"+hI+"\n";
        SaveData(file,data);
    }

    private void downloadImages(String value, final String finalpath) {

        Log.d(TAG, "downloadImage: ");

        Picasso.with(DownloadQuizzes.this).load(value)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try{

                            File myDir = new File(finalpath);

                            if (!myDir.exists()) myDir.mkdirs();

                            myDir = new File(myDir, "image.jpeg");
                            FileOutputStream out = new FileOutputStream(myDir);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                            pbImage.setProgress(100);
                            out.flush();
                            out.close();

                        }catch (Exception e){
                            Log.d(TAG, "onBitmapLoaded: "+e.getMessage());
                        }
                        Log.d(TAG, "onBitmapLoaded: ");
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.d(TAG, "onBitmapFailed: Failed :(");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Log.d(TAG, "onPrepareLoad: ");
                    }


                })
        ;
    }

    private void SaveData(File file, String data) {

        Log.d(TAG, "SaveData: ");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toastMessage("Error! File Not found :(");
            return;
        }

        try {
            try{
                fos.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                toastMessage("Error! :(");
                return;
            }
        }
        finally {
            try { fos.close();}
            catch (IOException e) {
                e.printStackTrace();
                toastMessage("Error! :(");
                return;
            }
            catch (NullPointerException e){
                e.printStackTrace();
                toastMessage("Error! :(");
                return;
            }
        }
    }

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
