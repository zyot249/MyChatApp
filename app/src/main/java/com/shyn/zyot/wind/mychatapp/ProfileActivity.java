package com.shyn.zyot.wind.mychatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shyn.zyot.wind.mychatapp.Model.User;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_OPEN_IMAGE = 1001;

    private Button btnDone;
    private Button btnLogout;
    private Button btnChangeName;
    private CircleImageView userImage;
    private TextView tvUsername;

    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbReference;
    private StorageReference stReference;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnDone = findViewById(R.id.btnDone);
        userImage = findViewById(R.id.userImage);
        tvUsername = findViewById(R.id.tvUsername);
        btnLogout = findViewById(R.id.btnLogout);
        btnChangeName = findViewById(R.id.btnChangeName);

        stReference = FirebaseStorage.getInstance().getReference("ProfileImages");
        // get user ID from main
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        // get info
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageUrl().equals("default"))
                    userImage.setImageResource(R.mipmap.ic_launcher_round);
                else Glide.with(getApplicationContext()).load(user.getImageUrl()).into(userImage);

                tvUsername.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // change profie image
        userImage.setOnClickListener(this);
        btnChangeName.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    private void openImageFolder() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_OPEN_IMAGE);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = ProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference storageReference = stReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = storageReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // get link uri
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        // change imageUrl in Users
                        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageUrl", "" + mUri);
                        dbReference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }


    }

    private void showChangeNameDialog(final String currentName) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.change_name_dialog, null);
        final EditText edtChangeName = dialogView.findViewById(R.id.edtChangeName);
        edtChangeName.setHint(currentName);
        AlertDialog dialog = new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Change Name")
                .setMessage("Enter your new name")
                .setView(dialogView)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = edtChangeName.getText().toString();
                        if (!newName.isEmpty()) {
                            if (!newName.equals(currentName)) {
                                dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("username", newName);
                                hashMap.put("search", newName.toLowerCase());
                                dbReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            Snackbar.make(btnChangeName, "You've changed your name successfully!", Snackbar.LENGTH_SHORT).show();
                                        else
                                            Snackbar.make(btnChangeName, "Failed Action", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Snackbar.make(btnChangeName, "Your name doesn't change!", Snackbar.LENGTH_SHORT).show();
                            }
                        }else {
                            Snackbar.make(btnChangeName, "Your new name can not be empty!", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .create();

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_OPEN_IMAGE) {
                // get uri
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
                }

                // upload
                if (uploadTask != null && uploadTask.isInProgress())
                    Toast.makeText(ProfileActivity.this, "Upload in progress!", Toast.LENGTH_SHORT).show();
                else uploadImage();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDone: {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.userImage: {
                openImageFolder();
                break;
            }
            case R.id.tvUsername: {
                break;
            }
            case R.id.btnLogout: {
                mAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.btnChangeName: {
                showChangeNameDialog(tvUsername.getText().toString());
                break;
            }
        }
    }
}
