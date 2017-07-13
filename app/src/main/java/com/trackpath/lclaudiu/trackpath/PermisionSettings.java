package com.trackpath.lclaudiu.trackpath;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

class PermisionSettings implements View.OnClickListener {
    private Activity mActivity;

    PermisionSettings(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
    }
}
