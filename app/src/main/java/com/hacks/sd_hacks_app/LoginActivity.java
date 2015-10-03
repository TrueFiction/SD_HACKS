package com.hacks.sd_hacks_app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
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


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        createUser();
    }

    /* creates parse user */
    protected void createUser () {
        ParseUser user = new ParseUser();
        user.setUsername("my name");
        user.setPassword("my pass");
        user.setEmail("email@example.com");

        // other fields can be set just like with ParseObject
        //user.put("phone", "650-253-0000");

        user.signUpInBackground();
    }

    public void sendToSignUp (View v) {
        // send the user to the sign up page
        Intent signUpIntent = new Intent(this, SignupActivity.class);
        startActivity(signUpIntent);
    }

    public void loginUser (View v) {
        //get the username that the user has entered
        EditText usernameField = (EditText) findViewById(R.id.username_enter);
        String usernameContents = usernameField.getText().toString();

        //get the password that the user has entered
        EditText passwordField = (EditText) findViewById(R.id.password_enter);
        String passwordContents = passwordField.getText().toString();

        ParseUser.logInInBackground(usernameContents, passwordContents,
                new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, com.parse.ParseException e) {
                        if (parseUser != null) {
                            //Send user to the main application screen, HomeActivity
                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(homeIntent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.login_error),
                                    Toast.LENGTH_SHORT).show();
                            // Login failed. Look at ParseException for error.
                        }
                    }
                });
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
