package com.anonymouscreations.sathurangam.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.database.MyDatabase;
import com.anonymouscreations.sathurangam.database.UserData;

public class SignUpActivity extends AppCompatActivity {

    TextView btnSignUp;
    EditText etName, etEmail, etPassword;
    ImageView btnBack;
    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // --- Code to hide support action bar
        getSupportActionBar().hide();

        btnBack = findViewById(R.id.btnBack);
        btnSignUp = findViewById(R.id.btnSignUp);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        userData = new UserData();

        // --- Sample
        etName.setText("raaj2");
        etEmail.setText("raaj2@gmail.com");
        etPassword.setText("raaj2@123");

        // === Button to signup
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // --- If validate returns true database object is created and method for creating new account is called
                if(validate())
                    new MyDatabase(getApplicationContext(),"user",SignUpActivity.this).checkAccountExistence(userData);
                else
                    Toast.makeText(SignUpActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            }
        });

        // === Button to navigate back to the login page
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    // === Function to validate input fields
    boolean validate(){

        // --- Adding data to the object
        userData.setEmail(etEmail.getText().toString().trim());
        userData.setName(etName.getText().toString().trim());
        userData.setPassword(etPassword.getText().toString().trim());

        // --- Validating for empty string
        if(userData.getName().length() > 0 && userData.getEmail().length() > 0 && userData.getPassword().length() > 0)
            return true;

        return false;
    }
}