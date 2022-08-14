package com.hdp.careup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    String lat, lang;
    private GoogleMap map;
    private Marker marker_me, marker_child;
    String childName, uuid;
    private boolean exit = false;
    OkHttpClient client = new OkHttpClient();
    MarkerOptions markerOptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        uuid = getIntent().getExtras().getString("uuid");
        childName = getIntent().getExtras().getString("childName");
        Toast.makeText(this, "This is UUID : " + uuid, Toast.LENGTH_SHORT).show();


    }

    private void setMarkerChild(LatLng latLng) {
        if (marker_child == null) {
            markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            markerOptions.title(childName);
            markerOptions.position(latLng);
            marker_child = map.addMarker(markerOptions);
            moveCamera(latLng);
        } else {
            marker_child.setPosition(latLng);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        UiSettings mapUI = map.getUiSettings();
        mapUI.setZoomControlsEnabled(true);
        mapUI.setAllGesturesEnabled(true);
        mapUI.setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(false);


        /*locating child starts*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("CHILD_LOCATION_THREAD", "run: Thead is running");
                while(!exit) {
                    Log.e("CHILD_LOCATION_WHILE", "run: while is running");
                    Request request = new Request.Builder()
                            .url("http://helpthem.info/getdata.php?uid=" + uuid)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            System.err.println("ERROR");
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
//                                System.out.println("Response : " + response.body().string());0
                                System.out.println(response.code());
                                try {
                                    JSONObject locationData = new JSONObject(response.body().string());
                                    lat = locationData.getString("lat");
                                    lang = locationData.getString("lang");

                                    // ToDO................
//                                    setMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lang)), marker, "Bus");
                                    LatLng latLng = new LatLng(Double.parseDouble(lang), Double.parseDouble(lat));
                                    runOnUiThread(() -> {
                                        Log.i("INSIDE THE THREAD", "onResponse: ------------------------------------------");
                                        setMarkerChild(latLng);
                                    });

                                    Log.e("LOCATION-RECIEVE", "onResponse: Longitude: " + lang + " Latitude: " + lat);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        /*locating child stop*/


        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(1000)
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location lastLocation = locationResult.getLastLocation();
                LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                if (marker_me == null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                    markerOptions.title("Me");
                    markerOptions.position(latLng);
                    marker_me = map.addMarker(markerOptions);
//                    moveCamera(latLng);
                } else {
                    marker_me.setPosition(latLng);
                }
            }
        }, Looper.getMainLooper());
    }

    public void moveCamera(LatLng latLng) {
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latLng)
                .zoom(15f)
                .build();


        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exit = true;
        super.onDestroy();
    }
}