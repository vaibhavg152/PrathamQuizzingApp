package com.example.vaibhav.prathamquizzingapp.classes;

import java.io.Serializable;

/**
 * Created by vaibhav on 23/6/18.
 */

public class Question implements Serializable {

    private String Ques,Ans,A,B,C,D;
    private Boolean hasImage,hasAudio,hasVideo;

    public Question(String ques, String ans, String a, String b, String c, String d, Boolean hasImage, Boolean hasAudio, Boolean hasVideo) {
        Ques = ques;
        Ans = ans;
        A = a;B = b;C = c;D = d;
        this.hasImage = hasImage;
        this.hasAudio = hasAudio;
        this.hasVideo = hasVideo;
    }

    public Boolean getHasImage() {
        return hasImage;
    }

    public Boolean getHasAudio() {
        return hasAudio;
    }

    public Boolean getHasVideo() {
        return hasVideo;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public void setHasAudio(Boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    public void setHasVideo(Boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public String getQues() {
        return Ques;
    }

    public void setQues(String ques) {
        Ques = ques;
    }

    public String getAns() {
        return Ans;
    }

    public void setAns(String ans) {
        Ans = ans;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

}
