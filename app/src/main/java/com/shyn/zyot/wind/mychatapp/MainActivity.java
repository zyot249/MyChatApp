package com.shyn.zyot.wind.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shyn.zyot.wind.mychatapp.Adapter.PagerAdapter;
import com.shyn.zyot.wind.mychatapp.Fragment.ChatFragment;
import com.shyn.zyot.wind.mychatapp.Fragment.UserFragment;
import com.shyn.zyot.wind.mychatapp.Model.Message;
import com.shyn.zyot.wind.mychatapp.Model.Room;
import com.shyn.zyot.wind.mychatapp.Model.User;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;
    private FirebaseUser firebaseUser;

    private TextView tvUsername;
    private CircleImageView userImage;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    private int unread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting for toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");

        // find view
        tvUsername = findViewById(R.id.tvUsername);
        userImage = findViewById(R.id.userImage);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // init firebaseAuth & database
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tvUsername.setText(user.getUsername());
                if (user.getImageUrl().equals("default"))
                    userImage.setImageResource(R.mipmap.ic_launcher_round);
                else
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // open profile activity
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // unread messages
        dbReference = FirebaseDatabase.getInstance().getReference("Messages");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt = countUnreadMessage();
                pagerAdapter = new PagerAdapter(getSupportFragmentManager());
                if (cnt == 0)
                    pagerAdapter.addFragment(new ChatFragment(), "Chats");
                else
                    pagerAdapter.addFragment(new ChatFragment(), "(" + cnt + ")" + "Chats");
                pagerAdapter.addFragment(new UserFragment(),"Users");
                viewPager.setAdapter(pagerAdapter);
                tabLayout.setupWithViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private int countUnreadMessage() {
        unread = 0;
        DatabaseReference userRooms = FirebaseDatabase.getInstance().getReference("UserRooms").child(firebaseUser.getUid());
        userRooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Room room = snapshot.getValue(Room.class);
                    DatabaseReference messages = FirebaseDatabase.getInstance().getReference("Messages").child(room.getRoomID());
                    messages.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                Message message = snapshot1.getValue(Message.class);
                                if (!message.getSentBy().equals(firebaseUser.getUid()) && !message.isSeen())
                                    unread++;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return unread;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnLogout: {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void status(String status) {
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        dbReference.updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }
}
