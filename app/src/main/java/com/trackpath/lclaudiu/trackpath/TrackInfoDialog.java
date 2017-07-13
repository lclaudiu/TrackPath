package com.trackpath.lclaudiu.trackpath;

import android.app.Activity;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class TrackInfoDialog extends DialogFragment {
    public static final String DIALOG_FRAGMENT_TAG = "dialog fragment tag";
    private Track mTrack;

    public TrackInfoDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View mMainView = inflater.inflate(R.layout.dialog_track_info, null);
        ((TextView) mMainView.findViewById(R.id.start_date)).setText("Start time : " + Utils.getStringDate(Long.parseLong(mTrack.getName())));
        ((TextView) mMainView.findViewById(R.id.end_date)).setText("End time : " + Utils.getStringDate(mTrack.getTrackPoints().get(mTrack.getTrackPoints().size() - 1).getTime()));

        long duration = (mTrack.getTrackPoints().get(mTrack.getTrackPoints().size() - 1).getTime() - mTrack.getTrackPoints().get(0).getTime()) / 1000;
        Location location;
        float maxSpeed = 0;
        int distance = 0;

        for (int i = 0 ; i < mTrack.getTrackPoints().size(); i++) {
            location = mTrack.getTrackPoints().get(i);
            maxSpeed = maxSpeed > location.getSpeed() ? maxSpeed : location.getSpeed();
            if (i < mTrack.getTrackPoints().size() - 1) {
                distance += location.distanceTo(mTrack.getTrackPoints().get(i + 1));
            }
        }

        ((TextView) mMainView.findViewById(R.id.duration)).setText("Duration (s) : " + duration);
        ((TextView) mMainView.findViewById(R.id.number_of_points)).setText("Number of points : " + mTrack.getTrackPoints().size());
        ((TextView) mMainView.findViewById(R.id.distance)).setText("Distance (m) : " + distance);
        ((TextView) mMainView.findViewById(R.id.max_speed)).setText("Max speed (m/s) : " + maxSpeed);
        ((TextView) mMainView.findViewById(R.id.average_speed)).setText("Average speed(m/s) : " + ((float) distance / (float)duration));

        builder.setView(mMainView);
        builder.setCancelable(true);

        return builder.create();
    }

    public static void showDialog(Activity activity, Track track) {
        FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        TrackInfoDialog dialog = new TrackInfoDialog();
        dialog.mTrack = track;

        dialog.show(((FragmentActivity) activity).getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        ft.commit();
    }
}