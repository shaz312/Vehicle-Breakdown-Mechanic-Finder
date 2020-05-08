package com.example.newproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatMechanicActivity extends AppCompatActivity {
    private ChatAdapter adapter;
    private ListView listView;
    private EditText message;
    private EditText usernameEditText;

    private String username,customerOrDriver;

    private DatabaseReference reference;
    private String senderMessageID,receiverMessageID;

    private static final String TAG = "FirebaseChat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_mechanic);

        listView = (ListView) findViewById(R.id.list);
        message = (EditText) findViewById(R.id.message);
        // usernameEditText = (EditText) findViewById(R.id.username);
        ImageButton send = (ImageButton) findViewById(R.id.send_message);
        //Button usernameSaveButton = (Button) findViewById(R.id.save_username);

        customerOrDriver = getIntent().getExtras().getString("customerOrDriver");

        receiverMessageID = getIntent().getStringExtra("mechanic");
        senderMessageID = getIntent().getStringExtra("customer");


        adapter = new ChatAdapter(this, R.id.list);
        listView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Service").child(senderMessageID).child("chat");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage msg = dataSnapshot.getValue(ChatMessage.class);
                adapter.add(msg);
                scrollToBottom();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage msg = new ChatMessage(customerOrDriver, message.getText().toString());
                reference.push().setValue(msg);
                message.setText("");
                scrollToBottom();
            }
        });
/*
        usernameSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameEditText.getText().toString();
                message.requestFocus();
                Toast.makeText(ChatActivity.this, "saved!", Toast.LENGTH_SHORT).show();
            }
        });

 */
    }

    private void scrollToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }
}