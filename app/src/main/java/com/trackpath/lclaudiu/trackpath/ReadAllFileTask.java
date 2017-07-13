package com.trackpath.lclaudiu.trackpath;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.trackpath.lclaudiu.trackpath.interfaces.TrackModelInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

class ReadAllFileTask extends AsyncTask<Object, Integer, LinkedList<Track>> {
    private static final int CALLBACK_INDEX = 0;
    private static final int CURRENT_FILE_NAME_INDEX = 1;

    private TrackModelInterface mCallBack;

    protected LinkedList<Track> doInBackground(Object... param) {
        mCallBack = (TrackModelInterface) param[CALLBACK_INDEX];
        String currentFileName = (String) param[CURRENT_FILE_NAME_INDEX];
        FileInputStream fis;
        BufferedReader reader;
        URI directoryUri;
        LinkedList<Track> tracks;
        Track track = null;
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement mJson;
        String line;

        try {
            directoryUri = new URI(null, null, Environment.getExternalStorageDirectory().toURI().toString() + Constants.DIRECTORY_NAME + "/", null, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        File directory = new File(directoryUri);
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            tracks = new LinkedList<>();
            for (File file : files) {
                if (file.exists() && (TextUtils.isEmpty(currentFileName) || (!TextUtils.isEmpty(currentFileName) && !file.getName().contains(currentFileName)))) {
                    try {
                        fis = new FileInputStream(file);
                        reader = new BufferedReader(new InputStreamReader(fis));

                        LinkedList<Location> locationsList = new LinkedList<>();

                        do {
                            line = reader.readLine();
                            if (!TextUtils.isEmpty(line)) {
                                mJson = parser.parse(line);
                                Location location = gson.fromJson(mJson, Location.class);
                                locationsList.add(location);
                            }
                        } while (line != null);

                        if (locationsList != null && locationsList.size() > 0) {
                            track = new Track(file.getName().substring(0, file.getName().indexOf(".")), locationsList);
                            track.path();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    tracks.add(track);
                }
            }

            return tracks;
        }

        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(LinkedList<Track> tracks) {
        mCallBack.updateMapWithAllTracks(tracks);
    }
}
