package com.trackpath.lclaudiu.trackpath.interfaces;

import com.trackpath.lclaudiu.trackpath.Track;

import java.util.LinkedList;

/**
 * This Interface provides the actions that the Presenter can demand from the Model.
 */

public interface TrackModelInterface {
    /**
     * Request to start recording a path
     */
    void startRecording();

    /**
     * Request to stop a recording
     */
    void stopRecording();

    /**
     * Request a list with all the recorded paths
     */
    void getListOfTracks();

    /**
     * Request for a single path
     *
     * @param name the name of the path (file)
     */
    void getTrack(String name);

    /**
     * This must be called when the activity is on stop so that the Model to detach from the service
     */
    void disconnectModelFromService();

    /**
     * This is a callback to display the ongoing track
     * @param track - the track to be displayed
     */
    void updateUI(Track track);

    /**
     * Demand model to bind to running service
     */
    void bindToService();

    /**
     * Callback for getting all files
     */
    void updateMapWithAllTracks(LinkedList<Track> tracks);
}
