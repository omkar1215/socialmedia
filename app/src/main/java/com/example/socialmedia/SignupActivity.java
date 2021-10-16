package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SignupActivity extends AppCompatActivity {

    TextView login;
    ImageView profilephoto;
    EditText name,emailid,pass,repass;
    Button signup;
    int count=0;
    StorageReference storage;
    FirebaseAuth mFirebaseAuthh;
    DatabaseReference MyReff;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        profilephoto=(ImageView)findViewById(R.id.imageView3);
        name=(EditText)findViewById(R.id.editTextTextPersonName2);
        emailid=(EditText)findViewById(R.id.editTextTextEmailAddress);
        pass=(EditText)findViewById(R.id.editTextTextPassword2);
        repass=(EditText)findViewById(R.id.editTextTextPassword3);
        signup=(Button) findViewById(R.id.button4);

        storage=FirebaseStorage.getInstance().getReference();
        mFirebaseAuthh=FirebaseAuth.getInstance();
        MyReff= FirebaseDatabase.getInstance().getReference();

        profilephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),100);
                count=1;
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String StringName=name.getText().toString();
                String StringEmalid=emailid.getText().toString();
                String StringPass=pass.getText().toString();
                String StringPass2=repass.getText().toString();
                if (StringName.isEmpty()){
                    name.setError("Please Enter Your Name");
                }
                if (StringEmalid.isEmpty()){
                    emailid.setError("Please Enter Email ID");
                }
                if (StringPass.isEmpty()){
                    pass.setError("Please Enter Password");
                }
                if (StringPass2.isEmpty()){
                    repass.setError("Please Enter Re-Password");
                }
                if (count==0){
                    Toast.makeText(SignupActivity.this, "Please Upload Your Profile Photo", Toast.LENGTH_SHORT).show();
                }
                if(uri==null){
                    Toast.makeText(SignupActivity.this, "Please Upload Your Profile Photo", Toast.LENGTH_SHORT).show();
                }
                if (!StringPass.equals(StringPass2)){
                    Toast.makeText(SignupActivity.this, "Password Doesn't Matched", Toast.LENGTH_SHORT).show();
                    pass.setError("Password Doesn't Matched");
                    repass.setError("Password Doesn't Matched");
                }
                if (!(StringName.isEmpty() && StringEmalid.isEmpty() && StringPass.isEmpty() && StringPass2.isEmpty() && !StringPass.equals(StringPass2) && count==0 && uri==null)){

                    mFirebaseAuthh.createUserWithEmailAndPassword(StringEmalid,StringPass).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                String id = StringEmalid.replaceAll("[^a-zA-Z0-9]", "");
                                storage.child(StringName+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        storage.child(StringName+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri Downloaduri) {
                                                MyReff.child("Database").child(id).child("photourl").setValue(Downloaduri.toString());
                                            }
                                        });
                                    }
                                });
                                MyReff.child("Database").child(id).child("name").setValue(StringName);
                                MyReff.child("Database").child(id).child("email").setValue(StringEmalid);
                                Intent sucessful=new Intent(SignupActivity.this,HomeActivity.class);
                                startActivity(sucessful);
                                finish();
                            }else {
                                Toast.makeText(SignupActivity.this, "Signup Failed : "+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });

        login=(TextView)findViewById(R.id.textView7);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginn=new Intent(SignupActivity.this,MainActivity.class);
                startActivity(loginn);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK){
            uri=data.getData();
            if (uri!=null){
                Picasso.with(SignupActivity.this).load(uri).centerCrop().fit().into(profilephoto);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mFirebaseAuthh.getCurrentUser();
        if (user != null){
            Intent done=new Intent(SignupActivity.this,HomeActivity.class);
            startActivity(done);
        }
    }
}