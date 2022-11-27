package com.app.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chatapp.ModleClasses.Users;
import com.app.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profile_image;
    ImageView editUserNameBtn, editUserAboutBtn;
    EditText profileUserName, userStatus, userPhone;
    TextView updateProfileBtn;

    String name, status, contact, image;

    FirebaseAuth auth;
    FirebaseDatabase db;
    FirebaseStorage storage;

    public static final int PICK_IMAGE = 1;
    Uri setProfileImgURI;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        profile_image = findViewById(R.id.profile_image);

        profileUserName = findViewById(R.id.profileUserName);
        profileUserName.setFocusable(false);
        editUserNameBtn = findViewById(R.id.editUserNameBtn);

        userStatus= findViewById(R.id.userStatus);
        userStatus.setFocusable(false);
        editUserAboutBtn = findViewById(R.id.editUserAboutBtn);

        userPhone= findViewById(R.id.userPhone);
        userPhone.setFocusable(false);

        updateProfileBtn = findViewById(R.id.updateProfileBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();

        DatabaseReference dbreference = db.getReference().child("users").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("/profileData").child(auth.getUid());
        dbreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contact = snapshot.child("contact").getValue().toString();
                name = snapshot.child("name").getValue().toString();
                status = snapshot.child("status").getValue().toString();
                image = snapshot.child("imageUri").getValue().toString();

                Picasso.get().load(image).into(profile_image);
                profileUserName.setText(name);
                userStatus.setText(status);
                userPhone.setText(contact);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        editUserNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileUserName.setFocusableInTouchMode(true);
                profileUserName.setTextColor(Color.BLACK);
                profileUserName.setText(name);
            }
        });

        editUserAboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userStatus.setFocusableInTouchMode(true);
                userStatus.setTextColor(Color.BLACK);
                userStatus.setText(status);
            }
        });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = profileUserName.getText().toString();
                String phoneNo = contact;
                String status = userStatus.getText().toString();

                if(setProfileImgURI == null){
                    setProfileImgURI = Uri.parse(image);
                }

                storageReference.putFile(setProfileImgURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUri = uri.toString();
                                Users users = new Users(auth.getUid(), name, phoneNo, status, imageUri);
                                dbreference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.i("name", "" + name);
                                            Toast.makeText(ProfileActivity.this, "Success!", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Update failed!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if(data != null){
                setProfileImgURI = data.getData();
                profile_image.setImageURI(setProfileImgURI);
            }
        }
    }
}