package com.shyn.zyot.wind.mychatapp.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shyn.zyot.wind.mychatapp.Adapter.ChatAdapter;
import com.shyn.zyot.wind.mychatapp.Model.Room;
import com.shyn.zyot.wind.mychatapp.Notification.Token;
import com.shyn.zyot.wind.mychatapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Room> roomChats;

    public ChatFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        roomChats = new ArrayList<>();
        readChats();
//        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void readChats() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRooms = FirebaseDatabase.getInstance().getReference("UserRooms").child(fuser.getUid());
        userRooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomChats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Room room = snapshot.getValue(Room.class);
                    if (room != null)
                        roomChats.add(room);
                }

                chatAdapter = new ChatAdapter(getContext(), roomChats);
                recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserTokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }
}
