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

    private Activity mActivity;
    private MapCallbacksInterface mActivityCallBack;
    boolean mBound = false;
    private TrackModelInterface mTrackModel;

    public TrackPresenter(Activity activity, MapCallbacksInterface callBack) {
        this.mActivity = activity;
        this.mActivityCallBack = callBack;
        if (mTrackModel == null) {
            mTrackModel = new TrackModel(activity, this);
        }
    }

    @Override
    public void startRecoding() {
        if (mTrackModel != null) {
            mTrackModel.startRecording();
        }
    }

    @Override
    public void stopRecording() {
        if (mTrackModel != null) {
            mTrackModel.stopRecording();
        }
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


    @Override
    public void disconnectPresenter() {
        if (mTrackModel != null) {
            mTrackModel.disconnectModelFromService();
        }
    }
}
