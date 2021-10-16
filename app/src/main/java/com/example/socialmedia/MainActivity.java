package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextView signup;
    EditText mail,pass;
    Button submit,google,facebook;
    FirebaseAuth mFirebaseAuth;
    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN=123;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String PROFILE = "public_profile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        FacebookSdk.sdkInitialize(MainActivity.this);

        facebook = (Button) findViewById(R.id.button3);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList(EMAIL,PROFILE));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Process Canceled Try Again", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(MainActivity.this, "Exception : "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        google=(Button)findViewById(R.id.button2);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        createRequest();

        signup=(TextView)findViewById(R.id.textView4);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupp=new Intent(MainActivity.this,SignupActivity.class);
                startActivity(signupp);
                finish();
            }
        });

        mFirebaseAuth=FirebaseAuth.getInstance();

        mail=(EditText)findViewById(R.id.email);
        pass=(EditText)findViewById(R.id.editTextTextPassword);

        submit=(Button)findViewById(R.id.button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String StringMail=mail.getText().toString();
                String StringPass=pass.getText().toString();
                String patternmail="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
                String patternpass="^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
                if(StringMail.isEmpty()){
                    mail.setError("Please Enter Email ID");
                }
                if(StringPass.isEmpty()){
                    pass.setError("Please Enter Password");
                }
                if (!StringMail.matches(patternmail)){
                    mail.setError("Enter Valid Email ID");
                }
                if (!StringPass.matches(patternpass)){
                    pass.setError("Password must contain at least eight characters, at least one number and both lower and uppercase letters and special characters");
                }
                if (!StringMail.isEmpty() && !StringPass.isEmpty() && StringMail.matches(patternmail) && StringPass.matches(patternpass)){
                    mFirebaseAuth.signInWithEmailAndPassword(StringMail,StringPass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent success=new Intent(MainActivity.this,HomeActivity.class);
                                success.putExtra("urlphoto","url");
                                startActivity(success);
                                finish();
                            }else {
                                Toast.makeText(MainActivity.this, "Log in Failed : "+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void createRequest(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("584140453476-kjht7h0bnc9nt548egfttse7k25sdpbh.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent success=new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(success);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent success=new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(success);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mFirebaseAuth.getCurrentUser();
        if (user != null){
            Intent done=new Intent(MainActivity.this,HomeActivity.class);
            startActivity(done);
        }
    }
}