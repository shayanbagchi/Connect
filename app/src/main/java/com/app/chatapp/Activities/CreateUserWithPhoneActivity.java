package com.app.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chatapp.ModleClasses.Users;
import com.app.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateUserWithPhoneActivity extends AppCompatActivity {

    String phoneNo;
    EditText userName;
    TextView nextBtn;
    CircleImageView profile_image;

    public static final int PICK_IMAGE = 1;
    Uri imageUri;
    String imageURI;

    FirebaseAuth auth;
    FirebaseDatabase db;
    FirebaseStorage storage;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_with_phone);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        userName = findViewById(R.id.userName);
        nextBtn = findViewById(R.id.nextBtn);
        profile_image = findViewById(R.id.profile_image);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        phoneNo = getIntent().getStringExtra("phone");

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userName.getText().toString();
                String status = "Available";
                DatabaseReference dbreference = db.getReference().child("users").child(auth.getUid());
                StorageReference storageReference = storage.getReference().child("profileData").child(auth.getUid());

                if(imageUri != null){
                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageURI = uri.toString();
                                        Users users = new Users(auth.getUid(), name, phoneNo, status, imageURI);
                                        dbreference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(CreateUserWithPhoneActivity.this, HomeActivity.class));
                                                }
                                                else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(CreateUserWithPhoneActivity.this, "Error creating user!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    imageURI = "https://firebasestorage.googleapis.com/v0/b/connect-abe38.appspot.com/o/profile_user_icon.jpg?alt=media&token=812d88de-9174-4d63-9788-3e42d5617d4e";
                    Users users = new Users(auth.getUid(), name, phoneNo, status, imageURI);
                    dbreference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                startActivity(new Intent(CreateUserWithPhoneActivity.this, HomeActivity.class));
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(CreateUserWithPhoneActivity.this, "Error creating user!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if(data != null){
                imageUri = data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}