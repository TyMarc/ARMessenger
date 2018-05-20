package com.lesgens.armessenger.controller;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.lesgens.armessenger.listeners.PositionListener;

public class AppController {
    private static final String TAG = "AppController";

    private static AppController instance;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LatLng mDefaultLocation = new LatLng(45.502095, -73.570287);

    private AppController() { }

    public static AppController getInstance() {
        if(instance == null) {
            instance = new AppController();
        }

        return instance;
    }

    public void init(final Context context) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getDeviceLocation(final PositionListener positionListener) {
        try {
            if (PermissionController.getInstance().isLocationPermissionGranted()) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = (Location) task.getResult();
                            Log.i(TAG, "Found location: location=" + mLastKnownLocation);
                            if(positionListener != null) {
                                positionListener.onPositionFound(new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()));
                            }
                        } else {
                            Log.i(TAG, "Current location is null. Using defaults.");
                            Log.i(TAG, "Exception: %s", task.getException());
                            if(positionListener != null) {
                                positionListener.onPositionFound(mDefaultLocation);
                            }
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
