package com.letssolvetogether.omr.detection;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.cameraview.CameraView;
import com.letssolvetogether.omr.object.CameraCustomView;
import com.letssolvetogether.omr.object.OMRSheetCorners;
import com.letssolvetogether.omr.ui.customviews.CustomView;

public class ProcessOMRSheetAsyncTask extends AsyncTask<CameraCustomView, Void, Boolean> {

    private static String TAG="ProcessOMRSheetAsyncTask";

    OMRSheetCorners omrSheetCorners;
    Bitmap bmpOMRSheet;
    CameraCustomView cameraCustomView;
    CameraView mCameraView;
    CustomView customView;

    @Override
    protected Boolean doInBackground(CameraCustomView... cameraCustomViews) {
        Log.i(TAG, "doInBackground");

        cameraCustomView = cameraCustomViews[0];
        this.mCameraView = cameraCustomView.getCameraView();
        this.customView =  cameraCustomView.getCustomView();

        bmpOMRSheet = this.mCameraView.getPreviewFrame();

        omrSheetCorners = new DetectionUtil().detectOMRSheetCorners(bmpOMRSheet);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.i(TAG, "onPostExecute");
        if(omrSheetCorners == null){
            mCameraView.requestPreviewFrame();
        }else{
            customView.setVisibility(View.VISIBLE);
            Log.i(TAG,"DONE");
        }
    }

}
