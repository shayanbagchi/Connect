package com.app.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chatapp.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneSignUpActivity extends AppCompatActivity {

    String phoneNumber;
    CountryCodePicker ccPicker;
    EditText phoneNo;
    TextView phnRegBtn;
    ProgressBar progressBar;

    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_sign_up);

        ccPicker = findViewById(R.id.ccPicker);
        phoneNo = findViewById(R.id.phoneNo);
        phnRegBtn = findViewById(R.id.phnRegBtn);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        ccPicker.registerCarrierNumberEditText(phoneNo);

        phnRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = ccPicker.getFullNumberWithPlus().trim();
                if (phoneNumber.isEmpty()) {
                    Toast.makeText(PhoneSignUpActivity.this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
                } else if (phoneNumber.length() != 13) {
                    Log.i("Phone No. - ",phoneNumber);
                    Toast.makeText(PhoneSignUpActivity.this, "Type a valid Phone Number", Toast.LENGTH_LONG).show();
                } else {
                    otpSend();
                }

//                Intent intent = new Intent(PhoneSignUpActivity.this, VerifyOtpActivity.class);
//                intent.putExtra("mobileNo", ccPicker.getFullNumberWithPlus().trim());
//                startActivity(intent);
            }
        });
    }

    private void otpSend() {

        progressBar.setVisibility(View.VISIBLE);
        phnRegBtn.setVisibility(View.INVISIBLE);

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("Credential", "onVerificationCompleted:" + credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                phnRegBtn.setVisibility(View.VISIBLE);
                Toast.makeText(PhoneSignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                String phone = phoneNumber;
                progressBar.setVisibility(View.GONE);
                phnRegBtn.setVisibility(View.VISIBLE);
                Toast.makeText(PhoneSignUpActivity.this, "OTP is sent successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PhoneSignUpActivity.this, VerifyOtpActivity.class);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("resendToken", token);
                intent.putExtra("phoneNo", phone);
                Log.d("Code:", "onCodeSent:" + verificationId);
                startActivity(intent);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}