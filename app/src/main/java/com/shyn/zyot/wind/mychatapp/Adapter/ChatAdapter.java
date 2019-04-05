package com.shyn.zyot.wind.mychatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shyn.zyot.wind.mychatapp.MessageActivity;
import com.shyn.zyot.wind.mychatapp.Model.Message;
import com.shyn.zyot.wind.mychatapp.Model.Room;
import com.shyn.zyot.wind.mychatapp.Model.RoomDetail;
import com.shyn.zyot.wind.mychatapp.Model.User;
import com.shyn.zyot.wind.mychatapp.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context mContext;
    private List<Room> roomChats;

    public ChatAdapter(Context mContext, List<Room> roomChats) {
        this.mContext = mContext;
        this.roomChats = roomChats;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View item = inflater.inflate(R.layout.list_item_chat, parent, false);
        ChatViewHolder chatViewHolder = new ChatViewHolder(item);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Room room = roomChats.get(position);
        final String roomID = room.getRoomID();
        final DatabaseReference roomDetail = FirebaseDatabase.getInstance().getReference("RoomDetail").child(roomID);
        roomDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RoomDetail detail = dataSnapshot.getValue(RoomDetail.class);
                if (detail != null) {
                    // set image
                    if (detail.getMemberIDs().size() == 2) {
                        String receiverID = "";
                        for (String id : detail.getMemberIDs()) {
                            if (!id.equals(fuser.getUid()))
                                receiverID = id;
                        }
                        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users").child(receiverID);
                        users.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User receiver = dataSnapshot.getValue(User.class);
                                // set image
                                assert receiver != null;
                                if (receiver.getImageUrl().equals("default")) {
                                    holder.userImage.setImageResource(R.mipmap.ic_launcher_round);
                                    holder.isSeen.setImageResource(R.mipmap.ic_launcher_round);
                                } else {
                                    Glide.with(mContext).load(receiver.getImageUrl()).into(holder.userImage);
                                    Glide.with(mContext).load(receiver.getImageUrl()).into(holder.isSeen);
                                }

                                // set status
                                if (receiver.getStatus().equals("online")) {
                                    holder.statusOn.setVisibility(View.VISIBLE);
                                    holder.statusOff.setVisibility(View.GONE);
                                } else {
                                    holder.statusOn.setVisibility(View.GONE);
                                    holder.statusOff.setVisibility(View.VISIBLE);
                                }

                                // set user name
                                holder.tvUsername.setText(receiver.getUsername());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    // set last msg
                    if (detail.getLastMsgID().equals("")) {
                        holder.tvLastMSG.setVisibility(View.VISIBLE);
                        holder.tvLastMSG.setText("Please Chat Together...");
                    } else {
                        holder.tvLastMSG.setVisibility(View.VISIBLE);
                        DatabaseReference messages = FirebaseDatabase.getInstance().getReference("Messages").child(detail.getRoomID()).child(detail.getLastMsgID());
                        messages.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Message lastMessage = dataSnapshot.getValue(Message.class);
                                if (lastMessage != null) {
                                    holder.tvLastMSG.setText(lastMessage.getContent());
                                    if (lastMessage.getSentBy().equals(fuser.getUid())) {
                                        if (lastMessage.isSeen())
                                            holder.isSeen.setVisibility(View.VISIBLE);
                                        else holder.isSeen.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        roomDetail.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                RoomDetail detail = dataSnapshot.getValue(RoomDetail.class);
                                if (detail != null) {
                                    // get receiver id
                                    if (detail.getMemberIDs().size() == 2) {
                                        String receiverID = "";
                                        for (String id : detail.getMemberIDs()) {
                                            if (!id.equals(fuser.getUid()))
                                                receiverID = id;
                                        }

                                        Intent intent = new Intent(mContext, MessageActivity.class);
                                        intent.putExtra("roomID", roomID);
                                        intent.putExtra("receiverID", receiverID);
                                        mContext.startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return roomChats.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView tvUsername;
        private TextView tvLastMSG;
        private CircleImageView statusOn;
        private CircleImageView statusOff;
        private CircleImageView isSeen;

        public ChatViewHolder(View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLastMSG = itemView.findViewById(R.id.tvLastMSG);
            statusOn = itemView.findViewById(R.id.statusOn);
            statusOff = itemView.findViewById(R.id.statusOff);
            isSeen = itemView.findViewById(R.id.isSeen);
        }
    }
}
