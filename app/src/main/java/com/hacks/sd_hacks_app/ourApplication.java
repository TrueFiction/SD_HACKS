package com.hacks.sd_hacks_app;

import android.app.Application;

import com.parse.Parse;

/**
 * Class Name: ourApplication
 * Description: sets up Parse for the entire application
 */
public class ourApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Initialization code to connect to parse, "Self Checkout": applicationID, client key
        Parse.initialize(this, "9XzfZnNjmZO8fxlYnkeuNkz7Md6TSdTtjNUVwbdM", "9TYeFAEhQ9YHKsMxF6DjXfis6A3ootSlvCLVzNro");
    }
}
