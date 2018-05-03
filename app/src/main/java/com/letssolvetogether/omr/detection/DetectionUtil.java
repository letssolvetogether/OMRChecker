package com.letssolvetogether.omr.detection;

import android.graphics.Bitmap;
import android.util.Log;

import com.letssolvetogether.omr.object.Circle;
import com.letssolvetogether.omr.object.OMRSheetCorners;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class DetectionUtil {

    private static String TAG="DetectionUtil";
    private OMRSheetCorners omrSheetCorners;

    public DetectionUtil() {
        omrSheetCorners = new OMRSheetCorners();
    }

    public OMRSheetCorners detectOMRSheetCorners(Bitmap bmpOMRSheet) {

        Circle circle[];
        Mat img = new Mat();

        Utils.bitmapToMat(bmpOMRSheet, img);
        Mat matGaussianBlur = new Mat();

        Imgproc.GaussianBlur(img, matGaussianBlur, new org.opencv.core.Size(5, 5), 3, 2.5);

        //convert to gray
        Mat matGray = new Mat();
        Imgproc.cvtColor(matGaussianBlur, matGray, Imgproc.COLOR_RGB2GRAY);
        Mat matCircles = new Mat();

        //Mat matThresholded = new Mat();
        //Core.inRange(matGray,new Scalar(50,50,50), new Scalar(255,255,255), matThresholded);

        //Detect circles
        //Imgproc.HoughCircles(matGray, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 230, 50, 50, 15, 25);
        if(bmpOMRSheet.getHeight() == 1280 && bmpOMRSheet.getWidth() == 960)
            Imgproc.HoughCircles(matGray, matCircles, Imgproc.CV_HOUGH_GRADIENT, 0.9, 500, 15, 30, 20, 30);
        else if(bmpOMRSheet.getHeight() == 960 && bmpOMRSheet.getWidth() == 720)
            Imgproc.HoughCircles(matGray, matCircles, Imgproc.CV_HOUGH_GRADIENT, 0.9, 300, 15, 30, 10, 20);

        if(matCircles.cols() != 4){
            return null;
        }

        circle = new Circle[matCircles.cols()];
        for (int x = 0; x < matCircles.cols(); x++) {
            double vCircle[] = matCircles.get(0, x);

            if (vCircle == null)
                return null;

            //get center and radius
            Point pt = new Point(vCircle[0],vCircle[1]);
            int radius = (int) Math.round(vCircle[2]);
            Imgproc.circle(matGray, new Point(Math.round(vCircle[0]), Math.round(vCircle[1])), radius, new Scalar(255,0,0));
            circle[x] = new Circle(pt.x, pt.y, radius);
            Log.i("Detection",pt.x+","+pt.y+","+radius);
        }

        getCircleCentersInOrder(circle, omrSheetCorners);
        return omrSheetCorners;
    }

    private void getCircleCentersInOrder(Circle mCircles[], OMRSheetCorners omrSheetCorners) {
        double cx, cy;

        for(int i=0; i< mCircles.length; i++){
            cx = mCircles[i].getCx();
            cy = mCircles[i].getCy();

            if(cy <= 600){
                if(cx <= 400){
                    omrSheetCorners.setTopLeftCorner(new Point(cx,cy));
                }else{
                    omrSheetCorners.setTopRightCorner(new Point(cx,cy));
                }
            }else{
                if(cx <= 400){
                    omrSheetCorners.setBottomRightCorner(new Point(cx,cy));
                }else{
                    omrSheetCorners.setBottomLeftCorner(new Point(cx,cy));
                }
            }
        }
    }
}
