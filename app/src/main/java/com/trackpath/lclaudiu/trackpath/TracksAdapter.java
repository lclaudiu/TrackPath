package com.trackpath.lclaudiu.trackpath;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.ViewHolder> {
    private LinkedList<Track> mTracks;
    private TrackClickListener mListener;

    static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        int mPosition;

        TextView mDate;
        TextView mLocation;

        private TracksAdapter mAdapter;

        ViewHolder(View itemLayoutView, TracksAdapter adapter) {
            super(itemLayoutView);

            mAdapter = adapter;
            mDate = (TextView) itemLayoutView.findViewById(R.id.track_date);
            mLocation = (TextView) itemLayoutView.findViewById(R.id.track_location);

            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mAdapter.clickedBook(mPosition);
        }
    }

    TracksAdapter(TrackClickListener listener, LinkedList<Track> tracks) {
        mListener = listener;
        mTracks = tracks;
    }

    @Override
    public TracksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.track_item, parent, false);

        return new ViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mPosition = position;
        holder.mDate.setText("Date : " + Utils.getStringDate(Long.parseLong(mTracks.get(position).getName())));
        Location locatin = mTracks.get(position).getTrackPoints().get(0);
        holder.mLocation.setText("Location: (" + locatin.getLatitude() + "," + locatin.getLongitude() + ")");
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    private void clickedBook(int position) {
        mListener.displayTrack(position);
    }
}