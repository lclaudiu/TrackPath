package com.trackpath.lclaudiu.trackpath.interfaces;

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
}
