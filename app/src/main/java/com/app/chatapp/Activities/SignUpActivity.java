package com.app.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chatapp.R;
import com.app.chatapp.ModleClasses.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class SignUpActivity extends AppCompatActivity {

    TextView redirectToSignInBtn, signUpBtn;
    EditText userName, userEmail, userPassword, confirmPassword;

    FirebaseAuth auth;
    FirebaseDatabase db;
    FirebaseStorage storage;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);


        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        redirectToSignInBtn = findViewById(R.id.redirectToSignInBtn);
        signUpBtn = findViewById(R.id.signUpBtn);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        confirmPassword = findViewById(R.id.confirmPassword);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userName.getText().toString();
                String contact = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                String confPassword = confirmPassword.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                progressDialog.show();

                if (TextUtils.isEmpty(name)){
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Please enter your name!", Toast.LENGTH_LONG).show();
                }

                if (TextUtils.isEmpty(contact)){
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Email cannot be empty!", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(password)){
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Password cannot be empty!", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(confPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Password cannot be empty!", Toast.LENGTH_LONG).show();
                }
                else if (!contact.matches(emailPattern)){
                    progressDialog.dismiss();
                    userEmail.setError("Invalid email!");
                    Toast.makeText(SignUpActivity.this, "Enter valid email!", Toast.LENGTH_LONG).show();
                }
                else if (password.length() < 8){
                    progressDialog.dismiss();
                    userPassword.setError("Enter a strong password!");
                    Toast.makeText(SignUpActivity.this, "Enter a strong password!", Toast.LENGTH_LONG).show();
                }
                else if (!password.equals(confPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Passwords don't match!", Toast.LENGTH_LONG).show();
                }
                else {
//                    auth.createUserWithEmailAndPassword(contact, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(task.isSuccessful()){
//                                DatabaseReference dbreference = db.getReference().child("users").child(auth.getUid());
////                                StorageReference storageReference = storage.getReference().child("profileData").child(auth.getUid());
//                                Users users = new Users(auth.getUid(), name, contact);
//                                dbreference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//                                            progressDialog.dismiss();
//                                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
//                                        }
//                                        else {
//                                            progressDialog.dismiss();
//                                            Toast.makeText(SignUpActivity.this, "Error creating user!", Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//                                });
//                           }
//                            else {
//                                progressDialog.dismiss();
//                                Toast.makeText(SignUpActivity.this, "Something went Wrong!", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
                }
            }
        });

        redirectToSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });
    }
}