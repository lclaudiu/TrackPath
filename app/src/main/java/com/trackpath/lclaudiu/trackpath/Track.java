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
    private PolylineOptions mPolyline = new PolylineOptions();
    private LatLngBounds mBounds;

    public Track(String name, LinkedList<Location> points) {
        this.mName = name;
        this.mTrackPoints = points;
    }

    public String getName() {
        return mName;
    }

    public LinkedList<Location> getTrackPoints() {
        return mTrackPoints;
    }

    public PolylineOptions getPolyline() {
        return mPolyline;
    }

    public LatLngBounds getBounds() {
        return mBounds;
    }

    public boolean path() {
        if (mTrackPoints != null
                && mTrackPoints.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (Location location : mTrackPoints) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                mPolyline.add(ll);
                builder.include(ll);
            }
            mBounds = builder.build();

            return true;
        }
        else {
            return false;
        }
    }
}
