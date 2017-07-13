package com.trackpath.lclaudiu.trackpath.interfaces;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.trackpath.lclaudiu.trackpath.Track;

import java.util.LinkedList;

/**
 * This Interface must be implemented by the Presenter who wants to get responses from Model.
 */

public interface TrackModelToPresenterInterface {
    /**
     * Callback from Model to Presenter
     * @param tracksList - returned list of tracks
     */
    void returnListOfTracks(LinkedList<Track> tracksList);

    /**
     * Callback from Model to Presenter
     * @param track - returned track
     */
    void returnTrack(Track track);

    /**
     * This is a callback to display the ongoing track
     * @param track - the track to be displayed
     */
    void updateUI(Track track);
}
