package com.hdp.careup.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.hdp.careup.Profile;
import com.hdp.careup.ProfileActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*public class LocationTransmitService extends Service {

    private static final String TAG = "LOCATION TRANSMIT SERVICE";
    private final OkHttpClient client = new OkHttpClient();
    private static boolean exit = false;
    private static final float LOCATION_REFRESH_DISTANCE = 1.0f;
    private static final long LOCATION_REFRESH_TIME = 1500;
    static String id = ViewTernScheduleAdapter.activity.getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("id", "");
    Context mContext = ViewTernScheduleAdapter.context;

    double longitude;
    double latitude;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Location Transmitting Service started.", Toast.LENGTH_LONG).show();

        class MyListener implements LocationListener {

            @Override
            public void onLocationChanged(@NonNull Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.i(TAG, "onLocationChanged: Called................");
            }
        }
        if (checkAndRequestPermissions()) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, new MyListener());
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!exit) {

                    Request request = new Request.Builder()
                            .url("http://helpthem.info/index.php?lat=" + longitude + "&lang=" + latitude + "&uid=" + id)
                            .build();
                    Log.e(TAG, "run: URL REQUEST :" + "http://helpthem.info/index.php?lat=" + longitude + "&lang=" + latitude + "&uid=" + id);
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            System.err.println("ERROR");
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                System.out.println("Response : " + response.body().string());
                                System.out.println(response.code());
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return START_STICKY;
    }


    public boolean checkAndRequestPermissions() {
        int internet = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.INTERNET);
        int loc = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) mContext, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
//        super.onDestroy();
        exit = true;
        Toast.makeText(this, "Location Transmitting Service destroyed.", Toast.LENGTH_LONG).show();
    }
}*/


public class ChildLocationService extends Service {

    private static final String TAG = "LOCATION TRANSMIT SERVICE";
    private final OkHttpClient client = new OkHttpClient();
    private static boolean exit = false;
    private static final float LOCATION_REFRESH_DISTANCE = 1.0f;
    private static final long LOCATION_REFRESH_TIME = 1000;
    private double longitude, latitude;
    private Location currentLocation;
    Handler mHandler = new Handler();
    Runnable runnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String uid = Profile.userID;

        exit = false;
        Toast.makeText(this, "Location Transmitting Service started.", Toast.LENGTH_LONG).show();


        // Traditional way of getting GPS data
        class MyListener implements LocationListener {

            @Override
            public void onLocationChanged(@NonNull Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.i("TAG", "onLocationChanged: Listening..............................................................");
            }
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, new MyListener());

        // Improved way of getting GPS data
//        new FallbackLocationTracker(this).start(new LocationTracker.LocationUpdateListener() {
//            @Override
//            public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
//                longitude = newLoc.getLongitude();
//                latitude = newLoc.getLatitude();
//                Log.i(TAG, "onLocationChanged: Listening..............................................................");
//            }
//        });

        new Thread(new Runnable() {

            @Override
            public void run() {

                while (!exit) {

                    Request request = new Request.Builder()
                            .url("http://helpthem.info/index.php?lat=" + longitude + "&lang=" + latitude + "&uid=" + uid)
                            .build();
                    Log.e("TAG", "URL REQUEST :" + "http://helpthem.info/index.php?lat=" + longitude + "&lang=" + latitude + "&uid=" + uid);
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            System.err.println("ERROR ----> " + e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                System.out.println("Response : " + response.body().string());
                                System.out.println(response.code());
                            }
                        }
                    });

                    Log.e("TAG", "onLocationChanged: Longitude: " + longitude + " Latitude: " + latitude + " ID: " + uid);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();



        /*runnable = new Runnable() {
            @Override
            public void run() {
                new FallbackLocationTracker(LocationTransmitService.this).start(new LocationTracker.LocationUpdateListener() {
                    @Override
                    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {

//                        Log.e(TAG, "onUpdate: Is getting called...............");
//                        Request request = new Request.Builder()
//                                .url("http://helpthem.info/index.php?lat=" + newLoc.getLongitude() + "&lang=" + newLoc.getLatitude() + "&uid=" + id)
//                                .build();
//                        Log.e(TAG, "run: URL REQUEST :" + "http://helpthem.info/index.php?lat=" + newLoc.getLongitude() + "&lang=" + newLoc.getLatitude() + "&uid=" + id);
//                        client.newCall(request).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                                System.err.println("ERROR");
//                            }
//
//                            @Override
//                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                                if (response.isSuccessful()) {
//                                    System.out.println("Response : " + response.body().string());
//                                    System.out.println(response.code());
//                                }
//                            }
//                        });
                        Log.e(TAG, "onUpdate: Longitude: " + newLoc.getLongitude() + " Latitude: " + newLoc.getLatitude());

                    }
                });
                mHandler.postDelayed(this, 2000);
            }
        };
        runnable.run();*/

        // Foreground Service Notification
        Intent intentForPending = new Intent(this, ProfileActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentForPending, PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "App Notification";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//        builder.setSmallIcon(R.drawable.marker);
        builder.setContentTitle("PubSee Live Location Service");
        builder.setContentText("Transmitting your realtime location. Do not close the application.");
        builder.setColor(Color.rgb(0, 70, 160));
        builder.setTicker("Ticker");
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();

        startForeground(99, notification);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mHandler.removeCallbacks(runnable);
        exit = true;
        Toast.makeText(this, "Location Transmitting Service destroyed.", Toast.LENGTH_LONG).show();
    }
}