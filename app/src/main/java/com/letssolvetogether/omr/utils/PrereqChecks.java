package com.letssolvetogether.omr.utils;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class PrereqChecks {

    private final static int BLUR_VALUE = 110;
    private final static int BRIGHTNESS_VALUE = 60;

    public boolean isBlurry(Bitmap bmp){
        Mat mat = new Mat();
        Utils.bitmapToMat(bmp,mat);
        int sharpness = sharpness(mat);
        Log.d("sharpness",String.valueOf(sharpness));

        if(sharpness <= BLUR_VALUE)
            return true;
        else
            return false;
    }

    public boolean hasLowBrightness(Bitmap bmp){
        Mat mat = new Mat();
        Utils.bitmapToMat(bmp,mat);
        int brightness = brightness(mat);
        Log.d("brightness",String.valueOf(brightness));

        if(brightness <= BRIGHTNESS_VALUE)
            return true;
        else
            return false;
    }

    public int sharpness(Mat mat){

        Mat matGray, matLaplacian;

        matLaplacian = new Mat();
        matGray=new Mat();

        Imgproc.cvtColor(mat, matGray, Imgproc.COLOR_BGR2GRAY);

        Imgproc.Laplacian(matGray, matLaplacian, CvType.CV_16S);

        MatOfDouble mean, stddev;

        mean = new MatOfDouble();
        stddev = new MatOfDouble();

        Core.meanStdDev(matLaplacian, mean ,stddev);

        return (int) Math.pow(stddev.get(0,0)[0],2);
    }

    public int brightness(Mat mat){

        ArrayList<Mat> listOfMat = new ArrayList<>();
        listOfMat.add(mat);
        MatOfInt channels = new MatOfInt(0);

        Mat mask = new Mat();
        Mat hist = new Mat(256, 1, CvType.CV_8UC1);
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat ranges = new MatOfFloat(0, 256);

        Imgproc.calcHist(listOfMat, channels, mask, hist, histSize, ranges);

        double t = mat.rows() * mat.cols() / 2;
        double total = 0;
        int med = -1;
        for (int row = 0; row < hist.rows(); row++) {
            double val = hist.get(row, 0)[0];
            if ((total <= t) && (total + val >= t)) {
                med = row;
                break;
            }
            total += val;
        }
        return med;
    }
}
