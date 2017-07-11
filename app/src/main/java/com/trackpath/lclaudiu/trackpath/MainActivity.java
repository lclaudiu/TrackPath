package com.trackpath.lclaudiu.trackpath;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trackpath.lclaudiu.trackpath.interfaces.MapCallbacksInterface;
import com.trackpath.lclaudiu.trackpath.interfaces.PresenterInterface;

import java.util.LinkedList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, MapCallbacksInterface {

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private PresenterInterface mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.rec_stop_track_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((CheckBox) v).isChecked()) {
                    if (mPresenter == null) {
                        mPresenter = new TrackPresenter();
                    }

                    mPresenter.startRecoding(MainActivity.this, MainActivity.this);
                } else {
                    if (mPresenter != null) {
                        mPresenter.stopRecording();
                    }
                }
            }
        });

        // if someone wants to break the app will remove the permissions while running the app
        permissionForReadWriteDisk();
        Utils.createTracksDirectory(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(44.439663, 26.096306);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Bucharest"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionLocation();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mUiSettings = mMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true); //isChecked(R.id.zoom_buttons_toggle));
        mUiSettings.setCompassEnabled(true); //isChecked(R.id.compass_toggle));
        mUiSettings.setMyLocationButtonEnabled(true); //isChecked(R.id.mylocationbutton_toggle));
        mMap.setMyLocationEnabled(true); //isChecked(R.id.mylocationlayer_toggle));
        mUiSettings.setScrollGesturesEnabled(true); //isChecked(R.id.scroll_toggle));
        mUiSettings.setZoomGesturesEnabled(true); //isChecked(R.id.zoom_gestures_toggle));
        mUiSettings.setTiltGesturesEnabled(true); //isChecked(R.id.tilt_toggle));
        mUiSettings.setRotateGesturesEnabled(true); //isChecked(R.id.rotate_toggle));
    }

    @Override
    public void displayTrack(Track track) {

    }

    @Override
    public void displayTracksList(LinkedList<Track> tracksList) {

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
                // If request is cancelled, the result arrays are empty.
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
