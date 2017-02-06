package com.flatmate.flatmate.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;


import com.flatmate.flatmate.Firebase.FirebaseHelperUser;
import com.flatmate.flatmate.Firebase.NewUser;
import com.flatmate.flatmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText editName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private Button buttonLogin;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    DatabaseReference db;
    FirebaseHelperUser helper;
    String email;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_layout);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        editName = (EditText) findViewById(R.id.editPersonName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
    }

    private void registerUser(){

        //getting email and password from edit texts
        email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        name  = editName.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        if( password.length() < 7){
            Toast.makeText(this,"Password must have at least 7 characters",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(SignInActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();

                            db = FirebaseDatabase.getInstance().getReference();
                            helper = new FirebaseHelperUser(db);

                            NewUser newUser = new NewUser();
                            String userId = firebaseAuth.getCurrentUser().getUid().toString();
                            newUser.set_ID(userId);
                            newUser.set_name(name);
                            newUser.set_email(email);
                            newUser.set_group("");
                            helper.save(newUser);

                            startActivity(new Intent(SignInActivity.this, LogInActivity.class));
                            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                        }else{
                            //display some message here
                            Toast.makeText(SignInActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View view) {

        if(view == buttonSignup){
            registerUser();
        }

        if(view == buttonLogin){
            //open login activity when user taps on the already registered textview
            startActivity(new Intent(this, LogInActivity.class));
            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
        }

    }
}