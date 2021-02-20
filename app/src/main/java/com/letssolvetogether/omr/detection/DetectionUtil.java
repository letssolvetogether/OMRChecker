package com.letssolvetogether.omr.detection;

import android.util.Log;

import com.letssolvetogether.omr.OMRSheetConstants;
import com.letssolvetogether.omr.exceptions.UnsupportedCameraResolutionException;
import com.letssolvetogether.omr.object.Circle;
import com.letssolvetogether.omr.object.OMRSheet;
import com.letssolvetogether.omr.object.OMRSheetCorners;
import com.letssolvetogether.omr.utils.OMRSheetUtil;
import com.letssolvetogether.omr.utils.PrereqChecks;
import com.letssolvetogether.omr.utils.StorageUtils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetectionUtil {

    private static String TAG="DetectionUtil";

    private int optionsPerQuestions;
    private int numberOfQuestions;
    private int questionsPerBlock;

    private byte[][] studentAnswers;

    private OMRSheetCorners omrSheetCorners;
    private OMRSheet omrSheet;

    private int thresholdOfNoOfBlackPixels;
    private int widthGaussian = 5, heightGaussian = 5;
    private int widthKSize, heightKSize;

    private int gaussianBlur[] = {5, 5, 7, 7, 7, 15, 15};
    private int structuringElement[] = {8, 8, 12, 16, 16, 26, 26};
    private int noOfBlackPixels[] = {50, 50, 50, 75, 100, 100, 150};
    private int resolutionWidth[] = {720, 960, 1080, 1536, 1920, 2448, 3120};

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

        Imgproc.GaussianBlur(matOMR, matGaussianBlur, new org.opencv.core.Size(widthGaussian, heightGaussian), 3, 2.5);

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

        minRadiusCornerCircle = matOMR.cols() / 52;
        maxRadiusCornerCircle = matOMR.cols() / 30;

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

        getCircleCentersInOrder(circle, omrSheetCorners, matOMR.cols(), matOMR.rows());

        if(omrSheetCorners.getTopLeftCorner() == null || omrSheetCorners.getTopRightCorner() == null || omrSheetCorners.getBottomLeftCorner() == null || omrSheetCorners.getBottomRightCorner() == null)
            omrSheetCorners = null;

        return omrSheetCorners;
    }

    private void getCircleCentersInOrder(Circle mCircles[], OMRSheetCorners omrSheetCorners, int w, int h) {
        double cx, cy;

        w = w/2;
        h = h/2;

        for(int i=0; i< mCircles.length; i++){
            cx = mCircles[i].getCx();
            cy = mCircles[i].getCy();

            if(cy <= h){
                if(cx <= w){
                    omrSheetCorners.setTopLeftCorner(new Point(cx,cy));
                }else{
                    omrSheetCorners.setTopRightCorner(new Point(cx,cy));
                }
            }else{
                if(cx <= w){
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

    public byte[][] getStudentAnswers(Mat matOMR) throws UnsupportedCameraResolutionException{

        int index = Arrays.binarySearch(resolutionWidth,matOMR.cols());
        if(index < 0) {
            throw new UnsupportedCameraResolutionException();
        }

        thresholdOfNoOfBlackPixels = noOfBlackPixels[index];
        widthKSize = heightKSize = structuringElement[index];
        widthGaussian = heightGaussian = gaussianBlur[index];

        Mat matGaussianBlur = new Mat();
        Imgproc.GaussianBlur(matOMR, matGaussianBlur, new Size(widthGaussian, heightGaussian), 3, 2.5);

        //convert to gray
        Mat matGray = new Mat();
        Imgproc.cvtColor(matGaussianBlur, matGray, Imgproc.COLOR_RGB2GRAY);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(widthKSize, heightKSize));
        Imgproc.morphologyEx(matGray, matGray, Imgproc.MORPH_CLOSE,kernel);

        double median = new PrereqChecks().brightness(matGray);

        Mat matThresholded = new Mat();
        int thresholdValue;

        thresholdValue = (int)median/2;

        if(median >= 130)
            thresholdValue += (median/10 - 12) * 5;


        System.out.println("Median: "+median);

        Core.inRange(matGray,new Scalar(thresholdValue, thresholdValue, thresholdValue), new Scalar(255,255,255), matThresholded);

        studentAnswers = new byte[numberOfQuestions][optionsPerQuestions];
        for(int k = 0; k < omrSheet.getNumberOfBlocks(); k++) {
            for(int i = 0; i< questionsPerBlock; i++) {
                for (int j = 0; j < optionsPerQuestions; j++) {
                    Point pt[] = new OMRSheetUtil(omrSheet).getRectangleCoordinates(k,i,j);
                    Point leftTopRectPoint = pt[0];
                    Point rightBottomRectPoint = pt[1];

                    //Imgproc.rectangle(matOMR,leftTopRectPoint,rightBottomRectPoint,new Scalar(255,0,0));
                    Rect rect = new Rect(leftTopRectPoint, rightBottomRectPoint);
                    int noOfWhitePixels = Core.countNonZero(matThresholded.submat(rect));
                    int totalPixels = omrSheet.getTotalPixelsInBoundingSquare();
                    int noOfBlackPixels = totalPixels - noOfWhitePixels;
                    Log.d("noOfBlackPixels", "i= " + (i +1) + " j= " + (j+1) + " " + noOfBlackPixels);
                    //NOT FILLED
                    if(noOfWhitePixels != totalPixels){
                        if (noOfBlackPixels >= omrSheet.getRequiredBlackPixelsInBoundingSquare()) {
                            //FULL
                            studentAnswers[i + (questionsPerBlock * k)][j] = OMRSheetConstants.CIRCLE_FILLED_FULL;
                            System.out.println("noOfBlackPixels - " + "i= " + (i +1) + " j= " + (j+1) + " " + noOfBlackPixels);
                        }else if(noOfBlackPixels >= thresholdOfNoOfBlackPixels){
                            //SEMI
                            studentAnswers[i + (questionsPerBlock * k)][j] = OMRSheetConstants.CIRCLE_FILLED_SEMI;
                            System.out.println("noOfBlackPixels - " + "i= " + (i +1) + " j= " + (j+1) + " " + noOfBlackPixels);
                        }
                    }
                }
            }
        }

        //just for testing purpose
        StorageUtils.storeImageForJUnitTest(matOMR,"omr_rgb");
        StorageUtils.storeImageForJUnitTest(matGray,"omr_gray");
        StorageUtils.storeImageForJUnitTest(matThresholded,"omr_threshold");
//        StorageUtils.storeImageOnDevice(matOMR,"omr_orig");
//        StorageUtils.storeImageOnDevice(matGray,"omr_gray");
//        StorageUtils.storeImageOnDevice(matThresholded,"omr_thrshold");
        return studentAnswers;
    }
}