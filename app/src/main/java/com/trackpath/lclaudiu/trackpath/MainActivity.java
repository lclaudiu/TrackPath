package com.trackpath.lclaudiu.trackpath;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.trackpath.lclaudiu.trackpath.interfaces.MapCallbacksInterface;
import com.trackpath.lclaudiu.trackpath.interfaces.PresenterInterface;

import java.util.LinkedList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, MapCallbacksInterface {

    static final int PICK_TRACK = 0;

    private GoogleMap mMap;
    private PresenterInterface mPresenter;
    public static LinkedList<Track> mTracksList;

    private Track mTrackDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mPresenter == null) {
            mPresenter = new TrackPresenter(MainActivity.this, MainActivity.this);
        }

        findViewById(R.id.rec_stop_track_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    if (mPresenter == null) {
                        mPresenter = new TrackPresenter(MainActivity.this, MainActivity.this);
                    }
                    if (mPresenter != null) {
                        mPresenter.startRecoding();
                    }
                } else {
                    if (mPresenter != null) {
                        mPresenter.stopRecording();
                    }
                }
            }
        });

        findViewById(R.id.show_all_tracks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null) {
                    mPresenter.getListOfTracks();
                }
            }
        });

        findViewById(R.id.clear_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.clear();
                }
                mTrackDisplayed = null;
                mTracksList = null;
                findViewById(R.id.show_info).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.show_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackInfoDialog.showDialog(MainActivity.this, mTrackDisplayed);
            }
        });

        // if someone wants to break the app will remove the permissions while the app is running
        permissionForReadWriteDisk();
        Utils.createTracksDirectory(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utils.isMyServiceRunning(this, LocationService.class)) {
            ((CheckBox) findViewById(R.id.rec_stop_track_button)).setChecked(true);

            if (mPresenter == null) {
                mPresenter = new TrackPresenter(MainActivity.this, MainActivity.this);
            }
            mPresenter.bindModelToService();
        } else {
            ((CheckBox) findViewById(R.id.rec_stop_track_button)).setChecked(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.disconnectPresenter();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(44.439663, 26.096306);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Bucharest"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionLocation();
            return;
        }
        mMap.setMyLocationEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    @Override
    public void updateUI(PolylineOptions polyline, LatLngBounds bounds, boolean livePosition) {
        if (mMap != null && polyline != null && bounds != null) {
            if (livePosition) {
                mMap.addPolyline(polyline.color(Color.RED));
            } else {
                mMap.addPolyline(polyline.color(Color.BLUE));
            }
            if (mTrackDisplayed == null || (mTrackDisplayed != null && !livePosition)) {
                int padding = 20;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }
        }
    }

    @Override
    public void displayTrack(Track track, boolean livePosition) {
        updateUI(track.getPolyline(), track.getBounds(), livePosition);
    }

    @Override
    public void displayTracksList(LinkedList<Track> tracksList) {
        if (tracksList != null && tracksList.size() > 0) {
            mTracksList = tracksList;

            Intent chooseTrack = new Intent(this, AllTracksActivity.class);
            startActivityForResult(chooseTrack, PICK_TRACK);
        } else {
            Toast.makeText(this, "No file recorded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_TRACK) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", -1);
                if (mTracksList != null
                        && position > -1
                        && position < mTracksList.size()) {
                    if (mMap != null) {
                        mMap.clear();
                    }
                    mTrackDisplayed = mTracksList.get(position);
                    displayTrack(mTracksList.get(position), false);
                    findViewById(R.id.show_info).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void permissionForReadWriteDisk() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.access_to_memory), Snackbar.LENGTH_INDEFINITE);
                mySnackbar.setAction(getString(R.string.settings), new PermisionSettings(this));
                mySnackbar.show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSIONS_FOR_WRITE_EXTERNAL_STORAGE_ID);
            }
        }
    }

    private void permissionLocation() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.access_to_fine_location), Snackbar.LENGTH_INDEFINITE);
                mySnackbar.setAction(getString(R.string.settings), new PermisionSettings(this));
                mySnackbar.show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSIONS_FOR_FINE_LOCATION_ID);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_FOR_WRITE_EXTERNAL_STORAGE_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionLocation();
                }
            }
            break;
            case Constants.PERMISSIONS_FOR_FINE_LOCATION_ID:
                break;
        }
    }
}
