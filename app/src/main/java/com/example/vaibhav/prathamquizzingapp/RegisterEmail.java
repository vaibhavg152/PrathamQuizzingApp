package com.example.vaibhav.prathamquizzingapp;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by vaibhav on 4/6/18.
 */

public class RegisterEmail extends AppCompatActivity {
    private static final String TAG = "RegisterEmail";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText etEmail,etPassword;
    private Button btnDone,btngo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resgister_email);
        Log.d(TAG, "onCreate: created");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null) {
                    Log.d(TAG, "onAuthStateChanged: signed in :" + user.getUid());
                    myapp.setUserId(user.getUid());
                    Toast.makeText(RegisterEmail.this,"Successfully signed in as "+user.getEmail(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterEmail.this,Register.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(RegisterEmail.this,"Signed out. :)",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        etEmail =(EditText) findViewById(R.id.etEmailR);
        etPassword = (EditText) findViewById(R.id.etpwdR);
        btnDone = (Button) findViewById(R.id.btnDoneRE);
        btngo = (Button) findViewById(R.id.btnGoooo);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked");
                String Email = etEmail.getText().toString();
                String pwd = etPassword.getText().toString();
                if (Email.length()<1||pwd.length()<6){
                    Toast.makeText(RegisterEmail.this,"Email or password can't be empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(Email,pwd);
                Log.d(TAG, "onClick: signed in ");
                    }
        });

        btngo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
