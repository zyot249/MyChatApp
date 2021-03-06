package com.shyn.zyot.wind.mychatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
        getSupportActionBar().setTitle(R.string.login);
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
        if (action != null){
            if (action.equals("LOGIN_AFTER_REGISTER") || action.equals("LOGIN_AFTER_RESET_PASSWORD")) {
                String email = intent.getStringExtra("user_email");
                if (!email.isEmpty())
                    edtEmail.setText(email);
            }
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get input
                String userEmail = edtEmail.getText().toString();
                String userPass = edtPassword.getText().toString();

                // check input
                if (userEmail.isEmpty())
                    Snackbar.make(btnLogin, "Email must be filled", Snackbar.LENGTH_SHORT).show();
                else if (userPass.isEmpty())
                    Snackbar.make(btnLogin, "Password must be filled", Snackbar.LENGTH_SHORT).show();
                else {
                    mAuth.signInWithEmailAndPassword(userEmail, userPass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(btnLogin, "Welcome", Snackbar.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else
                                        Snackbar.make(btnLogin, "Authentication failed", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // reset password if forgot
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });


    }
}
