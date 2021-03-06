package com.mcdenny.coronavirusapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.UUID;

public class Config {
    private static final String TAG = "Config";
    public Config() {
    }

    public static final String BASE_URL = "https://corona.lmao.ninja/v2/";
    public static final String NIGERIAN_STATES_URL = "http://locationsng-api.herokuapp.com/api/v1/";
    public static final String PAYPAL_CLIENT_ID = "ASdds-5cjAlci1b_Z0CIDCGExbPTFwYLDQ1X5riR_rCG0TuH-oMoRAJ59gPqM5h02ziEFfeMUlM5yM0G";
    public static final int PAYPAL_REQUEST_CODE = 7777;

    public static final String UGANDA = "uganda";
    public static final String NIGERIA = "nigeria";

    public static final String UPDATED = "updated";

    public static final int HOSPITAL_RESULT_OK = 201;

    //Covid 19 probability
    public static final int COUGH = 90;
    public static final int COLD = 60;
    public static final int DIARRHEA = 60;
    public static final int SORE_THROAT = 60;
    public static final int BODY_ACHES = 50;
    public static final int HEADACHE = 60;
    public static final int FEVER = 98;
    public static final int BREATHING_DIFFICULTY = 99;
    public static final int FATIGUE = 50;
    public static final int TRAVEL = 80;
    public static final int TRAVEL_HISTORY = 98;
    public static final int DIRECT_CONTACT = 99;

    //Default markers
    public static final LatLng JOS_HOSPITAL = new LatLng(9.906647, 8.954732);

    public static final LatLng PLATEAU_SPECIALIST = new LatLng(9.896261, 8.884068);

    public static final LatLng VETINARY_INSTITUTE = new LatLng(9.702210, 8.779535);

    private static final int LOCATION_PERMISSION_ID = 1;

    //FCM
    public static final String FCM_NOTIFICATION_ID = "222";
    public static final int NOTIFICATION_ID = 101;
    public static final String FCM_NOTIFICATION_CHANNEL = "cloud_messages_channel";


    //Util methods

    public static String formatNumber(long number){
        NumberFormat formatter = new DecimalFormat("#,###");
        double num = (double) number;
        return formatter.format(num);
    }

    //Key generator
    public static String generateKey(){
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(0, Math.min(uuid.length(), 8));

        Date date= new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);

        String key = timestamp.toString() + uuid;
        return key.replaceAll("[-+.^:,]","");
    }

    //Check whether the user allowed the location
    public static  boolean checkLocationPermissions(Context context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    //If location is not allowed, request location
    public static void requestLocationPermissions(Activity activity){
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_ID
        );
    }


    //Check for available network
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    //Check for an active internect connection
    public static boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
    }
}
