package com.hacks.sd_hacks_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Class Name:
 * Description:
 */
public class itemsAdapter extends ArrayAdapter<ParseObject> {

    public itemsAdapter(Context context, int textViewResourceId, List<ParseObject> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("in adapter getView", "Inside");
        // Get the data item for this position
        ParseObject item = (ParseObject)getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            Log.e("in adapter getView", "convertView is null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, parent, false);

            TextView itemName = (TextView) convertView.findViewById(R.id.item_name);
            TextView itemPrice = (TextView) convertView.findViewById(R.id.item_price);
            ImageView itemImage = (ImageView) convertView.findViewById(R.id.item_img);

            // Populate the data into the template view using the data object
            itemName.setText(item.getString("Name"));
            DecimalFormat df = new DecimalFormat("#.00");
            itemPrice.setText(df.format(item.getNumber("Price")));
        }

        // Lookup view for data population
        TextView itemName = (TextView) convertView.findViewById(R.id.item_name);
        TextView itemPrice = (TextView) convertView.findViewById(R.id.item_price);
        final ImageView itemImage = (ImageView) convertView.findViewById(R.id.item_img);

        // Populate the data into the template view using the data object
        itemName.setText(item.getString("Name"));
        DecimalFormat df = new DecimalFormat("#.00");
        itemPrice.setText(df.format(item.getNumber("Price")));
        ParseFile fileObject = (ParseFile)item.get("Image");
        fileObject.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    Log.d("test", "We've got data in data.");
                    Bitmap bmp = BitmapFactory
                            .decodeByteArray(data, 0, data.length);

                    itemImage.setImageBitmap(bmp);

                } else {
                    Log.d("test", "There was a problem downloading the data.");
                }
            }
        });

        Log.e("in adapter getView", "changed values");

        // Return the completed view to render on screen
        return convertView;
    }
}
