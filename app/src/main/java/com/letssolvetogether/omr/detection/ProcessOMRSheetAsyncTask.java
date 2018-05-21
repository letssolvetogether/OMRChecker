package com.letssolvetogether.omr.detection;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.cameraview.CameraView;
import com.letssolvetogether.omr.evaluation.EvaluationUtil;
import com.letssolvetogether.omr.object.CameraCustomView;
import com.letssolvetogether.omr.object.OMRSheet;
import com.letssolvetogether.omr.object.OMRSheetCorners;
import com.letssolvetogether.omr.ui.customviews.CustomView;

public class ProcessOMRSheetAsyncTask extends AsyncTask<CameraCustomView, Void, Boolean> {

    private static String TAG="ProcessOMRSheetAsyncTask";

    OMRSheet omrSheet;
    OMRSheetCorners omrSheetCorners;
    Bitmap bmpOMRSheet;
    CameraCustomView cameraCustomView;
    CameraView mCameraView;
    CustomView customView;
    DetectionUtil detectionUtil = new DetectionUtil();

    @Override
    protected Boolean doInBackground(CameraCustomView... cameraCustomViews) {
        Log.i(TAG, "doInBackground");

        cameraCustomView = cameraCustomViews[0];
        this.mCameraView = cameraCustomView.getCameraView();
        this.customView =  cameraCustomView.getCustomView();
        this.omrSheet = cameraCustomView.getOmrSheet();

        bmpOMRSheet = this.mCameraView.getPreviewFrame();

        omrSheetCorners = detectionUtil.detectOMRSheetCorners(bmpOMRSheet);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.i(TAG, "onPostExecute");
        if(omrSheetCorners == null){
            mCameraView.requestPreviewFrame();
        }else{
            customView.setVisibility(View.VISIBLE);

            omrSheet.setBmpOMRSheet(bmpOMRSheet);
            omrSheet.setOmrSheetBlock();
            omrSheet.setOmrSheetCorners(omrSheetCorners);

            detectionUtil.findROIofOMR(omrSheet);
            int score = new EvaluationUtil().getScore(omrSheet);
            customView.setScore(score);
            Log.i(TAG,"DONE");
        }
    }

}