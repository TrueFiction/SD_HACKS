package com.hacks.sd_hacks_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
    }

    /** This method creates a user in Parse, also does error checking
     * through Parse */
    public void createUser(View v) {
        try {
            //get the username that the user has entered
            EditText usernameField =
                    (EditText) findViewById(R.id.username_enter_signup);
            if(usernameField.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.signup_empty_username),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String usernameContents = usernameField.getText().toString();

            //get the email that the user may have entered
            EditText EmailField =
                    (EditText) findViewById(R.id.email_enter_signup);
            if(EmailField.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.signup_empty_email),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String emailContents = EmailField.getText().toString();

            //get the password that the user has entered
            EditText passwordField =
                    (EditText) findViewById(R.id.password_enter_signup);
            if(passwordField.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.signup_empty_password),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String passwordContents = passwordField.getText().toString();

            //start user creation
            final ParseUser user = new ParseUser();
            user.setUsername(usernameContents);
            user.setPassword(passwordContents);
            user.setEmail(emailContents);

            //done asynchronously to avoid slowing user interface
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    Intent LoginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(LoginIntent);
                }
            });

        } catch (Exception e) {
            //we need to handle exception here
            Log.e("Error occurred", e.toString());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //if RESULT_OK is returned then activity will be popped off Back Stack
        if (resultCode == RESULT_OK) {
            //pop this activity off of the Back Stack
            finish();
        }
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
