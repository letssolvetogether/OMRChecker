package com.letssolvetogether.omr.evaluation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;

import com.letssolvetogether.omr.object.OMRSheet;
import com.letssolvetogether.omr.object.OMRSheetBlock;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EvaluationUtil {

    private static String TAG="EvaluationUtil";

    private int optionsPerQuestions;
    private int numberOfQuestions;
    private int questionsPerBlock;

    private int blockWidth;
    private int blockHeight;

    private int xFirstBlockOffset;
    private int yFirstBlockOffset;

    private int xDistanceBetweenCircles;
    private int yDistanceBetweenCircles;

    private int yDistanceBetweenRows;

    private int xDistanceBetweenBlock;
    private int yDistanceBetweenBlock;

    private boolean[][] studentAnswers;
    private int[] correctAnswers;

    private OMRSheet omrSheet;

    public EvaluationUtil(OMRSheet omrSheet) {
        this.omrSheet = omrSheet;

        OMRSheetBlock omrSheetBlock = omrSheet.getOmrSheetBlock();

        blockWidth = omrSheetBlock.getBlockWidth();
        blockHeight = omrSheetBlock.getBlockHeight();

        xFirstBlockOffset = omrSheetBlock.getxFirstBlockOffset();
        yFirstBlockOffset = omrSheetBlock.getyFirstBlockOffset();

        xDistanceBetweenBlock = omrSheetBlock.getxDistanceBetweenBlock();
        yDistanceBetweenBlock = omrSheetBlock.getyDistanceBetweenBlock();

        xDistanceBetweenCircles = omrSheetBlock.getxDistanceBetweenCircles();
        yDistanceBetweenCircles = omrSheetBlock.getyDistanceBetweenCircles();

        yDistanceBetweenRows = omrSheetBlock.getyDistanceBetweenRows();

        optionsPerQuestions = omrSheet.getOptionsPerQuestions();
        numberOfQuestions = omrSheet.getNumberOfQuestions();
        questionsPerBlock = omrSheet.getQuestionsPerBlock();
    }

    public int getScore(){
        getStudentAnswers(omrSheet.getMatOMRSheet());
        correctAnswers = omrSheet.getCorrectAnswers();
        int score = calculateScore(studentAnswers, correctAnswers);
        return score;
    }

    public int calculateScore(boolean[][] studentAnswers, int[] correctAnswers){
        int score = 0;
        int answerPerQuestion;

        for(int i=0; i < numberOfQuestions; i++){
            answerPerQuestion =0;
            for(int j=0; j < optionsPerQuestions; j++){
                if(correctAnswers[i] != 0 && studentAnswers[i][j]){
                    answerPerQuestion++;
                }
            }
            if(answerPerQuestion==1 && studentAnswers[i][correctAnswers[i]-1]){
                drawRectangle(i/questionsPerBlock,i%questionsPerBlock,correctAnswers[i]-1,Color.GREEN);
                score++;
            }
        }
        return score;
    }

    public void drawRectangle(int block, int questionNo, int option, int color){
        Canvas canvas = new Canvas(omrSheet.getBmpOMRSheet());

        Point pt[] = getRectangleCoordinates(block, questionNo, option);

        Point leftTopRectPoint = pt[0];
        Point rightBottomRectPoint = pt[1];

        Paint greenPaint = new Paint();
        greenPaint.setStyle(Paint.Style.STROKE);
        greenPaint.setColor(color);
        greenPaint.setStrokeWidth(5);

        canvas.drawRect(new android.graphics.Rect((int)leftTopRectPoint.x,(int)leftTopRectPoint.y,(int)rightBottomRectPoint.x,(int)rightBottomRectPoint.y),greenPaint);
    }

    private Point[] getRectangleCoordinates(int block, int questionNo, int option){
        Point ptrect = new Point();
        ptrect.x = xFirstBlockOffset + (block * blockWidth) + (block * xDistanceBetweenBlock) + (option * xDistanceBetweenCircles);
        ptrect.y = yFirstBlockOffset + (block * yDistanceBetweenBlock) + (questionNo * yDistanceBetweenCircles) + (yDistanceBetweenRows * questionNo);
        int c = omrSheet.getWidthOfBoundingSquareForCircle();
        c /= 2;

        Point pt[] = new Point[2];
        pt[0] = new Point(ptrect.x - c, ptrect.y - c);
        pt[1] = new Point(ptrect.x + c, ptrect.y + c);

        return pt;
    }

    public void getStudentAnswers(Mat matOMR){

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

        studentAnswers = new boolean[omrSheet.getNumberOfQuestions()][optionsPerQuestions];
        for(int k = 0; k < omrSheet.getNumberOfBlocks(); k++) {
            for(int i = 0; i< omrSheet.getQuestionsPerBlock(); i++) {
                for (int j = 0; j < optionsPerQuestions; j++) {
                    Point pt[] = getRectangleCoordinates(k,i,j);
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
        //storeImage(bmp);
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

    public void storeImage(Bitmap bmp){
        FileOutputStream out = null;
        File imageFile = new File(Environment.getExternalStorageDirectory(), "omr.jpg");
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