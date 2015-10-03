package com.hacks.sd_hacks_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.text.ParseException;


public class CheckoutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checkout);


    }

    public void sendToSignUp (View v) {
        // send the user to the sign up page
        /*Intent signUpIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpIntent);*/
    }



    /** Exit the application and leave the application running as a process*/
    /*@Override
    public void onBackPressed() {
        //exit the application
        IntentFactory.exitApplication(this);
        //close this activity
        finish();
    }*/
}
