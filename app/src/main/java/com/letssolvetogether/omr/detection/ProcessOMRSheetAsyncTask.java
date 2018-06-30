package com.letssolvetogether.omr.detection;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.cameraview.CameraView;
import com.letssolvetogether.omr.evaluation.EvaluationUtil;
import com.letssolvetogether.omr.object.OMRSheet;
import com.letssolvetogether.omr.object.OMRSheetCorners;
import com.letssolvetogether.omr.ui.customviews.CustomView;

public class ProcessOMRSheetAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private static String TAG="ProcessOMRSheetAsyncTask";

    OMRSheet omrSheet;
    OMRSheetCorners omrSheetCorners;
    Bitmap bmpOMRSheet;
    CameraView mCameraView;
    CustomView customView;
    DetectionUtil detectionUtil = new DetectionUtil();

    public ProcessOMRSheetAsyncTask(CameraView mCameraView, CustomView customView, OMRSheet omrSheet) {
        this.omrSheet = omrSheet;
        this.mCameraView = mCameraView;
        this.customView = customView;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.i(TAG, "doInBackground");

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