package com.example.vaibhav.prathamquizzingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.classes.myapp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by vaibhav on 1/6/18.
 */

public class logIn extends Activity {

    private static final String TAG = "logIn";

    private FirebaseAuth auth;
    private EditText etEmail, etPassword;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button btnEnter,btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: reached");

        etEmail     = (EditText) findViewById(R.id.etEmail);
        etPassword  = (EditText) findViewById(R.id.etPwd);
        btnEnter = (Button)   findViewById(R.id.btnDoneLogin);
        btnRegister = (Button)   findViewById(R.id.btnRegister);

        final Boolean admin = getIntent().getBooleanExtra("user",false);
        if (admin) btnRegister.setVisibility(View.INVISIBLE);
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = auth.getCurrentUser();
                if (user!=null) {
                    myapp.setUserId(user.getUid());

                    Log.d(TAG, "onAuthStateChanged: "+admin);
                    if (admin){
                       if (user.getUid().equals("aofrJyc112hbmnQxYbzY8JoZ7yW2")
                               ||myapp.getUserId().equals("lW1eDbqaEzPLuJqIWJ0cbxTgcbz2")){

                           Log.d(TAG, "onAuthStateChanged: vrtified");
                           Intent intent = new Intent(logIn.this,SuperUser.class);
                           startActivity(intent);
                       }
                       else {
                           toastMessage("User is not an admin");
                           auth.signOut();
                       }
                    }
                    else {

                        Intent intent = new Intent(logIn.this, HomePage.class);
                        startActivity(intent);
                    }
                }
                else toastMessage("signed out");
            }
        };

        auth.addAuthStateListener(authStateListener);

        btnEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: Signing in");

                    String Email = etEmail.getText().toString();
                    String pwd = etPassword.getText().toString();
                    if (Email.length() < 1 || pwd.length() < 6) {
                        toastMessage("You must enter something in email and password should be at least 6 characters long");
                        return;
                    }

                    auth.signInWithEmailAndPassword(Email,pwd);
                    toastMessage("Logging in might take a few seconds");
                }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: going to the register screen");

                Intent intent = new Intent(logIn.this,RegisterEmail.class);
                startActivity(intent);
            }
        });

    }


    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}

