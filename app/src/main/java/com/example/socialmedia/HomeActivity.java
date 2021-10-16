package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity {

    Button option,logout;
    TextView name,email;
    ImageView profilephoto;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser user=auth.getCurrentUser();
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        option=(Button)findViewById(R.id.button5);
        logout=(Button)findViewById(R.id.button6);
        name=(TextView)findViewById(R.id.textView5);
        email=(TextView)findViewById(R.id.textView8);
        profilephoto=(ImageView)findViewById(R.id.imageView2);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){
            name.setText("Name : "+signInAccount.getDisplayName());
            email.setText("Gmail : "+signInAccount.getEmail());

            Picasso.with(HomeActivity.this).load(signInAccount.getPhotoUrl()).centerCrop().fit().into(profilephoto);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String Usermail=user.getEmail();
            String id=Usermail.replaceAll("[^a-zA-Z0-9]", "");
            String value = extras.getString("urlphoto");
            Picasso.with(HomeActivity.this).load(value).centerCrop().fit().into(profilephoto);
            DatabaseReference retrive= FirebaseDatabase.getInstance().getReference().child("Database");
            retrive.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    String namee=snapshot.child(id).child("name").getValue().toString();
                    String url=snapshot.child(id).child("photourl").getValue().toString();
                    String emaill=snapshot.child(id).child("email").getValue().toString();
                    name.setText("Name : "+namee);
                    email.setText("Gmail : "+emaill);

                    Picasso.with(HomeActivity.this).load(url).centerCrop().fit().into(profilephoto);

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        }

        if (user!=null){
            name.setText("Name : "+user.getDisplayName());
            email.setText("Gmail : "+user.getEmail());

            Picasso.with(HomeActivity.this).load(user.getPhotoUrl()).centerCrop().fit().into(profilephoto);

        }

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count==0){
                    logout.setVisibility(View.VISIBLE);
                    option.setBackgroundResource(R.drawable.ic_baseline_highlight_off_24);
                    count=1;
                }else {
                    logout.setVisibility(View.GONE);
                    option.setBackgroundResource(R.drawable.ic_baseline_add_circle_outline_24);
                    count=0;
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent signout=new Intent(HomeActivity.this,MainActivity.class);
                startActivity(signout);
                finish();
            }
        });
    }
}