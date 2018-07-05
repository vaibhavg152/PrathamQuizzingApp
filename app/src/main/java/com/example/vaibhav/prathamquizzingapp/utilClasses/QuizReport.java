package com.example.vaibhav.prathamquizzingapp.utilClasses;

/**
 * Created by vaibhav on 4/7/18.
 */

public class QuizReport {

    private int quesNum;
    private String Question,correctAns;

    public QuizReport(int quesNum, String question, String correctAns) {
        this.quesNum = quesNum;
        Question = question;
        this.correctAns = correctAns;
    }

    public int getQuesNum() {
        return quesNum;
    }

    public void setQuesNum(int quesNum) {
        this.quesNum = quesNum;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }
}
