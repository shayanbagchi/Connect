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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    String email, password;
    TextView redirectToSignUpBtn, signInBtn, guestSignInBtn;
    EditText loginEmail, loginPassword;
    FirebaseAuth auth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        redirectToSignUpBtn = findViewById(R.id.redirectToSignUpBtn);
        signInBtn = findViewById(R.id.signInBtn);
//        guestSignInBtn = findViewById(R.id.guestSignInBtn);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = loginEmail.getText().toString();
                password = loginPassword.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                progressDialog.show();

                if (TextUtils.isEmpty(email)){
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "Email cannot be empty!", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(password)){
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "Password cannot be empty!", Toast.LENGTH_LONG).show();
                }
                else if (!email.matches(emailPattern)){
                    progressDialog.dismiss();
                    loginEmail.setError("Invalid email!");
                    Toast.makeText(SignInActivity.this, "Enter valid email!", Toast.LENGTH_LONG).show();
                }
                else if (password.length() < 8){
                    progressDialog.dismiss();
                    loginPassword.setError("Enter a strong password!");
                    Toast.makeText(SignInActivity.this, "Enter a strong password!", Toast.LENGTH_LONG).show();
                }
                else {
                    signInUser(email, password);
                }
            }
        });

        email = new String("John@gmail.com");
        password = new String("Password");

//        guestSignInBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signInUser();
//            }
//        });

        redirectToSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }

    public void signInUser(String email, String password){
        auth.signInWithEmailAndPassword(this.email, this.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    public void signInUser(){
//        auth.signInAnonymously()
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            progressDialog.dismiss();
//                            Toast.makeText(SignInActivity.this, "Guest Sign IN Successful!", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
//                        } else {
//                            progressDialog.dismiss();
//                            Toast.makeText(SignInActivity.this, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
}