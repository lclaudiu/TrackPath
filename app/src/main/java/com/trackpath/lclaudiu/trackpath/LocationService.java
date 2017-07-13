package com.trackpath.lclaudiu.trackpath;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();

    Messenger mClientMessanger;

    static final int START_GET_LOCATION = 1;

    static final int STOP_GET_LOCATION = 2;

    static final int MSG_LOCATION = 3;

    static final String MSG_LOCATION_KEY = "location";

    // the interval to get updates about location. This should be lower/higher based on tests
    private static final long NORMAL_UPDATE_INTERVAL = 4 * 1000; //milliseconds

    // the minimum interval to get updates about location
    private static final long FAST_UPDATE_INTERVAL = NORMAL_UPDATE_INTERVAL / 2;

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 11223344;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;


    private boolean mChangingConfiguration = false;

    /**
     * The current location.
     */
    private Location mLocation;

    /**
     * The name of the file where is recorded the current track
     */
    private String mCurrentTrackFileName;

    private NotificationManager mNotificationManager;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_GET_LOCATION:
                    mClientMessanger = msg.replyTo;
                    getLastLocation();
                    break;
                case STOP_GET_LOCATION:
                    stopForeground(true);
                    removeLocationUpdates();
                    // set to null the name of the last file saved
                    mCurrentTrackFileName = null;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Display a notification to indicate that the service is running.
        startForeground(NOTIFICATION_ID, showNotification());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // a new location that needs to be saved
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    public void requestLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the location updates
     */
    public void removeLocationUpdates() {
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stopSelf();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a notification while this service is running.
     */
    private Notification showNotification() {
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent activityIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        return new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launch, "Start the activity", activityIntent)
                .setContentText("The path is recorded")
                .setContentTitle("Active track service")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launch)
                .setTicker("Active track service")
                .setWhen(System.currentTimeMillis())
                .build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLocation = task.getResult();
                        if (TextUtils.isEmpty(mCurrentTrackFileName)) {
                            mCurrentTrackFileName = mLocation.getTime() + "";
                        }
                        onNewLocation(mLocation);
                        requestLocationUpdates();
                    } else {
                        Log.w(TAG, "Failed to get location.");
                    }
                }
            });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Message responseMessage = Message.obtain(null, LocationService.MSG_LOCATION);
        String locationGson = new Gson().toJson(location);
        saveLocationToFile(locationGson + "\n");

        if (mClientMessanger == null) {
            return;
        }
        Bundle msgBundle = new Bundle();
        msgBundle.putString(MSG_LOCATION_KEY, mCurrentTrackFileName);
        responseMessage.setData(msgBundle);
        try {
            mClientMessanger.send(responseMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(NORMAL_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FAST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void saveLocationToFile(String location) {
        if (!TextUtils.isEmpty(mCurrentTrackFileName)) {
            try {
                URI fileUri;
                try {
                    fileUri = new URI(null, null, Environment.getExternalStorageDirectory().toURI().toString() + Constants.DIRECTORY_NAME + "/" + mCurrentTrackFileName + ".txt", null, null);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    return;
                }
                File file = new File(fileUri);
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fileWriter = new FileWriter(file, true);
                fileWriter.append(location);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}