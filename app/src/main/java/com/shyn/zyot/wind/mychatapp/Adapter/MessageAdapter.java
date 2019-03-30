package com.shyn.zyot.wind.mychatapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shyn.zyot.wind.mychatapp.Model.Message;
import com.shyn.zyot.wind.mychatapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Message> mMessages;
    private String senderImageUrl;

    private FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Message> mMessages, String senderImageUrl) {
        this.mContext = mContext;
        this.mMessages = mMessages;
        this.senderImageUrl = senderImageUrl;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_left_msg, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_right_msg, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.tvMessage.setText(message.getMessage());
        if (senderImageUrl.equals("default"))
            holder.userImage.setImageResource(R.mipmap.ic_launcher_round);
        else
            Glide.with(mContext).load(senderImageUrl).into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView tvMessage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }

    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mMessages.get(position).getSenderID().equals(fuser.getUid()))
            return MSG_TYPE_RIGHT;
        else return MSG_TYPE_LEFT;
    }
}
