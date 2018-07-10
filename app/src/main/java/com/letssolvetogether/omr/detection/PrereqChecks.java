package com.letssolvetogether.omr.detection;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgproc.Imgproc;

public class PrereqChecks {

    private final static int BLUR_VALUE = 110;

    public boolean isBlurry(Bitmap bmp){
        int sharpness = sharpness(bmp);
        Log.d("sharpness",String.valueOf(sharpness));

        if(sharpness <= BLUR_VALUE)
            return true;
        else
            return false;
    }

    public int sharpness(Bitmap bmp){

        Mat mat, matGray, matLaplacian;

        mat = new Mat();
        matLaplacian = new Mat();
        matGray=new Mat();

        Utils.bitmapToMat(bmp,mat);

        Imgproc.cvtColor(mat, matGray, Imgproc.COLOR_BGR2GRAY);

        Imgproc.Laplacian(matGray, matLaplacian, CvType.CV_16S);

        MatOfDouble mean, stddev;

        mean = new MatOfDouble();
        stddev = new MatOfDouble();

        Core.meanStdDev(matLaplacian, mean ,stddev);

        return (int) Math.pow(stddev.get(0,0)[0],2);
    }
}
