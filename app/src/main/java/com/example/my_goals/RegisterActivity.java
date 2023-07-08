package com.example.my_goals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_goals.models.Session;
import com.example.my_goals.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
        EditText et_name_user, et_surname, et_email, et_password, et_re_password;
        Button bt_submit;
        TextView tv_go_login;
        DatabaseReference dbref;
        private FirebaseAuth auth;
        User user; //for user object;
        String authid; //for IUD of user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Retrieve an instance of  Firebase authentication
        auth= FirebaseAuth.getInstance();
        //getting an instance and to access location in the database
        dbref = FirebaseDatabase.getInstance().getReference("Users");

        //Link impostors to XML Views
        et_name_user = findViewById(R.id.ed_register_name);
        et_surname = findViewById(R.id.et_register_surname);
        et_email = findViewById(R.id.et_register_email);
        et_password = findViewById(R.id.et_register_password);
        et_re_password = findViewById(R.id.et_register_re_password);
        bt_submit = findViewById(R.id.bt_register_submit);
        tv_go_login = findViewById(R.id.tv_register_have_account);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get values from Views
                String first_name_value = et_name_user.getText().toString();
                String surname_value = et_surname.getText().toString();
                String email_value = et_email.getText().toString();
                String password_value = et_password.getText().toString();
                String re_password_value = et_re_password.getText().toString();

                //checking for completing all fields for registration
                if(first_name_value.isEmpty() || surname_value.isEmpty() || email_value.isEmpty() || password_value.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "All fields should be completed", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    //check password
                    if (password_value.length() <6) {
                        et_password.setError("6 characters minimum");
                        return;
                    }
                    //check if entered new passwords matching
                    if(!re_password_value.equals(password_value)){
                        et_re_password.setError("Passwords do not match");
                        return;
                    }

                    //Creating User object and adding data to database
                    auth.createUserWithEmailAndPassword(email_value,password_value)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //to get id of registered user
                                        authid = auth.getUid();
                                        Toast.makeText(RegisterActivity.this, "Successful creating of new account", Toast.LENGTH_LONG).show();
                                        user = new User(first_name_value, surname_value, "User");
                                        //register new user in Realtime DB
                                        dbref.child(authid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                navigateActivityonSuccess();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_LONG).show();

                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });
        tv_go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to activity Sing in
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);

            }
        });
    }

    //to navigate to next activity and to store user data of registered user
    private void navigateActivityonSuccess(){
        Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
        if(user != null) {
            //transfer data about user to next activity
            //Session.SessionDetails.email = email;
            Session.SessionDetails.userType = user.getUserType();
            Session.SessionDetails.userUI = authid;
            Session.SessionDetails.fn = user.getFirstName();
            Session.SessionDetails.sn = user.getLastName();
        }
        startActivity(intent);
    }
}