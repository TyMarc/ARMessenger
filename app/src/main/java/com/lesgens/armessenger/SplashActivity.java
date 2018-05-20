package com.lesgens.armessenger;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.ar.core.ArCoreApk;
import com.lesgens.armessenger.controller.AppController;
import com.lesgens.armessenger.controller.PermissionController;
import com.lesgens.armessenger.listeners.PositionListener;

public class SplashActivity extends Activity implements OnMapReadyCallback, PositionListener {
    private static final String TAG = "SplashActivity";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9001;
    private static final float DEFAULT_ZOOM = 15f;
    private MapView mapView;
    private GoogleMap googleMap;
    private ProgressBar progressBar;
    private View startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progress_bar);
        startButton = findViewById(R.id.start_button);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        maybeEnableArButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady");
        this.googleMap = googleMap;
        setUpLocation();
    }

    private void setUpLocation() {
        if(googleMap == null) {
            return;
        }

        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        googleMap.setMapType(new GoogleMapOptions().liteMode(true).mapType(GoogleMap.MAP_TYPE_NORMAL).getMapType());

        PermissionController.getInstance().getLocationPermission(this);

        if(PermissionController.getInstance().isLocationPermissionGranted()) {
            AppController.getInstance().getDeviceLocation(this);
        }
    }

    void maybeEnableArButton() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            startButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            startButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPositionFound(LatLng location) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                location, DEFAULT_ZOOM));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionController.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
        setUpLocation();
    }
}
