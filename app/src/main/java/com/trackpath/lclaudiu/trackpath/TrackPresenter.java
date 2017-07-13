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

class TrackPresenter implements PresenterInterface, TrackModelToPresenterInterface {

    private Activity mActivity;
    private MapCallbacksInterface mActivityCallBack;
    private TrackModelInterface mTrackModel;

    TrackPresenter(Activity activity, MapCallbacksInterface callBack) {
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
        if (mTrackModel != null) {
            mTrackModel.getListOfTracks();
        }
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
            this.mActivityCallBack.displayTrack(track, false);
        }
    }

    @Override
    public void updateUI(Track track) {
        if (this.mActivityCallBack != null && track.getPolyline() != null && track.getBounds() != null) {
            this.mActivityCallBack.updateUI(track.getPolyline(), track.getBounds(), true);
        }
    }


    @Override
    public void disconnectPresenter() {
        if (mTrackModel != null) {
            mTrackModel.disconnectModelFromService();
        }
    }

    @Override
    public void bindModelToService() {
        if (mTrackModel == null) {
            mTrackModel = new TrackModel(mActivity, this);
        }

        mTrackModel.bindToService();
    }
}
