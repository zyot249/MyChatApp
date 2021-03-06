package com.shyn.zyot.wind.mychatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shyn.zyot.wind.mychatapp.Model.User;


public class RegisterActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // setting for toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // find view
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // init firebaseAuth
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                if (name.isEmpty())
                    Snackbar.make(btnRegister, "Name must be filled", Snackbar.LENGTH_SHORT).show();
                else if (email.isEmpty())
                    Snackbar.make(btnRegister, "Email must be filled", Snackbar.LENGTH_SHORT).show();
                else if (password.length() < 6)
                    Snackbar.make(btnRegister, "Password must be longer than 6 characters", Snackbar.LENGTH_SHORT).show();
                else {
                    register(name, email, password);
                }
            }
        });
    }

    private void register(final String name, String email, String password) {
        final String userEmail = email;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userID = firebaseUser.getUid();

                            Snackbar.make(btnRegister, "Register Successfully", Snackbar.LENGTH_SHORT).show();
                            // Write database
                            dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                            User user = new User(userID, name, "default", "offline", name.toLowerCase());

                            dbReference.setValue(user).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // open Login Activity
                                        mAuth.signOut();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.setAction("LOGIN_AFTER_REGISTER");
                                        intent.putExtra("user_email", userEmail);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Snackbar.make(btnRegister, "You can't register with this email and password!", Snackbar.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        firebaseUser.delete();
                                    }
                                }
                            });
                        } else
                            Snackbar.make(btnRegister, "Register Failed", Snackbar.LENGTH_SHORT).show();
                    }
                });


    }
}
