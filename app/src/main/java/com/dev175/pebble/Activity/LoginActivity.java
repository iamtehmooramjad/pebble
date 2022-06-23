package com.dev175.pebble.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dev175.pebble.R;
import com.dev175.pebble.SessionManagement;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    TextInputEditText pinCode1,pinCode2,pinCode3,pinCode4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        pinCode1 = (TextInputEditText)findViewById(R.id.pinCode1);
        pinCode2 = (TextInputEditText)findViewById(R.id.pinCode2);
        pinCode3 = (TextInputEditText)findViewById(R.id.pinCode3);
        pinCode4 = (TextInputEditText)findViewById(R.id.pinCode4);
    }

    public void login(View view) {
        String pin = pinCode1.getText().toString()+pinCode2.getText().toString()+pinCode3.getText().toString()+pinCode4.getText().toString();

        int numPin = Integer.parseInt(pin);
        int myPin = 1750;

        if (myPin == numPin)
        {
            Toast.makeText(this, "Login Successfull", Toast.LENGTH_SHORT).show();
            SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
            sessionManagement.saveSession(myPin);
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Invalid PIN entered!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is logged in
        //if user is logged in then move to HomeActivity
        SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
        int userPin = sessionManagement.getSession();
        if (userPin!=-1)
        {
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}