package com.hdp.careup.services;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class ChildLocationService extends Service {

    final int LOCATION_REFRESH_TIME = 150;
    final int LOCATION_REFRESH_DISTANCE = 1;
    LocationManager mLocationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Nullable
    @Override
    public ComponentName startService(Intent service) {

        System.out.println("here we go.. this is the service startService()");

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Activity compact has executed...!");
//            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);

        return START_STICKY;
    }

    private final LocationListener locationListener = new LocationListener() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationChanged(@NonNull Location location) {
            System.out.println("lat lang has executed........!");
            Toast.makeText(getApplicationContext(), "Location", Toast.LENGTH_SHORT).show();
        }
    };
}
