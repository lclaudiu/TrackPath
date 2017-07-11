package com.trackpath.lclaudiu.trackpath.interfaces;

import com.trackpath.lclaudiu.trackpath.Track;

import java.util.LinkedList;

/**
 * This Interface must be implemented by the activity who wants to get a response from Presenter
 */

public interface MapCallbacksInterface {
    /**
     * This is a callback to display tje ongoing track
     * @param track - the track to be displayed
     */
    void updateUI(Track track);

    /**
     * This is a callback to display one single path
     * @param track - the track to be displayed
     */
    void displayTrack(Track track);

    /**
     * This will return all the paths so that the user can choose one of them to display
     * @param tracksList - the list with oll the tracks recorded
     */
    void displayTracksList(LinkedList<Track> tracksList);
}
