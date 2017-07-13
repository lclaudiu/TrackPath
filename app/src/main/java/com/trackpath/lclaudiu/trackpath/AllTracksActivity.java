package com.trackpath.lclaudiu.trackpath;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

/**
 * This class is displaying a list with all the tracks recorded
 */

public class AllTracksActivity extends AppCompatActivity implements TrackClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_all_tracks);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.all_tracks_list_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        TracksAdapter mAdapter = new TracksAdapter(this, MainActivity.mTracksList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void displayTrack(int position) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("position", position);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
