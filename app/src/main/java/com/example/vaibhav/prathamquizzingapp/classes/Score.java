package com.example.vaibhav.prathamquizzingapp.classes;

/**
 * Created by vaibhav on 23/6/18.
 */

public class Score  {

    private String topic,score,avgScore;

    public Score(String topic, String score, String avgScore) {
        this.topic = topic;
        this.score = score;
        this.avgScore = avgScore;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(String avgScore) {
        this.avgScore = avgScore;
    }

}
