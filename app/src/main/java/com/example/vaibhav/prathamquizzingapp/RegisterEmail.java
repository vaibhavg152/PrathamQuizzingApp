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

import com.example.vaibhav.prathamquizzingapp.utilClasses.myapp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by vaibhav on 4/6/18.
 */

public class RegisterEmail extends Activity {
    private static final String TAG = "RegisterEmail";

    private FirebaseAuth mAuth;
    private EditText etEmail,etPassword;
    private Button btnDone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: created");

        mAuth = FirebaseAuth.getInstance();

        etEmail     = (EditText) findViewById(R.id.etEmail);
        etPassword  = (EditText) findViewById(R.id.etPwd);
        btnDone     = (Button)   findViewById(R.id.btnDoneLogin);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked");
                String Email = etEmail.getText().toString();
                String pwd = etPassword.getText().toString();
                if (Email.length()<1||pwd.length()<6){
                    toastMessage("Email or password can't be empty");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(Email,pwd).addOnCompleteListener(RegisterEmail.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            toastMessage("User could not be created! Try again");
                            etEmail.setText("");
                            etPassword.setText("");
                        }
                        else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null) {
                                Log.d(TAG, "onAuthStateChanged: signed in :" + user.getUid());

                                myapp.setUserId(user.getUid());
                                toastMessage("Successfully signed up as "+user.getEmail());
                                Intent intent = new Intent(RegisterEmail.this,HomePage.class);
                                startActivity(intent);
                            }
                        }
                    }
                });

                toastMessage("Please Wait. It might take a few seconds :)");
                Log.d(TAG, "onClick: signed in ");
                    }
        });

    }

    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
