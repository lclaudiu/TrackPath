package com.trackpath.lclaudiu.trackpath;

import android.location.Location;

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
}
