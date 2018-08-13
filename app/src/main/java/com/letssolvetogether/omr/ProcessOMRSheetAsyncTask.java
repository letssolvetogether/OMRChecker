package com.letssolvetogether.omr;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.cameraview.CameraView;
import com.letssolvetogether.omr.detection.DetectionUtil;
import com.letssolvetogether.omr.evaluation.EvaluationUtil;
import com.letssolvetogether.omr.object.OMRSheet;
import com.letssolvetogether.omr.object.OMRSheetCorners;
import com.letssolvetogether.omr.utils.PrereqChecks;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class ProcessOMRSheetAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private static String TAG="ProcessOMRSheetAsyncTask";

    OMRSheet omrSheet;
    OMRSheetCorners omrSheetCorners;
    Bitmap bmpOMRSheet;
    Mat matOMR;
    CameraView mCameraView;
    //CustomView customView;
    LinearLayout linearLayout;
    ImageView iv;
    DetectionUtil detectionUtil;
    PrereqChecks prereqChecks;

    public ProcessOMRSheetAsyncTask(CameraView mCameraView, OMRSheet omrSheet) {
        this.omrSheet = omrSheet;
        this.mCameraView = mCameraView;

        detectionUtil = new DetectionUtil();
        prereqChecks = new PrereqChecks();

        linearLayout = new LinearLayout(mCameraView.getContext());
        iv = new ImageView(mCameraView.getContext());
        iv.setAdjustViewBounds(true);
        linearLayout.addView(iv);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.i(TAG, "doInBackground");

        bmpOMRSheet = this.mCameraView.getPreviewFrame();

        boolean isBlurry = prereqChecks.isBlurry(bmpOMRSheet);
        if(isBlurry) {
            mCameraView.requestPreviewFrame();
            return false;
        }

        matOMR = new Mat();
        Utils.bitmapToMat(bmpOMRSheet,matOMR);
        omrSheetCorners = detectionUtil.detectOMRSheetCorners(matOMR);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.i(TAG, "onPostExecute");
        if(omrSheetCorners == null){
            mCameraView.requestPreviewFrame();
        }else{
            omrSheet.setMatOMRSheet(matOMR);
            omrSheet.setWidth(matOMR.cols());
            omrSheet.setHeight(matOMR.rows());
            omrSheet.setOmrSheetBlock();
            omrSheet.setOmrSheetCorners(omrSheetCorners);

            Mat roiOfOMR = detectionUtil.findROIofOMR(omrSheet);
            omrSheet.setMatOMRSheet(roiOfOMR);

            Bitmap bmp = Bitmap.createBitmap(roiOfOMR.cols(), roiOfOMR.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(roiOfOMR, bmp);
            omrSheet.setBmpOMRSheet(bmp);

            int score = new EvaluationUtil(omrSheet).getScore();

            final AlertDialog.Builder dialogOMRSheetDisplay = new AlertDialog.Builder(mCameraView.getContext());

            //set custom title to dialog box
            TextView textView = new TextView(mCameraView.getContext());
            textView.setText(" Score: " + score);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(30);

            //set OMR Sheet to be displayed in dialog
            iv.setImageBitmap(omrSheet.getBmpOMRSheet());

            dialogOMRSheetDisplay.setCustomTitle(textView);
            dialogOMRSheetDisplay.setView(linearLayout);
            dialogOMRSheetDisplay.setNeutralButton("Go for next OMR", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dlg, int sumthin) {
                }
            });

            dialogOMRSheetDisplay.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mCameraView.requestPreviewFrame();
                }
            });

            dialogOMRSheetDisplay.show();
            Log.i(TAG,"DONE");
        }
    }
}