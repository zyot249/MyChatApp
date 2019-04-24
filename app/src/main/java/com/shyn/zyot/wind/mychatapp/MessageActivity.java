package com.shyn.zyot.wind.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.shyn.zyot.wind.mychatapp.Model.RoomDetail;
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
    private TextView tvReceiverStatus;

    private DatabaseReference dbReference;
    private FirebaseUser fuser;

    private ValueEventListener seenListener;

    //    ServiceAPI serviceAPI;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                dbReference.removeEventListener(seenListener);
            }
        });

        // init api service for notification
//        serviceAPI = Client.getClient("https://fcm.googleapis.com/").create(ServiceAPI.class);

        //get current user
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        //find view
        receiverImage = findViewById(R.id.receiverImage);
        tvReceiverName = findViewById(R.id.tvReceiverName);
        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        tvReceiverStatus = findViewById(R.id.tvReceiverStatus);

        // set layout manager for recyclerView
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // get receiver and room
        Intent intent = getIntent();
        final String receiverID = intent.getStringExtra("receiverID");
        String getRoomID = intent.getStringExtra("roomID");


        // create room if not exist
        final String senderID = fuser.getUid();
        if (getRoomID == null || getRoomID.equals("")) {
            // create room for sender
            getRoomID = createRoom(senderID, receiverID);
        }

        // send message
        final String roomID = getRoomID;
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                notify = true;
                // validate input
                String input = etMessage.getText().toString();
                if (input.length() == 0)
                    Toast.makeText(MessageActivity.this, "Empty Message!", Toast.LENGTH_SHORT).show();
                else {
                    sendMessage(input, senderID, roomID, receiverID);
                    etMessage.setText("");
                }
            }
        });


        // show info of receiver
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(receiverID);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tvReceiverName.setText(user.getUsername());
                if (!user.getStatus().isEmpty()) {
                    tvReceiverStatus.setVisibility(View.VISIBLE);
                    tvReceiverStatus.setText(user.getStatus());
                } else
                    tvReceiverStatus.setVisibility(View.GONE);
                if (user.getImageUrl().equals("default")) {
                    receiverImage.setImageResource(R.mipmap.ic_launcher_round);
                } else
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(receiverImage);

                readMessage(user.getImageUrl(), roomID);
                setLastMessage(roomID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(roomID, fuser.getUid());
    }

    private void seenMessage(String roomID, final String senderID) {
        dbReference = FirebaseDatabase.getInstance().getReference("Messages").child(roomID);
        seenListener = dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (!message.getSentBy().equals(senderID)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLastMessage(final String roomID) {
        dbReference = FirebaseDatabase.getInstance().getReference("Messages").child(roomID);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastMessageID = "";
                // get the latest msg
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    lastMessageID = snapshot.getKey();
                }

                // update last msg of room
                DatabaseReference roomDetail = FirebaseDatabase.getInstance().getReference("RoomDetail").child(roomID);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("lastMsgID", lastMessageID);
                roomDetail.updateChildren(hashMap);
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
        dbReference = FirebaseDatabase.getInstance().getReference();
        String roomID = dbReference.push().getKey();  // gen key --> roomID

        // list of sender
        Room senderRoom = new Room(roomID);
        dbReference = FirebaseDatabase.getInstance().getReference("UserRooms").child(senderID);
        dbReference.child(receiverID).setValue(senderRoom);

        // list of receiver
        Room receiverRoom = new Room(roomID);
        dbReference = FirebaseDatabase.getInstance().getReference("UserRooms").child(receiverID);
        dbReference.child(senderID).setValue(receiverRoom);

        // create on room detail
        dbReference = FirebaseDatabase.getInstance().getReference("RoomDetail");
        RoomDetail detail = new RoomDetail();
        detail.setRoomID(roomID);
        detail.setLastMsgID("");
        ArrayList<String> memberIDs = new ArrayList<>();
        memberIDs.add(senderID);
        memberIDs.add(receiverID);
        detail.setMemberIDs(memberIDs);
        dbReference.child(roomID).setValue(detail);

        return roomID;
    }

    private void sendMessage(final String message, String senderID, String roomID, final String receiverID) {
        dbReference = FirebaseDatabase.getInstance().getReference("Messages").child(roomID);
        Message msg = new Message(message, senderID, false);
        dbReference.push().setValue(msg);

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                if (notify) {
//                    sendNotification(receiverID, user.getUsername(), message);
//                }
//                notify = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
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

//    private void sendNotification(final String receiver, final String username, final String message) {
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("UserTokens");
//        Query query = tokens.orderByKey().equalTo(receiver);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Token token = snapshot.getValue(Token.class);
//                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "New Message",
//                            receiver);
//
//                    Sender sender = new Sender(data, token.getToken());
//
//                    serviceAPI.sendNotification(sender)
//                            .enqueue(new Callback<MyResponse>() {
//                                @Override
//                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//                                    if (response.code() == 200) {
//                                        if (response.body().success != 1) {
//                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void currentUser(String userid) {
//        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//        editor.putString("currentuser", userid);
//        editor.apply();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
//        currentUser(fuser.getUid());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (seenListener != null)
            dbReference.removeEventListener(seenListener);
        status("offline");
//        currentUser("none");
    }
}
