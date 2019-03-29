package com.shyn.zyot.wind.mychatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shyn.zyot.wind.mychatapp.Adapter.MessageAdapter;
import com.shyn.zyot.wind.mychatapp.Model.Message;
import com.shyn.zyot.wind.mychatapp.Model.Room;
import com.shyn.zyot.wind.mychatapp.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView receiverImage;
    private TextView tvReceiverName;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSendMessage;

    private DatabaseReference dbReference;
    private FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // and this
//                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//            }
//        });

        //get current user
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        //find view
        receiverImage = findViewById(R.id.receiverImage);
        tvReceiverName = findViewById(R.id.tvReceiverName);
        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        // set layout manager for recyclerView
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // get receiver and room
        Intent intent = getIntent();
        String receiverID = intent.getStringExtra("receiverID");
        String getRoomID = intent.getStringExtra("roomID");


        // create room if not exist
        final String senderID = fuser.getUid();
        if (getRoomID == null || getRoomID.equals("")) {
            // create room for sender
            getRoomID = createRoom(senderID,receiverID);
        }

        // send message
        final String roomID = getRoomID;
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate input
                String input = etMessage.getText().toString();
                if (input.length() == 0)
                    Toast.makeText(MessageActivity.this, "Empty Message!", Toast.LENGTH_SHORT).show();
                else {
                    sendMessage(input, senderID, roomID);
                    etMessage.setText("");
                }
            }
        });


        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(receiverID);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tvReceiverName.setText(user.getUsername());
                if (user.getImageUrl().equals("default")) {
                    receiverImage.setImageResource(R.mipmap.ic_launcher_round);
                } else
                    Glide.with(MessageActivity.this).load(user.getImageUrl()).into(receiverImage);

                readMessage(user.getImageUrl(), roomID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String createRoom(String senderID, String receiverID) {
//        // create room for sender's chattedUsers
//        DatabaseReference chattedUsers = FirebaseDatabase.getInstance().getReference("ChattedUsers").child(senderID);
//        String roomID = chattedUsers.push().getKey();
//        Room senderRoom = new Room(roomID, receiverID);
//        chattedUsers.child(receiverID).setValue(senderRoom);
//
//        // create room for receiver's chattedUsers
//        chattedUsers = FirebaseDatabase.getInstance().getReference("ChattedUsers").child(receiverID);
//        Room receiverRoom = new Room(roomID, senderID);
//        chattedUsers.child(senderID).setValue(receiverRoom);

        // create room on list
        DatabaseReference userRooms = FirebaseDatabase.getInstance().getReference();
        String roomID = userRooms.push().getKey();  // gen key --> roomID

        // list of sender
        Room senderRoom = new Room(roomID);
        userRooms = FirebaseDatabase.getInstance().getReference("UserRooms").child(senderID);
        userRooms.child(receiverID).setValue(senderRoom);

        // list of receiver
        Room receiverRoom = new Room(roomID);
        userRooms = FirebaseDatabase.getInstance().getReference("UserRooms").child(receiverID);
        userRooms.child(senderID).setValue(receiverRoom);

        return roomID;
    }

    private void sendMessage(String message, String senderID, String roomID) {


        dbReference = FirebaseDatabase.getInstance().getReference("Messages").child(roomID);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("senderID", senderID);

        dbReference.push().setValue(hashMap);
    }

    private void readMessage(final String userImageUrl, String roomID) {
        final List<Message> mMessages = new ArrayList<>();

        dbReference = FirebaseDatabase.getInstance().getReference("Messages").child(roomID);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    mMessages.add(message);
                }

                MessageAdapter messageAdapter = new MessageAdapter(MessageActivity.this, mMessages, userImageUrl);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status(String status) {
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        dbReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
