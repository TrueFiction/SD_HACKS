package com.hacks.sd_hacks_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.*;
import com.google.android.gms.common.api.*;
import com.google.android.gms.wallet.*;
import com.google.android.gms.wallet.fragment.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookiePolicy;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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


public class ShoppingCartActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    // Change to your live publishable key before shipping
    public static final String PUBLISHABLE_KEY = "pk_test_9KjAM1DWhTbiw8r4uBQE15hU";

    // Unique identifiers for asynchronous requests:
    private static final int LOAD_MASKED_WALLET_REQUEST_CODE = 1000;
    private static final int LOAD_FULL_WALLET_REQUEST_CODE = 1001;

    private SupportWalletFragment walletFragment;

    private GoogleApiClient googleApiClient;

    /** Called when the activity is first created. */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        walletFragment =
                (SupportWalletFragment) getSupportFragmentManager().findFragmentById(R.id.wallet_fragment);

        MaskedWalletRequest maskedWalletRequest = MaskedWalletRequest.newBuilder()

                // Request credit card tokenization with Stripe by specifying tokenization parameters:
                .setPaymentMethodTokenizationParameters(PaymentMethodTokenizationParameters.newBuilder()
                        .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.PAYMENT_GATEWAY)
                        .addParameter("gateway", "stripe")
                        .addParameter("stripe:publishableKey", PUBLISHABLE_KEY)
                        .addParameter("stripe:version", com.stripe.Stripe.VERSION)
                        .build())

                        // You want the shipping address:
                .setShippingAddressRequired(false)

                        // Price set as a decimal:
                .setEstimatedTotalPrice("20.00") // TODO
                .setCurrencyCode("USD")
                .build();

        // Set the parameters:
        WalletFragmentInitParams initParams = WalletFragmentInitParams.newBuilder()
                .setMaskedWalletRequest(maskedWalletRequest)
                .setMaskedWalletRequestCode(LOAD_MASKED_WALLET_REQUEST_CODE)
                .build();

        // Initialize the fragment:
        walletFragment.initialize(initParams);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_SANDBOX)
                        .setTheme(WalletConstants.THEME_HOLO_LIGHT)
                        .build())
                .build();
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

    public void clearAll(View v) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Cart");
        query.whereEqualTo("User", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject p : list)
                        p.deleteInBackground();
                } else {
                    Log.e("error clearing cart", "error: " + e);
                }
            }
        });
        // send the user to the sign up page
        Intent homeIntent = new Intent(ShoppingCartActivity.this, HomeActivity.class);
        startActivity(homeIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("GOT HERE!!!!!!!!!!!!!");

        int errorCode = -1;
        if (data != null) {
            errorCode = data.getIntExtra(
                    WalletConstants.EXTRA_ERROR_CODE, -1);
        }
        handleError(errorCode);

        if (requestCode == LOAD_MASKED_WALLET_REQUEST_CODE) { // Unique, identifying constant
            if (resultCode == Activity.RESULT_OK) {
                System.out.println("Maskeddddddddddddddddddddd....");
                MaskedWallet maskedWallet = data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                FullWalletRequest fullWalletRequest = FullWalletRequest.newBuilder()
                        .setCart(Cart.newBuilder()
                                .setCurrencyCode("USD")
                                .setTotalPrice("20.00")
                                .addLineItem(LineItem.newBuilder() // Identify item being purchased
                                        .setCurrencyCode("USD")
                                        .setQuantity("1")
                                        .setDescription("Self Checkout")
                                        .setTotalPrice("2000") // TODO
                                        .setUnitPrice("2000") // TODO
                                        .build())
                                .build())
                        .setGoogleTransactionId(maskedWallet.getGoogleTransactionId())
                        .build();
                Wallet.Payments.loadFullWallet(googleApiClient, fullWalletRequest, LOAD_FULL_WALLET_REQUEST_CODE);
            }
        } else if (requestCode == LOAD_FULL_WALLET_REQUEST_CODE) { // Unique, identifying constant
            if (resultCode == Activity.RESULT_OK) {
                System.out.println("Well, result is ok!!!!!!!!!!!!!!!!!!!!!!!!Q");
                /*if (data == null)
                    return;*/
                FullWallet fullWallet = data.getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);
                String tokenJSON = fullWallet.getPaymentMethodToken().getToken();
                try {
                    tokenJSON = new JSONObject(tokenJSON).put("amount", "2000").toString(); // TODO
                } catch (JSONException j) {
                    j.printStackTrace();
                }
                System.out.println("successfully appended amount");

                com.stripe.model.Token token = com.stripe.model.Token.GSON.fromJson(tokenJSON, com.stripe.model.Token.class);

                try {
                    sendPost(tokenJSON);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            System.out.println("not quite :(((((((((((");
        }
    }

    void handleError(int errorCode) {
        switch (errorCode) {
            case WalletConstants.ERROR_CODE_SPENDING_LIMIT_EXCEEDED:
                Toast.makeText(getApplicationContext(),
                        getString(R.string.spending_limit_exceeded, errorCode),
                        Toast.LENGTH_LONG).show();
                break;
            case WalletConstants.ERROR_CODE_INVALID_PARAMETERS:
            case WalletConstants.ERROR_CODE_AUTHENTICATION_FAILURE:
            case WalletConstants.ERROR_CODE_BUYER_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_MERCHANT_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE:
            case WalletConstants.ERROR_CODE_UNSUPPORTED_API_VERSION:
            case WalletConstants.ERROR_CODE_UNKNOWN:
            default:
                // unrecoverable error
                //mGoogleWalletDisabled = true;
                System.out.println("Error code: " + errorCode);
                break;
        }
    }


    private void sendPost(final String tokenJSON) throws Exception {
        System.out.println("u have entered send post!!!!!!!!!!!!!!!!!!!");
        //Your server URL
        new Thread(new Runnable() {
            public void run() {
                try {
                    String url = "https://107.170.199.169/";
                    HttpsURLConnection con = setUpHttpsConnection(url);
                    if (con == null) {
                        System.out.println("HTTPS connection did a failure!!!11");
                        return;
                    }

                    //add reuqest header
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    System.out.println("i'm gonna write the bytes now........");
                    // Send post request
                    con.setDoOutput(true);// Should be part of code only for .Net web-services else no need for PHP
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(tokenJSON);
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
                    System.out.println("\nSending 'POST' request to URL : " + url);
                    System.out.println("Post parameters : " + tokenJSON);
                    System.out.println("Response Code : " + responseCode);

                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    // source: http://littlesvr.ca/grumble/2014/07/21/android-programming-connect-to-an-https-server-with-self-signed-certificate/
    /**
     * Set up a connection to littlesvr.ca using HTTPS. An entire function
     * is needed to do this because littlesvr.ca has a self-signed certificate.
     *
     * The caller of the function would do something like:
     * HttpsURLConnection urlConnection = setUpHttpsConnection("https://littlesvr.ca");
     * InputStream in = urlConnection.getInputStream();
     * And read from that "in" as usual in Java
     *
     * Based on code from:
     * https://developer.android.com/training/articles/security-ssl.html#SelfSigned
     */
    @SuppressLint("SdCardPath")
    public HttpsURLConnection setUpHttpsConnection(String urlString)
    {
        System.out.println("Setting up HTTPS connection..............");
        try
        {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            // My CRT file that I put in the assets folder
            // I got this file by following these steps:
            // * Go to https://littlesvr.ca using Firefox
            // * Click the padlock/More/Security/View Certificate/Details/Export
            // * Saved the file as littlesvr.crt (type X.509 Certificate (PEM))
            // The MainActivity.context is declared as:
            // public static Context context;
            // And initialized in MainActivity.onCreate() as:
            // MainActivity.context = getApplicationContext();
            InputStream caInput = new BufferedInputStream(this.getApplicationContext().getAssets().open("SelfCheckout.crt"));
            Certificate ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }

            });
            urlConnection.setSSLSocketFactory(context.getSocketFactory());

            return urlConnection;
        }
        catch (Exception ex)
        {
            System.out.println("Failed to establish SSL connection to server: " + ex.toString());
            return null;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onConnected(Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}


    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }
}
