package com.trackpath.lclaudiu.trackpath;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;

/**
 * This class will keep all the points of a single track
 */

public class Track {
    /**
     * the name of the file, it is the UTC time of the first location added
     */
    private String mName;
    private LinkedList<Location> mTrackPoints;

    void Track(String name, LinkedList<Location> points) {
        this.mName = name;
        this.mTrackPoints = points;
    }

    public String getmName() {
        return mName;
    }

    public LinkedList<Location> getmTrackPoints() {
        return mTrackPoints;
    }

    public boolean path(PolylineOptions polyline, LatLngBounds bounds) {
        if (mTrackPoints != null
                && mTrackPoints.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (Location location : mTrackPoints) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                polyline.add(ll);
                builder.include(ll);
            }
            bounds = builder.build();

            return true;
        }
        else {
            return false;
        }
    }
}
