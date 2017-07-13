package com.trackpath.lclaudiu.trackpath.interfaces;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.trackpath.lclaudiu.trackpath.Track;

import java.util.LinkedList;

/**
 * This Interface must be implemented by the activity who wants to get a response from Presenter
 */

public interface MapCallbacksInterface {
    /**
     *     /**
     * This is a callback to display the ongoing track
     * @param polyline - the path to be displayed on the map
     * @param bounds - the area to be displayed
     * @param livePosition - indicates if a track to display is a live or an old one
     */
    void updateUI(PolylineOptions polyline, LatLngBounds bounds, boolean livePosition);

    /**
     * This is a callback to display one single path
     * @param track - the track to be displayed
     * @param livePosition - indicates if a track to display is a live or an old one
     */
    void displayTrack(Track track, boolean livePosition);

    /**
     * This will return all the paths so that the user can choose one of them to display
     * @param tracksList - the list with oll the tracks recorded
     */
    void displayTracksList(LinkedList<Track> tracksList);
}
