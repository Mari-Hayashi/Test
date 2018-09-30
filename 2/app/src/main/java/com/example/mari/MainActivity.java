package com.example.mari;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;

import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public LocationManager locationManager;


    private TextView textView;
    private Button button;

    private float distance_m; // total distance
    public static float distance; // momental distance
    public static float angle; // angle

    private Loc p_prev;
    private Loc p_now;
    private Location temp;

    private String s;

    private int INTERVAL;
    private float MIN_DIST;

    private int min;
    private int hr;
    public static boolean tracking;

    private boolean loading;

    //private ImageView start_iv;
    //private ImageView walk_iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        distance_m = 0;
        textView = findViewById(R.id.shinsou);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        p_prev = new Loc(0,0);
        p_now = new Loc (0,0);
        button = findViewById(R.id.button1);
        INTERVAL = 2000;
        MIN_DIST = 1f;
        tracking = true;
        loading = false;
        //start_iv = findViewById(R.id.flag);
        //walk_iv = findViewById(R.id.mofu);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        } else {
            locationStart();

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, MIN_DIST, this);
        }
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { end();}


        });
        //timer = new Timer();
        //TimerTask timetask = showtext();

    }
    private void locationStart(){
        //start_iv.setTranslationX(MyView1.CENTER_X - 30);
        //start_iv.setTranslationY(MyView1.CENTER_Y - 20);
        //walk_iv.setVisibility(View.INVISIBLE);

        textView.setText("Application Initiated");
        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled");
        } else {
            // GPSを設定するように促す
            Intent settingsIntent =
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,INTERVAL, MIN_DIST, this);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 位置測定を始めるコードへ跳ぶ
                locationStart();
            } else {
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
@Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }
@Override
    public void onLocationChanged(Location location) {
        if (tracking) {
            boolean shinsou = false; // true at initial and no need to calculate distance

            if (p_now.lati == 0 || p_prev.lati == 0) shinsou = true;

            p_prev.copy(p_now);
            p_now.update((float) location.getLatitude(), (float) location.getLongitude());
            if (shinsou) {
                textView.setText("Loadiing ... ");
                temp = location;
                Date currentTime = Calendar.getInstance().getTime();
                min = currentTime.getMinutes();
                hr = currentTime.getHours();
                return;
            }
            float[] results = new float[3];
            Location.distanceBetween(p_prev.lati, p_prev.longi, p_now.lati, p_prev.longi, results);


            distance = results[0];
            distance_m += distance;
            angle = temp.bearingTo(location);


            if (distance != 0) {
                findViewById(R.id.view1).invalidate();
            }
            String m = Integer.toString(min);
            if (min < 10){
                m = "0" + m;
            }

            s = "Start Tracking: " + Integer.toString(hr) + ":" + m + "\nTotal Distance:" + Float.toString(distance_m) + "m\n";
            textView.setText(s);
/*
        try{
            OutputStream out = openFileOutput("a.txt",MODE_PRIVATE);
            PrintWriter writer =
            new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
            writer.append(s);
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        */

            // ((ViewClass)findViewById(R.id.view1)).draw1();
            temp = location;
            loading = false;

           // start_iv.setTranslationX(MyView1.CENTER_X - 25);
           // start_iv.setTranslationY(MyView1.CENTER_Y - 25);
           // walk_iv.setVisibility(View.VISIBLE);
           // walk_iv.setTranslationX(MyView1.cur_x - 25);
           // walk_iv.setTranslationY(MyView1.cur_y - 25);
        }
    }
    public void end(){
        if (!loading) {
            if (tracking) {
                tracking = false;
                findViewById(R.id.view1).invalidate();
                Date currentTime = Calendar.getInstance().getTime();
                min = currentTime.getMinutes();
                hr = currentTime.getHours();
                s = "End Tracking: " + Integer.toString(hr) + ":" + Integer.toString(min) + "\n";
                textView.setText(textView.getText() + s);
                button.setBackgroundResource(R.drawable.b_quit);


            } else {
                /*button.setText("Loading");
                loading = true;
                tracking = true;
                distance_m = 0;
                p_prev.lati = 0;
                p_prev.longi = 0;
                p_now.lati = 0;
                p_now.longi = 0;
                */
                finish();

            }
        }
    }
@Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }

}




