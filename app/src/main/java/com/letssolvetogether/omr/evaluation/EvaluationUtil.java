package com.letssolvetogether.omr.evaluation;

import android.graphics.Bitmap;
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

    public String getScore(OMRSheet omrSheet){
        getStudentAnswers(omrSheet);
        /*int score = calculateScore(studentAnswers, correctAnswers);
        return score;*/
        String score = calculateScore(studentAnswers, correctAnswers);
        return score;
    }

    public String calculateScore(boolean[][] studentAnswers, int[] correctAnswers){
        int score = 0;
        String answer = "";
        for(int i=0; i < numberOfQuestions; i++){
            for(int j=0; j < optionsPerQuestions; j++){
                if(studentAnswers[i][j] == true){
                    answer += i+","+j+"\n";
                }
            }
        }
        return answer;
        //return score;
    }

    private void getStudentAnswers(OMRSheet omrSheet){

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

        Mat mat = new Mat();
        Utils.bitmapToMat(omrSheet.getBmpOMRSheet(), mat);

        Mat matGaussianBlur = new Mat();
        Imgproc.GaussianBlur(mat, matGaussianBlur, new Size(5,5), 3, 2.5);

        //convert to gray
        Mat matGray = new Mat();
        Imgproc.cvtColor(matGaussianBlur, matGray, Imgproc.COLOR_RGB2GRAY);

        double median = getMedian(matGray);

        Mat matThresholded = new Mat();
        int thresholdValue;

        if(median <= 130)
            thresholdValue = (int)median/2;
        else
            thresholdValue = 80;

        Core.inRange(matGray,new Scalar(thresholdValue, thresholdValue, thresholdValue), new Scalar(255,255,255), matThresholded);

        Point ptrect = new Point();

        studentAnswers = new boolean[omrSheet.getNumberOfQuestions()][optionsPerQuestions];
        for(int k = 0; k < omrSheet.getNumberOfBlocks(); k++) {
            for(int i = 0; i< omrSheet.getQuestionsPerBlock(); i++) {
                for (int j = 0; j < optionsPerQuestions; j++) {
                    ptrect.x = xFirstBlockOffset + (k * blockWidth) + (k * xDistanceBetweenBlock) + (j * xDistanceBetweenCircles);
                    ptrect.y = yFirstBlockOffset + (k * yDistanceBetweenBlock) + (i * yDistanceBetweenCircles) + (yDistanceBetweenRows * i);
                    int c = omrSheet.getWidthOfBoundingSquareForCircle();
                    c /= 2;
                    Point pt1 = new Point(ptrect.x - c, ptrect.y - c);
                    Point pt2 = new Point(ptrect.x + c, ptrect.y + c);
                    Imgproc.rectangle(mat, pt1, pt2, new Scalar(255, 0, 0));

                    Rect rect = new Rect(pt1, pt2);
                    int nonZeroCount = Core.countNonZero(matThresholded.submat(rect));
                    Log.d("nonzero", "i= " + (i +1) + " j= " + (j+1) + " " + nonZeroCount);
                    if (nonZeroCount <= omrSheet.getNumberOfFilledPixelsInBoundingSquare()) {
                        studentAnswers[i + (questionsPerBlock * k)][j] = true;
                        Imgproc.rectangle(mat, pt1, pt2, new Scalar(255, 0, 0),5);
                    }
                }
            }
        }
        //just for testing purpose
        storeImage(mat);
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

    public void storeImage(Mat img){
        Bitmap bmp = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img, bmp);
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