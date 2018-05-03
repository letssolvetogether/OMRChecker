package com.letssolvetogether.omr.object;

import android.arch.lifecycle.ViewModel;

public class OMRSheet extends ViewModel {

    private OMRSheetCorners omrSheetCorners;

    public OMRSheetCorners getOmrSheetCorners() {
        return omrSheetCorners;
    }

    public void setOmrSheetCorners(OMRSheetCorners omrSheetCorners) {
        this.omrSheetCorners = omrSheetCorners;
    }
}
