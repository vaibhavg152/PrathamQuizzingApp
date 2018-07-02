package com.example.vaibhav.prathamquizzingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vaibhav.prathamquizzingapp.classes.myapp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by vaibhav on 1/6/18.
 */

public class logIn extends AppCompatActivity {

    private static final String TAG = "logIn";

    private FirebaseAuth auth;
    private EditText etEmail, etPassword;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference reference;
    private Button enter,btnRegister;
    private final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            + "/Pratham/User/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        Log.d(TAG, "onCreate: reached");

        reference = FirebaseDatabase.getInstance().getReference();

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPwd);
        enter = (Button) findViewById(R.id.btnDone);
        btnRegister = (Button) findViewById(R.id.btnRegister);

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
                           Toast.makeText(logIn.this, "Invalid user", Toast.LENGTH_SHORT).show();
                           auth.signOut();
                       }
                    }
                    else {

                        Intent intent = new Intent(logIn.this, HomePage.class);
                        startActivity(intent);
                    }
                }
                else Toast.makeText(logIn.this,"signed out",Toast.LENGTH_SHORT).show();
            }
        };

        auth.addAuthStateListener(authStateListener);

        enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: Signing in");

                    String Email = etEmail.getText().toString();
                    String pwd = etPassword.getText().toString();
                    if (Email.length() < 1 || pwd.length() < 6) {
                        Toast.makeText(logIn.this, "You must enter something in email and password should be at least" +
                                " 6 characters long", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    auth.signInWithEmailAndPassword(Email,pwd);

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

}

