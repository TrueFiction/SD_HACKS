package com.hacks.sd_hacks_app;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class ShoppingCartActivity extends Activity
{
    /** Called when the activity is first created. */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
    }

    @Override
    public void onResume() {
        super.onResume();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Cart");
        query.whereEqualTo("User", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Log.e("success", "Got cart list" + list.size());

                    // Create the adapter to convert the array to views
                    itemsAdapter adapter = new itemsAdapter(ShoppingCartActivity.this, android.R.layout.simple_list_item_1, list);
                    // Attach the adapter to a ListView
                    ListView listView = (ListView) findViewById(R.id.cart_list);
                    listView.setAdapter(adapter);
                    Log.e("success", "adapter applied");
                } else {
                    Log.e("error getting cart list", "error: " + e);
                }
            }
        });
    }
}
