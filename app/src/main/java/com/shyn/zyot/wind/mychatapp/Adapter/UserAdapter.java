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
import com.shyn.zyot.wind.mychatapp.MessageActivity;
import com.shyn.zyot.wind.mychatapp.Model.User;
import com.shyn.zyot.wind.mychatapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context mContext;
    private List<User> users;

    public UserAdapter(Context mContext, List<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View item = inflater.inflate(R.layout.list_item_user, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(item);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User user = users.get(position);
        holder.tvUsername.setText(user.getUsername());
        if (user.getImageUrl().equals("default"))
            holder.userImage.setImageResource(R.mipmap.ic_launcher_round);
        else Glide.with(mContext).load(user.getImageUrl()).into(holder.userImage);

        if (user.getStatus().equals("online")) {
            holder.statusOn.setVisibility(View.VISIBLE);
            holder.statusOff.setVisibility(View.GONE);
        } else {
            holder.statusOn.setVisibility(View.GONE);
            holder.statusOff.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("receiverID", user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private CircleImageView userImage;
        private CircleImageView statusOn;
        private CircleImageView statusOff;

        public UserViewHolder(View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            userImage = itemView.findViewById(R.id.userImage);
            statusOn = itemView.findViewById(R.id.statusOn);
            statusOff = itemView.findViewById(R.id.statusOff);
        }
    }
}
