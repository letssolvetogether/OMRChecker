package com.letssolvetogether.omr.detection;

import android.graphics.Bitmap;
import android.util.Log;

import com.letssolvetogether.omr.object.Circle;
import com.letssolvetogether.omr.object.OMRSheet;
import com.letssolvetogether.omr.object.OMRSheetCorners;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

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

        int minRadiusCornerCircle;
        int maxRadiusCornerCircle;

        minRadiusCornerCircle = bmpOMRSheet.getWidth() / 48;
        maxRadiusCornerCircle = bmpOMRSheet.getWidth() / 32;

        Imgproc.HoughCircles(matGray, matCircles, Imgproc.CV_HOUGH_GRADIENT, 0.9, bmpOMRSheet.getWidth()/2, 15, 30, minRadiusCornerCircle, maxRadiusCornerCircle);

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
                    omrSheetCorners.setBottomLeftCorner(new Point(cx,cy));
                }else{
                    omrSheetCorners.setBottomRightCorner(new Point(cx,cy));
                }
            }
        }
    }

    public Bitmap findROIofOMR(OMRSheet omrSheet){

        Point ptCornerPoints[];
        int previewWidth = omrSheet.getWidth();
        int previewHeight = omrSheet.getHeight();

        Mat mat = new Mat(previewWidth, previewHeight, CvType.CV_8UC4);
        Mat outputMat = new Mat(previewWidth, previewHeight, CvType.CV_8UC4);

        List<Point> src = new ArrayList<>();

        ptCornerPoints = getCornerPoints(omrSheet.getOmrSheetCorners());

        if(ptCornerPoints == null)
            return null;

        for(int i=0; i< ptCornerPoints.length; i++){
            src.add(ptCornerPoints[i]);
        }

        Utils.bitmapToMat(omrSheet.getBmpOMRSheet(), mat);

        Mat startM = Converters.vector_Point2f_to_Mat(src);

        List<Point> dest = new ArrayList<>();

        ptCornerPoints = getNewCornerPoints(previewWidth, previewHeight);

        if(ptCornerPoints == null)
            return null;

        for(int i=0; i< ptCornerPoints.length; i++){
            dest.add(ptCornerPoints[i]);
        }

        Mat endM = Converters.vector_Point2f_to_Mat(dest);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(mat, outputMat, perspectiveTransform, new Size(previewWidth, previewHeight));

        Utils.matToBitmap(outputMat,omrSheet.getBmpOMRSheet());
        return omrSheet.getBmpOMRSheet();
    }

    private Point[] getCornerPoints(OMRSheetCorners omrSheetCorners){

        Point pt[] = new Point[4];
        pt[0] = new Point(omrSheetCorners.getTopLeftCorner().x, omrSheetCorners.getTopLeftCorner().y);
        pt[1] = new Point(omrSheetCorners.getTopRightCorner().x, omrSheetCorners.getTopRightCorner().y);
        pt[2] = new Point(omrSheetCorners.getBottomRightCorner().x, omrSheetCorners.getBottomRightCorner().y);
        pt[3] = new Point(omrSheetCorners.getBottomLeftCorner().x, omrSheetCorners.getBottomLeftCorner().y);

        for(int i=0; i<4; i++){
            if(pt[i]==null)
                return null;
        }
        return pt;
    }

    private Point[] getNewCornerPoints(int pictureWidth, int pictureHeight){
        Point pt[] = new Point[4];
        pt[0] = new Point(0, 0);
        pt[1] = new Point(pictureWidth, 0);
        pt[2] = new Point(pictureWidth, pictureHeight);
        pt[3] = new Point(0, pictureHeight);

        for(int i=0; i<4; i++){
            if(pt[i]==null)
                return null;
        }
        return pt;
    }
}