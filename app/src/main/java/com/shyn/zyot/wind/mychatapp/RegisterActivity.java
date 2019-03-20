package com.shyn.zyot.wind.mychatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // setting for toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
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
                final String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                if (name.isEmpty())
                    Toast.makeText(RegisterActivity.this, "Name must be filled", Toast.LENGTH_SHORT).show();
                else if (email.isEmpty())
                    Toast.makeText(RegisterActivity.this, "Email must be filled", Toast.LENGTH_SHORT).show();
                else if (password.length() <= 6)
                    Toast.makeText(RegisterActivity.this, "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
                else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mAuth.signOut();
                                        Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.setAction("LOGIN_AFTER_REGISTER");
                                        intent.putExtra("user_email", email);
                                        startActivity(intent);
                                        finish();
                                    } else
                                        Toast.makeText(RegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
    }
}
