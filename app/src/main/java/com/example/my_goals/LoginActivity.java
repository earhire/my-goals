package com.example.my_goals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_goals.models.Session;
import com.example.my_goals.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    EditText ed_username, ed_password;
    Button bt_login;
    TextView tv_go_register;

    //Firebase declaration
    DatabaseReference dbRef;
    ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Link impostors to XML Views
        ed_username = findViewById(R.id.ed_login_username);
        ed_password = findViewById(R.id.ed_login_password);
        bt_login = findViewById(R.id.bt_login_login);
        tv_go_register = findViewById(R.id.tv_login_go_register);

        FirebaseAuth auth;
        // Initialise Firebase Auth
        auth = FirebaseAuth.getInstance();

        //Log in to the account
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_value = ed_username.getText().toString();
                String password_value = ed_password.getText().toString();

                // verifying of completing fields
                if (TextUtils.isEmpty(email_value)){
                    ed_username.setError("Email required");
                    return;
                }
                if (TextUtils.isEmpty(password_value)){
                    ed_username.setError("Password is required");
                    return;
                }
                String data_user, data_password;
                //authentication with email and password
                auth.signInWithEmailAndPassword(email_value,password_value)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //in case of successful authentication
                                    FirebaseUser user = auth.getCurrentUser();
                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    //to retrieve data about user
                                    dbRef = FirebaseDatabase.getInstance().getReference();
                                    dbRef.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for(DataSnapshot snapShot : dataSnapshot.getChildren()){
                                                //store user details to use in other activities
                                                Session.SessionDetails.userUI = uid;
                                                Session.SessionDetails.userType = dataSnapshot.child("userType").getValue().toString();
                                                Session.SessionDetails.fn = dataSnapshot.child("firstName").getValue().toString();
                                                Session.SessionDetails.sn = dataSnapshot.child("lastName").getValue().toString();
                                                Session.SessionDetails.email = ed_username.getText().toString();
                                                //showing user message about successful authentication
                                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                //start new activity
                                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //in case failure read user details from db
                                            System.out.println("The read failed: " + databaseError.getCode());
                                        }
                                    });

                                }
                                else {
                                    //in case user did pass authentication requirements
                                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


        tv_go_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}