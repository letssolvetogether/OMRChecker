package com.letssolvetogether.omr.object;

import com.google.android.cameraview.CameraView;
import com.letssolvetogether.omr.ui.customviews.CustomView;

public class CameraCustomView {
    CameraView mCameraView;
    CustomView customView;

    public CameraView getCameraView() {
        return mCameraView;
    }

    public void setCameraView(CameraView mCameraView) {
        this.mCameraView = mCameraView;
    }

    public CustomView getCustomView() {
        return customView;
    }

    public void setCustomView(CustomView customView) {
        this.customView = customView;
    }
}
