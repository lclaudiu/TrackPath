package com.trackpath.lclaudiu.trackpath;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.trackpath.lclaudiu.trackpath.interfaces.PresenterInterface;
import com.trackpath.lclaudiu.trackpath.interfaces.TrackModelInterface;

/**
 * This class represents the Model.
 * From here is started the service.
 * Here are readed the list of paths.
 * From here is returned a specific path.
 */

public class TrackModel implements TrackModelInterface {
    private PresenterInterface mPresenter;
    private Context mContext;

    /**
     * Messenger to send messages to service.
     */
    private Messenger mService = null;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    private boolean mIsBound;

    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    public TrackModel(Context context, PresenterInterface presenter) {
        this.mContext = context;
        this.mPresenter = presenter;
    }

    /**
     * First start service and after that bind to it
     */
    @Override
    public void startRecording() {
        if (mContext != null && !mIsBound) {
            if (!isMyServiceRunning(LocationService.class)) {
                Intent intent = new Intent(mContext, LocationService.class);
                mContext.startService(intent);
            }

            doBindService();
        }
    }

    /**
     * First unbind from service and after that stop the service
     */
    @Override
    public void stopRecording() {
        if (isMyServiceRunning(LocationService.class) && mIsBound) {
            Message msg = Message.obtain(null, LocationService.STOP_GET_LOCATION);
            msg.replyTo = mMessenger;
            try {
                doUnbindService();
                mService.send(msg);
                mIsBound = false;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getListOfTracks() {

    }

    @Override
    public void getTrack(String name) {

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LocationService.MSG_LOCATION:
                    // update UI with the new location
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mIsBound = true;
            mService = new Messenger(service);

            // We want to monitor the service for as long as we are connected to it.
            try {
                Message msg = Message.obtain(null, LocationService.START_GET_LOCATION);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mIsBound = false;

            // As part of the sample, tell the user what happened.
            Toast.makeText(mContext, "Disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        mContext.bindService(new Intent(mContext, LocationService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach existing connection.
            mContext.unbindService(mConnection);
            if (mContext.stopService(new Intent(mContext, LocationService.class))) {
                mIsBound = false;
            }
        }
    }
}
