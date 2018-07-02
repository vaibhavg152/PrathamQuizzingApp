package com.example.vaibhav.prathamquizzingapp;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.classes.myapp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by vaibhav on 7/6/18.
 */

public class DownloadQuizzes extends AppCompatActivity {
    private static final String TAG = "DownloadQuizzes";

    private DatabaseReference databaseReference;
    private final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Pratham/Quizzes/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_images);
        Log.d(TAG, "onCreate: created");

        final String cls = myapp.getCls();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pratham").child("Offline").child("Quizzes").child(cls);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: ");
                for (DataSnapshot sub:dataSnapshot.getChildren()) {
                    String subject = sub.getKey();
                    String topics = "";
                    for (DataSnapshot ds : sub.getChildren()) {

                        final String title = ds.child("Title").getValue(String.class);
                        topics += title + "\n";
                        for (DataSnapshot ds1 : ds.child("Questions").getChildren()) {

                            final String Qno = ds1.getKey();
                            final String url = ds1.child("Image").getValue(String.class);
                            final String location = path + cls + "/" + subject + "/" + title + "/" + Qno + "/";
                            Log.d(TAG, "location: " + location);
                            downloadImage(url, location);
                            downloadStrings(ds1, location);
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

    private void downloadStrings(DataSnapshot snapshot, String finalpath) {
        Log.d(TAG, "downloadStrings: ");

        String hA="false",hI="false",hV="false";
        if (snapshot.hasChild("Audio"))
            hA = "true";
        if (snapshot.hasChild("Image"))
            hI="true";
        if (snapshot.hasChild("Video"))
            hV = "true";
        String A = snapshot.child("A").getValue(String.class);
        String B = snapshot.child("B").getValue(String.class);
        String C = snapshot.child("C").getValue(String.class);
        String D = snapshot.child("D").getValue(String.class);
        String Ques = snapshot.child("Question").getValue(String.class);
        String Ans = snapshot.child("Ans").getValue(String.class);

        File myDir = new File(finalpath);
        if (!myDir.exists()) myDir.mkdirs();

        File file = new File(myDir,"ques.txt");
        String data = Ques +"\n"+A+"\n"+B+"\n"+C+"\n"+D+"\n"+Ans+"\n"+hA+"\n"+hI+"\n"+hV+"\n";
        SaveData(file,data);
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

    private void downloadImage(String value, final String finalpath) {

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

}
