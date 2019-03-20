package com.shyn.zyot.wind.mychatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private TextView tvForgotPass;
    private Button btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // setting for toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // find view
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        btnLogin = findViewById(R.id.btnLogin);

        // init firebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // get data if after register
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals("LOGIN_AFTER_REGISTER")) {
            String email = intent.getStringExtra("user_email");
            if (!email.isEmpty())
                edtEmail.setText(email);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get input
                String userEmail = edtEmail.getText().toString();
                String userPass = edtPassword.getText().toString();

                // check input
                if (userEmail.isEmpty())
                    Toast.makeText(LoginActivity.this, "Email must be filled", Toast.LENGTH_SHORT).show();
                else if (userPass.isEmpty())
                    Toast.makeText(LoginActivity.this, "Password must be filled", Toast.LENGTH_SHORT).show();
                else {
                    mAuth.signInWithEmailAndPassword(userEmail, userPass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else
                                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}
