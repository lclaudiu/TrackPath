package com.trackpath.lclaudiu.trackpath;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.trackpath.lclaudiu.trackpath.interfaces.TrackModelInterface;
import com.trackpath.lclaudiu.trackpath.interfaces.TrackModelToPresenterInterface;

import java.util.LinkedList;

/**
 * This class represents the Model.
 * From here is started the service.
 * Here are readed the list of paths.
 * From here is returned a specific path.
 */

public class TrackModel implements TrackModelInterface {
    private TrackModelToPresenterInterface mPresenter;
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

    /**
     * This is the name of the file where the active service is saving the path
     */
    private String mCurrentPathName;

    TrackModel(Context context, TrackModelToPresenterInterface presenter) {
        this.mContext = context;
        this.mPresenter = presenter;
    }

    /**
     * First start service and after that bind to it
     */
    @Override
    public void startRecording() {
        if (mContext != null && !mIsBound) {
            if (!Utils.isMyServiceRunning(mContext, LocationService.class)) {
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
        if (Utils.isMyServiceRunning(mContext, LocationService.class) && mIsBound) {
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
        new ReadAllFileTask().execute(this, mCurrentPathName);
    }

    @Override
    public void getTrack(String name) {

    }

    @Override
    public void disconnectModelFromService() {
        doUnbindService();
    }

    @Override
    public void updateUI(Track track) {
        if (mPresenter != null) {
            mPresenter.updateUI(track);
        }
    }

    @Override
    public void bindToService() {
        if (Utils.isMyServiceRunning(mContext, LocationService.class) && !mIsBound) {
            doBindService();
        }
    }

    @Override
    public void updateMapWithAllTracks(LinkedList<Track> tracks) {
        if (mPresenter != null) {
            mPresenter.returnListOfTracks(tracks);
        }
    }

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LocationService.MSG_LOCATION:
                    mCurrentPathName = msg.getData().getString(LocationService.MSG_LOCATION_KEY);
                    new ReadFileTask().execute(mCurrentPathName, TrackModel.this);
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
        }
    };

    private void doBindService() {
        mContext.bindService(new Intent(mContext, LocationService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void doUnbindService() {
        if (mIsBound) {
            // Detach existing connection.
            mContext.unbindService(mConnection);
            mIsBound = false;
            mCurrentPathName = null;
        }
    }
}