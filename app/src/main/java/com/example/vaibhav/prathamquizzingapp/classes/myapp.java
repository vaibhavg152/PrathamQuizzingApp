package com.example.vaibhav.prathamquizzingapp.classes;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vaibhav on 2/6/18.
 */

public class myapp extends Application {

    private static final String TAG = "myapp";
    private static String userId,school,Sec,Cls,Topic,quizTitle,Subject;
    private static int numClasses;
    private static ArrayList<String> Clses = new ArrayList<>();
    private static ArrayList<Sections> allSections = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static ArrayList<String> getClses() {
        return Clses;
    }

    public static String getTopic() {
        return Topic;
    }

    public static void setTopic(String topic) {
        Topic = topic;
    }

    public static String getQuizTitle() {
        return quizTitle;
    }

    public static void setQuizTitle(String quizTitle) {
        myapp.quizTitle = quizTitle;
    }

    public static String getSubject() {
        return Subject;
    }

    public static void setSubject(String subject) {
        Subject = subject;
    }

    public static String[] getClsArray(){
        String[] result = new String[numClasses];
        for (int i=0;i<numClasses;i++){
            result[i] = Clses.get(i);
        }
        return result;
    }

    public static void setClses(ArrayList<String> clses) {
        Clses = clses;
        numClasses = clses.size();
    }

    public static int getNumClasses(){
        return numClasses;
    }

    public static void clearSections(){
        allSections.clear();
    }

    public static String[] getSections(String title) {
        for (Sections s:allSections)
            if (s.getTitle().equals(title))
                return s.getArraySections();

        return new String[0];
    }

    public static String getSchool() {
        return school;
    }

    public static void setSchool(String sch) {
        school = sch;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userID) {
        userId = userID;
    }

    public static String getSec() {
        return Sec;
    }

    public static void setSec(String sec) {
        Sec = sec;
    }

    public static String getCls() {
        return Cls;
    }

    public static void setCls(String cls) {
        Cls = cls;
    }

    public static void addSection(Sections sections) {

        for (Sections s:allSections){
            if (s.getTitle().equals(sections.getTitle())) {
                Log.d(TAG, "addSection: couldnt be added! :(");
                return;
            }
        }
        allSections.add(sections);
    }

}
