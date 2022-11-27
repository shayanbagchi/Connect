package com.app.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chatapp.ModleClasses.Messages;
import com.app.chatapp.Adapters.MessagesAdapter;
import com.app.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReceiverName, ReceiverUid, ReceiverImage, SenderUid;
    CircleImageView profileImage;
    TextView receiverName;

    EditText sendMessage;
    CardView sendBtn;

    FirebaseAuth auth;
    FirebaseDatabase db;

    String senderRoom, receiverRoom;

    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;

    MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar chat_toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        ReceiverName = getIntent().getStringExtra("name");
        ReceiverUid = getIntent().getStringExtra("uid");
        ReceiverImage = getIntent().getStringExtra("RecieverImage");

        receiverName = findViewById(R.id.receiverName);
        profileImage = findViewById(R.id.profileImage);

        receiverName.setText("" + ReceiverName);
        Picasso.get().load(ReceiverImage).into(profileImage);

        SenderUid = auth.getUid();
        sendMessage = findViewById(R.id.sendMessage);
        sendBtn = findViewById(R.id.sendBtn);

        messagesArrayList = new ArrayList<>();

        messageAdapter = findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter = new MessagesAdapter(ChatActivity.this, messagesArrayList);
        messageAdapter.setAdapter(adapter);

        senderRoom = SenderUid + ReceiverUid;
        receiverRoom = ReceiverUid + SenderUid;

        DatabaseReference chatReference = db.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                    Log.i("LastMessage", "" + messagesArrayList);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = sendMessage.getText().toString();
                if (message.isEmpty()) {
                    sendBtn.setEnabled(false);
                    Toast.makeText(ChatActivity.this, "Message can't be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage.setText("");
                Date date = new Date();

                Messages messages = new Messages(message, SenderUid, date.getTime());

                DatabaseReference dbReference = db.getReference().child("chats");

                dbReference
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dbReference
                                        .child(receiverRoom)
                                        .child("messages")
                                        .push()
                                        .setValue(messages)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout) {
            Dialog dialog = new Dialog(ChatActivity.this, R.style.Dialog);
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

        return super.onOptionsItemSelected(item);
    }
}