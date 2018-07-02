package com.example.vaibhav.prathamquizzingapp.classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vaibhav on 23/6/18.
 */

public class Quizz implements Serializable{

    private ArrayList<Question> questions;
    private String cls,topic;

    public Quizz(String cls, String topic) {
        questions = new ArrayList<>();
        this.cls = cls;
        this.topic = topic;
    }

    public void addQuestion(String Ques, String Ans, String A, String B, String C, String D, Boolean hI, Boolean hA, Boolean hV){
        Question question = new Question(Ques,Ans,A,B,C,D,hI,hA,hV);
        questions.add(question);
    }

    public Question getQuestion(int Qno){
        return questions.get(Qno-1);
    }

}
