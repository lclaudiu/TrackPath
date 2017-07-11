package com.trackpath.lclaudiu.trackpath;

import android.app.Activity;

import com.trackpath.lclaudiu.trackpath.interfaces.MapCallbacksInterface;
import com.trackpath.lclaudiu.trackpath.interfaces.PresenterInterface;
import com.trackpath.lclaudiu.trackpath.interfaces.TrackModelInterface;
import com.trackpath.lclaudiu.trackpath.interfaces.TrackModelToPresenterInterface;

import java.util.LinkedList;

/**
 * The presenter class
 */

public class TrackPresenter implements PresenterInterface, TrackModelToPresenterInterface {

    private MapCallbacksInterface mActivityCallBack;
    boolean mBound = false;
    private TrackModelInterface mTrackModel;

    @Override
    public void startRecoding(Activity activity, MapCallbacksInterface callBack) {
        this.mActivityCallBack = callBack;
        if (mTrackModel == null) {
            mTrackModel = new TrackModel(activity, this);
        }

        mTrackModel.startRecording();
    }

    @Override
    public void stopRecording() {
        mTrackModel.stopRecording();
    }

    @Override
    public void getListOfTracks() {

    }

    @Override
    public void getTrack(String name) {

    }

    @Override
    public void returnListOfTracks(LinkedList<Track> tracksList) {
        if (this.mActivityCallBack != null) {
            this.mActivityCallBack.displayTracksList(tracksList);
        }
    }

    @Override
    public void returnTrack(Track track) {
        if (this.mActivityCallBack != null) {
            this.mActivityCallBack.displayTrack(track);
        }
    }
}
