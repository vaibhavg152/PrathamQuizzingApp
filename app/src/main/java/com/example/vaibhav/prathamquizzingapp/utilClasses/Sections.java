package com.example.vaibhav.prathamquizzingapp.utilClasses;

import java.util.ArrayList;

/**
 * Created by vaibhav on 28/6/18.
 */

public class Sections {
    private String title;
    private ArrayList<String> sections;

    public Sections(String title) {
        this.title = title;
        sections = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getArraySections() {
        String[] result = new String[sections.size()];
        for (int i =0;i<result.length;i++){
            result[i] = sections.get(i);
        }
        return result;
    }

    public void setSections(ArrayList<String> sections) {
        this.sections = sections;
    }

}
