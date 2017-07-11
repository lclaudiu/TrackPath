package com.trackpath.lclaudiu.trackpath.interfaces;

import android.app.Activity;

/**
 * This Interface provides the actions that the View can demand from the Presenter.
 */

public interface PresenterInterface {
    /**
     * Request to start recording a path
     */
    void startRecoding();

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
     * @param name the name of the path (file)
     */
    void getTrack(String name);

    /**
     * This must be called when the activity is on stop so that to detach from the service
     */
    void disconnectPresenter();
}
