package com.app.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

public class VerifyOtpActivity extends AppCompatActivity {

    String otpVerificationId, phoneNo, code, resendToken;
    EditText otpNo1, otpNo2, otpNo3, otpNo4, otpNo5, otpNo6;
    TextView verifyOtpBtn, resendOtpBtn;
    ProgressDialog progressDialog;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        otpNo1 = findViewById(R.id.otpNo1);
        otpNo2 = findViewById(R.id.otpNo2);
        otpNo3 = findViewById(R.id.otpNo3);
        otpNo4 = findViewById(R.id.otpNo4);
        otpNo5 = findViewById(R.id.otpNo5);
        otpNo6 = findViewById(R.id.otpNo6);

        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);

        auth = FirebaseAuth.getInstance();

        editTextInput();

        otpVerificationId = getIntent().getStringExtra("verificationId");
        phoneNo = getIntent().getStringExtra("phoneNo");
        resendToken = getIntent().getStringExtra("token");

//        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(VerifyOtpActivity.this, "OTP sent successfully.", Toast.LENGTH_SHORT).show();
//            }
//        });

        verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (otpNo1.getText().toString().trim().isEmpty() ||
                        otpNo2.getText().toString().trim().isEmpty() ||
                        otpNo3.getText().toString().trim().isEmpty() ||
                        otpNo4.getText().toString().trim().isEmpty() ||
                        otpNo5.getText().toString().trim().isEmpty() ||
                        otpNo6.getText().toString().trim().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(VerifyOtpActivity.this, "Enter a valid OTP!", Toast.LENGTH_SHORT).show();
                } else {
                    if (otpVerificationId != null) {
                         code = otpNo1.getText().toString().trim() +
                                otpNo2.getText().toString().trim() +
                                otpNo3.getText().toString().trim() +
                                otpNo4.getText().toString().trim() +
                                otpNo5.getText().toString().trim() +
                                otpNo6.getText().toString().trim();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpVerificationId, code);
                        auth.signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            String phone = phoneNo;
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(VerifyOtpActivity.this, CreateUserWithPhoneActivity.class);
                                            intent.putExtra("phone",phone);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
//                                            Toast.makeText(VerifyOtpActivity.this, "Welcome...", Toast.LENGTH_SHORT).show();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(VerifyOtpActivity.this, "OTP is not Valid!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void editTextInput() {
        otpNo1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                otpNo2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpNo2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                otpNo3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpNo3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                otpNo4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpNo4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                otpNo5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpNo5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                otpNo6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}