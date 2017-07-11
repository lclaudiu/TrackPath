package com.trackpath.lclaudiu.trackpath;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kanly on 11/07/2017.
 */

public class Utils {

    /**
     * Creates the directory where the tracks are saved
     * @param context
     * @return
     */
    public static boolean createTracksDirectory(Context context) {
        boolean directoryExists = false;
        File directory = null;
        URI directoryUri = null;

        try {
            directoryUri = new URI(null, null, Environment.getExternalStorageDirectory().toURI().toString() + Constants.DIRECTORY_NAME, null, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        directory = new File(directoryUri);
        if (!directory.exists()) {
            directoryExists = makeDirectory(directoryUri);
        }

        return directoryExists;

    }

    private static boolean makeDirectory(URI directoryName) {
        boolean createdDirectory = false;
        File directory = new File(directoryName);

        if (!directory.exists()) {
            createdDirectory = directory.mkdir();
        }

        return createdDirectory;
    }

    public static String getStringDate(Long timeMiis) {
        Date dateToConvert = new Date(timeMiis);
        Calendar calendarDate = Calendar.getInstance(Locale.getDefault());
        calendarDate.setTime(dateToConvert);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        return format.format(calendarDate.getTime());
    }
}
