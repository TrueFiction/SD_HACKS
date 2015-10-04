package com.hacks.sd_hacks_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;


public class HomeActivity extends Activity implements View.OnClickListener {


    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;
    private TextView barcodeHiddenValue;
    private TextView priceValue;
    private ImageView barcodeImg;
    private Button cartSendButton;
    private Button cartNotSendButton;
    private Button readBarcodeButton;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        statusMessage = (TextView)findViewById(R.id.status_message);
        barcodeValue = (TextView)findViewById(R.id.barcode_value);
        barcodeHiddenValue = (TextView)findViewById(R.id.barcode_hidden_value);
        priceValue = (TextView)findViewById(R.id.price_value);
        barcodeImg = (ImageView)findViewById(R.id.barcode_img);
        cartSendButton = (Button)findViewById(R.id.cartSend);
        cartNotSendButton = (Button)findViewById(R.id.cartNotSend);
        readBarcodeButton = (Button)findViewById(R.id.read_barcode);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_barcode).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
                    query.whereEqualTo("UPC", barcode.displayValue);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {
                                Log.d("score", "Retrieved " + scoreList.size() + " scores");
                                ParseObject scannedObject= scoreList.get(0);
                                barcodeValue.setText(scannedObject.getString("Name"));
                                barcodeHiddenValue.setText(scannedObject.getObjectId());
                                DecimalFormat df = new DecimalFormat("#.00");
                                priceValue.setText(df.format(scannedObject.getNumber("Price")));
                                ParseFile fileObject = (ParseFile)scannedObject.get("Image");
                                fileObject.getDataInBackground(new GetDataCallback() {
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            Log.d("test", "We've got data in data.");
                                            Bitmap bmp = BitmapFactory
                                                    .decodeByteArray(data, 0, data.length);

                                            barcodeImg.setImageBitmap(bmp);

                                        } else {
                                            Log.d("test", "There was a problem downloading the data.");
                                        }
                                    }
                                });
                                cartSendButton.setVisibility(View.VISIBLE);
                                cartNotSendButton.setVisibility(View.VISIBLE);
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });

                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void sendCart(View v) {
        //button says send item to cart
        if (cartSendButton.getText() == getString(R.string.cart_send)){
            barcodeValue.setText(barcodeValue.getText() + " added to cart.");
            cartSendButton.setText("View Cart");
            cartNotSendButton.setText("Scan Items");
            readBarcodeButton.setVisibility(View.INVISIBLE);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
            query.getInBackground(barcodeHiddenValue.getText().toString(), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        Log.d("success", object.toString());
                        ParseUser user = ParseUser.getCurrentUser();
                        ParseObject cartItem = new ParseObject("Cart");
                        cartItem.put("User", ParseUser.getCurrentUser());
                        cartItem.put("Name", object.getString("Name"));
                        cartItem.put("Image", object.getParseFile("Image"));
                        cartItem.put("Price", object.getNumber("Price"));
                        cartItem.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("success", "cart saved");
                                } else {
                                    Log.d("success", "cart not saved" + e.toString());
                                }
                            }
                        });
                    } else {
                        // something went wrong
                    }
                }
            });
        }
        //button says view the cart
        else {
            // send the user to the cart page
            Intent ShoppingCartIntent = new Intent(this, ShoppingCartActivity.class);
            startActivity(ShoppingCartIntent);
        }
    }

    public void notSendCart(View v) {
        barcodeValue.setText("");
        priceValue.setText("");
        barcodeImg.setImageDrawable(null);
        statusMessage.setText(getString(R.string.barcode_header));
        cartSendButton.setText(getString(R.string.cart_send));
        cartSendButton.setVisibility(View.INVISIBLE);
        cartNotSendButton.setText(getString(R.string.cart_not_send));
        cartNotSendButton.setVisibility(View.INVISIBLE);
        readBarcodeButton.setVisibility(View.VISIBLE);
    }

    public void viewCart(View v) {
        Intent intent = new Intent(this, ShoppingCartActivity.class);
        startActivity(intent);
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
