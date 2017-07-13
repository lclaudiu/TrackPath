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

class ReadFileTask extends AsyncTask<Object, Integer, Track> {
    private static final int FILE_NAME_INDEX = 0;
    private static final int CALLBACK_INDEX = 1;

    private TrackModelInterface mCallBack;

    protected Track doInBackground(Object... param) {
        mCallBack = (TrackModelInterface) param[CALLBACK_INDEX];
        FileInputStream fis;
        BufferedReader reader;
        URI fileUri;
        Track track = null;

        try {
            fileUri = new URI(null, null, Environment.getExternalStorageDirectory().toURI().toString() + Constants.DIRECTORY_NAME + "/" + param[FILE_NAME_INDEX] + ".txt", null, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        File file = new File(fileUri);
        if (file.exists()) {
            try {
                fis = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(fis));
                String line;
                JsonParser parser;
                JsonElement mJson;
                Gson gson = new Gson();
                LinkedList<Location> locationsList = new LinkedList<>();

                do {
                    line = reader.readLine();
                    if (!TextUtils.isEmpty(line)) {
                        parser = new JsonParser();
                        mJson = parser.parse(line);
                        Location location = gson.fromJson(mJson, Location.class);
                        locationsList.add(location);
                    }
                } while (line != null);

                if (locationsList.size() > 0) {
                    track = new Track((String) param[FILE_NAME_INDEX], locationsList);
                    track.path();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return track;
        }

        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(Track track) {
        if (mCallBack != null && track != null) {
            mCallBack.updateUI(track);
        }
    }
}
