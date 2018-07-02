package com.example.vaibhav.prathamquizzingapp.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vaibhav on 11/6/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME="quizzes.db",COL1="QUESTION",COL2="A",COL3="B",COL4="C",COL5="D",COL6="ANS",
            COL7="HASiMAGE",COL8="HASaUIO",COL9="HASvIDEO";
    private String TABLE_NAME;

    public DatabaseHelper(Context context, String TABLE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        this.TABLE_NAME = TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create = "CREATE TABLE "+TABLE_NAME+" (QNO INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL1+" TEXT, "+COL2+" TEXT, "+ COL3+" TEXT, "+COL4 +" TEXT, "+COL5+" TEXT, "+COL6+" TEXT, " +
                COL7 +" INTEGER, " +COL8+ " INTEGER, " +COL9+ "INTEGER)";
        sqLiteDatabase.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public Boolean addQuestion(String a,String b,String c,String d,String ques,String ans,Boolean hI,Boolean hA,Boolean hV){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1,ques);
        contentValues.put(COL2,a);
        contentValues.put(COL3,b);
        contentValues.put(COL4,c);
        contentValues.put(COL5,d);
        contentValues.put(COL6,ans);
        contentValues.put(COL7,hI);
        contentValues.put(COL8,hA);
        contentValues.put(COL9,hV);

        long result = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        return (result!=-1);
    }

    public String getTitle(){
        return TABLE_NAME;
    }

    public void deleteQuiz(){
        getWritableDatabase().execSQL("DROP TABLE "+TABLE_NAME);
    }

    public Cursor showData(){
        return getWritableDatabase().rawQuery("SELECT * FROM "+TABLE_NAME,null);
    }

}
