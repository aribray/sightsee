package com.example.sightsee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sightsee.ui.MapsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GoogleLoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.login);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.loading);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(GoogleLoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("success", "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                                    updateUI(user);
                                    Intent i=new Intent(getApplicationContext(),DistanceActivity.class);
                                    startActivity(i);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("error", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(GoogleLoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });

            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(GoogleLoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("success", "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    updateUI(user);
                                    Intent i=new Intent(getApplicationContext(),DistanceActivity.class);
                                    startActivity(i);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("error", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(GoogleLoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        });

    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
//            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
//                    user.getEmail(), user.isEmailVerified()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
//            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);

//            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        } else {

        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}


//public class GoogleLoginActivity extends AppCompatActivity implements View.OnClickListener {

//    private static final String TAG = "GoogleActivity";
//    static final int RC_SIGN_IN = 9001;
//
//    private FirebaseAuth mAuth;
//
//    private GoogleSignInClient mGoogleSignInClient;
//    private TextView mStatusTextView;
//    private TextView mDetailTextView;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_google_login);
//
//
//        // Views
////        mStatusTextView = findViewById(R.id.status);
////        mDetailTextView = findViewById(R.id.detail);
//
//        // Button listeners
//        findViewById(R.id.sign_in_button).setOnClickListener(this);
////        findViewById(R.id.sign_out_button).setOnClickListener(this);
//
//
//        // Configure sign-in to request the user's ID, email address, and basic
//        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        // Build a GoogleSignInClient with the options specified by gso.
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        // ...
//        // Initialize Firebase Auth
//
//        mAuth = FirebaseAuth.getInstance();
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//
////        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
////        updateUI(account);
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            // ...
//            case R.id.sign_out_button:
//                signOut();
//                break;
//            case R.id.sign_in_button:
//                signIn();
//                break;
//        }
//    }
//
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//                // [START_EXCLUDE]
//                updateUI(null);
//                // [END_EXCLUDE]
//            }
//        }
//    }
//
//
//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d("firebase_login_attempt", "firebaseAuthWithGoogle:" + acct.getId());
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("success", "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("failure", "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.sign_in_button), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }
//
//    private void updateUI(FirebaseUser user) {
////        hideProgressDialog();
//        if (user != null) {
////            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
////            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
//            startActivity(i);
//            Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
//            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
//        }
//        else {
//            Toast.makeText(getApplicationContext(), "Not Logged In", Toast.LENGTH_SHORT).show();
//
//        }
////            mStatusTextView.setText(R.string.signed_out);
////            mDetailTextView.setText(null);
////
////            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
////            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
//    }
//
//    //TODO: implement a logout feature
//
//    private void signOut() {
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google sign out
//        mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
//                        updateUI(null);
//                    }
//                });
//    }
//
//    private void revokeAccess() {
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google revoke access
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//                    }
//                });
//    }

//    @Override
//    public void onClick(View v) {
//        int i = v.getId();
//        if (i == R.id.sign_in_button) {
//            signIn();
//        } else if (i == R.id.sign_out_button) {
//            signOut();
//        }
//    }




