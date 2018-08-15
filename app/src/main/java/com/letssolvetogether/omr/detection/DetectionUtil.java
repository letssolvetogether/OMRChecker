package com.letssolvetogether.omr.detection;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.letssolvetogether.omr.object.Circle;
import com.letssolvetogether.omr.object.OMRSheet;
import com.letssolvetogether.omr.object.OMRSheetCorners;
import com.letssolvetogether.omr.utils.OMRSheetUtil;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetectionUtil {

    private static String TAG="DetectionUtil";

    private int optionsPerQuestions;
    private int numberOfQuestions;
    private int questionsPerBlock;

    private boolean[][] studentAnswers;

    private OMRSheetCorners omrSheetCorners;
    private OMRSheet omrSheet;

    public DetectionUtil(OMRSheet omrSheet) {
        omrSheetCorners = new OMRSheetCorners();

        this.omrSheet = omrSheet;

        optionsPerQuestions = omrSheet.getOptionsPerQuestions();
        numberOfQuestions = omrSheet.getNumberOfQuestions();
        questionsPerBlock = omrSheet.getQuestionsPerBlock();
    }

    public OMRSheetCorners detectOMRSheetCorners(Mat matOMR) {

        Circle circle[];

        Mat matGaussianBlur = new Mat();

        Imgproc.GaussianBlur(matOMR, matGaussianBlur, new org.opencv.core.Size(5, 5), 3, 2.5);

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

        minRadiusCornerCircle = matOMR.cols() / 48;
        maxRadiusCornerCircle = matOMR.cols() / 32;

        Imgproc.HoughCircles(matGray, matCircles, Imgproc.CV_HOUGH_GRADIENT, 0.9, matOMR.cols()/2, 15, 30, minRadiusCornerCircle, maxRadiusCornerCircle);

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

    public Mat findROIofOMR(OMRSheet omrSheet){

        Point ptCornerPoints[];
        int previewWidth = omrSheet.getWidth();
        int previewHeight = omrSheet.getHeight();

        Mat mat = omrSheet.getMatOMRSheet();
        Mat outputMat = new Mat(previewWidth, previewHeight, CvType.CV_8UC4);

        List<Point> src = new ArrayList<>();

        ptCornerPoints = getCornerPoints(omrSheet.getOmrSheetCorners());

        if(ptCornerPoints == null)
            return null;

        for(int i=0; i< ptCornerPoints.length; i++){
            src.add(ptCornerPoints[i]);
        }

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

        return outputMat;
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

    public boolean[][] getStudentAnswers(Mat matOMR){

        Mat matGaussianBlur = new Mat();
        Imgproc.GaussianBlur(matOMR, matGaussianBlur, new Size(5,5), 3, 2.5);

        //convert to gray
        Mat matGray = new Mat();
        Imgproc.cvtColor(matGaussianBlur, matGray, Imgproc.COLOR_RGB2GRAY);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(8,8));
        Imgproc.morphologyEx(matGray, matGray, Imgproc.MORPH_CLOSE,kernel);

        double median = getMedian(matGray);

        Mat matThresholded = new Mat();
        int thresholdValue;

        if(median <= 130)
            thresholdValue = (int)median/2;
        else
            thresholdValue = 80;

        Core.inRange(matGray,new Scalar(thresholdValue, thresholdValue, thresholdValue), new Scalar(255,255,255), matThresholded);

        studentAnswers = new boolean[numberOfQuestions][optionsPerQuestions];
        for(int k = 0; k < omrSheet.getNumberOfBlocks(); k++) {
            for(int i = 0; i< questionsPerBlock; i++) {
                for (int j = 0; j < optionsPerQuestions; j++) {
                    Point pt[] = new OMRSheetUtil(omrSheet).getRectangleCoordinates(k,i,j);
                    Point leftTopRectPoint = pt[0];
                    Point rightBottomRectPoint = pt[1];

                    Rect rect = new Rect(leftTopRectPoint, rightBottomRectPoint);
                    int nonZeroCount = Core.countNonZero(matThresholded.submat(rect));
                    Log.d("nonzero", "i= " + (i +1) + " j= " + (j+1) + " " + nonZeroCount);
                    if (nonZeroCount <= omrSheet.getNumberOfFilledPixelsInBoundingSquare()) {
                        studentAnswers[i + (questionsPerBlock * k)][j] = true;
                    }
                }
            }
        }

        //just for testing purpose
//        storeImage(matOMR,"omr_orig");
//        storeImage(matGray,"omr_gray");
//        storeImage(matThresholded,"omr_thrshold");
        return studentAnswers;
    }

    private int getMedian(Mat mat) {

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

        Log.d(TAG, String.format("Median = %d", med));
        return med;
    }

    public void storeImage(Mat mat, String imageName){
        FileOutputStream out = null;
        File imageFile = new File(Environment.getExternalStorageDirectory(), imageName+".jpg");

        Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);

        try {
            if(!imageFile.exists()) {
                imageFile.createNewFile();
            }
            out = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}