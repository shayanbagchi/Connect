package com.app.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chatapp.R;
import com.app.chatapp.Adapters.UserAdapter;
import com.app.chatapp.ModleClasses.Users;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    TabLayout tabLayout;

    RecyclerView userRecyclerView;
    UserAdapter adapter;

    FirebaseAuth auth;
    FirebaseDatabase db;

    ArrayList<Users> usersArrayList;

    float  x1, x2, y1, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);

        LinearLayout layout = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
        layoutParams.weight = 0.5f; // e.g. 0.5f
        layout.setLayoutParams(layoutParams);

        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0: Toast.makeText(HomeActivity.this, "Camera", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(HomeActivity.this, CameraActivity.class);
                            startActivity(i);
                        break;
                    case 1: Toast.makeText(HomeActivity.this, "Chats", Toast.LENGTH_LONG).show();
                        break;
//                    case 2: Toast.makeText(MainActivity.this, "Tab 3", Toast.LENGTH_LONG).show();
//                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        usersArrayList = new ArrayList<>();

        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference dbReference = db.getReference().child("users");

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String currentUserUid = auth.getCurrentUser().getUid();
                    Users users = dataSnapshot.getValue(Users.class);
                    if(!users.getUid().equals(currentUserUid)){
                        usersArrayList.add(users);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("Error", "Data retrieval failed! - " + error);
            }
        });

//        usersArrayList.add(new Users(null, "Shayan","bagchishayan.9@gmail.com"));

        adapter = new UserAdapter(HomeActivity.this,usersArrayList);
        userRecyclerView.setAdapter(adapter);


        if (auth.getCurrentUser() == null){
            startActivity(new Intent(HomeActivity.this, SignInActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout){
            Dialog dialog = new Dialog(HomeActivity.this, R.style.Dialog);
            dialog.setContentView(R.layout.dialog_layout);

            TextView no_btn, yes_btn;

            no_btn = dialog.findViewById(R.id.no_btn);
            yes_btn = dialog.findViewById(R.id.yes_btn);

            no_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    auth.signOut();
                    finish();
//                    System.exit(0);
                }
            });
            dialog.show();
        }
        if (id == R.id.profile){
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 < x2){
                    Intent i = new Intent(HomeActivity.this, CameraActivity.class);
                    startActivity(i);
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    tab.select();
                }
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}