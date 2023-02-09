package com.anonymouscreations.sathurangam.activities;

import androidx.appcompat.app.AppCompatActivity;


import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.database.LoginUserData;
import com.anonymouscreations.sathurangam.database.MyDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    TextView btnLogin, btnSignUp;
    EditText etEmail, etPassword;
    LoginUserData loginUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- Code to hide support action bar
        getSupportActionBar().hide();

        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        loginUserData = new LoginUserData();

        // --- Temporary data for logging in
        etEmail.setText("raaj@gmail.com");
        etPassword.setText("raaj@123");

        // --- Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // --- Calling validate method to validate the input fields
                if(validate())
                    new MyDatabase(getApplicationContext(),"user",LoginActivity.this).loginUser(loginUserData);
                else
                    Toast.makeText(LoginActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Signup button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }

    // === Validating the input fields
    boolean validate(){

        // --- Extracting data from the elements
        loginUserData.setEmail(etEmail.getText().toString().trim());
        loginUserData.setPassword(etPassword.getText().toString().trim());

        // --- Validation for empty string in the input fields
        if(loginUserData.getEmail().length() > 0 && loginUserData.getEmail().length() > 0)
            return true;

        return false;
    }
}