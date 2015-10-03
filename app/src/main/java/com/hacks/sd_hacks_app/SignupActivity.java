package com.hacks.sd_hacks_app;

import android.app.Activity;
import android.os.Bundle;


public class SignupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
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
