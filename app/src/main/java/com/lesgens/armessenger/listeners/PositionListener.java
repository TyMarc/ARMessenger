package com.lesgens.armessenger.listeners;

import com.google.android.gms.maps.model.LatLng;

public interface PositionListener {
    void onPositionFound(LatLng location);
}
