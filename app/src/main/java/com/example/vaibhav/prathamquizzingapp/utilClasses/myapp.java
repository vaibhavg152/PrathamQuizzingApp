package com.example.vaibhav.prathamquizzingapp.utilClasses;

import android.app.Application;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by vaibhav on 2/6/18.
 */

public class myapp extends Application {

    private static final String TAG = "myapp";
    private static String userId,school,Sec,Cls,Topic,quizTitle,Subject;
    private static int numClasses;
    private static String[] Clses;
    private static ArrayList<Sections> allSections = new ArrayList<>();

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
        Log.d(TAG, "getClsArray: "+Clses.length);
        return Clses;
    }

    public static void setClses(String[] clses) {
        Clses = clses;
        numClasses = clses.length;
        Log.d(TAG, "setClses: "+numClasses);
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
