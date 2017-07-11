package com.trackpath.lclaudiu.trackpath.interfaces;

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
}
