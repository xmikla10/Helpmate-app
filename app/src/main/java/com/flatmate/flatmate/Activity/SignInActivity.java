package com.flatmate.flatmate.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;


import com.flatmate.flatmate.Firebase.FirebaseHelperUser;
import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Firebase.NewUser;
import com.flatmate.flatmate.Firebase.NotificationMessage;
import com.flatmate.flatmate.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.UUID;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    //defining view objects
    private EditText editName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private Button buttonLogin;
    private SignInButton googleBut;
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
        googleBut = (SignInButton) findViewById(R.id.signingoogle);
        TextView textView = (TextView) googleBut.getChildAt(0);
        textView.setText("Sign In with Google");

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        googleBut.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        System.out.println("--------> " + gso.getAccount());


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void registerUser(){

        //getting email and password from edit texts
        email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        name  = editName.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,getString(R.string.enter_email),Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,getString(R.string.enter_password),Toast.LENGTH_LONG).show();
            return;
        }

        if( password.length() < 7){
            Toast.makeText(this, R.string.password_must,Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(SignInActivity.this, R.string.successfully_registered,Toast.LENGTH_LONG).show();

                            db = FirebaseDatabase.getInstance().getReference();
                            helper = new FirebaseHelperUser(db);

                            NewUser newUser = new NewUser();
                            NewGroup newFindUser = new NewGroup();

                            String userId = firebaseAuth.getCurrentUser().getUid().toString();
                            newUser.set_ID(userId);
                            newUser.set_name(name);
                            newUser.set_email(email);
                            newUser.set_group("");
                            helper.save(newUser);

                            newFindUser.set_user_email(email);
                            newFindUser.set_user_ID(userId);
                            db.child("user").child("groups").child("find").push().setValue(newFindUser);

                            startActivity(new Intent(SignInActivity.this, LogInActivity.class));
                            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                        }else{
                            //display some message here
                            Toast.makeText(SignInActivity.this, R.string.reg_error,Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

        if ( view == googleBut)
        {
            signIn();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            System.out.println("--------> " + result.getStatus());

            if (result.isSuccess())
            {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                System.out.println("--------> " + result.getSignInAccount());
                firebaseAuthWithGoogle(account);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct)
    {
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful())
                        {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String userId = mAuth.getCurrentUser().getUid().toString();
                            db = FirebaseDatabase.getInstance().getReference();

                            db.child("user").child("users").child(userId).child("data").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    if ( dataSnapshot.getValue() == null)
                                    {
                                        db = FirebaseDatabase.getInstance().getReference();
                                        helper = new FirebaseHelperUser(db);

                                        NewUser newUser = new NewUser();
                                        NewGroup newFindUser = new NewGroup();

                                        String userId = mAuth.getCurrentUser().getUid().toString();
                                        newUser.set_ID(userId);
                                        newUser.set_name(acct.getDisplayName());
                                        newUser.set_email(acct.getEmail());
                                        newUser.set_group("");
                                        helper.save(newUser);

                                        newFindUser.set_user_email(email);
                                        newFindUser.set_user_ID(userId);
                                        db.child("user").child("groups").child("find").push().setValue(newFindUser);
                                    }

                                    startActivity(new Intent(SignInActivity.this, LogInActivity.class));
                                    overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                                    Toast.makeText(SignInActivity.this, "Sign In with Google",Toast.LENGTH_LONG).show();
                                    progressDialog.cancel();
                                }
                                @Override public void onCancelled(DatabaseError databaseError) {}});
                        }
                    }
                });
    }

}