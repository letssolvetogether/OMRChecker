package com.letssolvetogether.omr.object;

import androidx.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Mat;

public class OMRSheet extends ViewModel {

    private static String TAG="OMRSheet";

    private Bitmap bmpOMRSheet;
    private Mat matOMRSheet;

    private int width;
    private int height;

    private OMRSheetCorners omrSheetCorners;
    private OMRSheetBlock omrSheetBlock;

    private int numberOfQuestions;
    private int optionsPerQuestions = 5;
    private int questionsPerBlock = 10;
    private int widthOfBoundingSquareForCircle;

    private int requiredBlackPixelsInBoundingSquare;
    private int totalPixelsInBoundingSquare;

    private int[] correctAnswers;

    public OMRSheet() {}

    public int getWidth(){
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight(){
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getBmpOMRSheet() {
        return bmpOMRSheet;
    }

    public void setBmpOMRSheet(Bitmap bmpOMRSheet) {
        this.bmpOMRSheet = bmpOMRSheet;
    }

    public OMRSheetCorners getOmrSheetCorners() {
        return omrSheetCorners;
    }

    public void setOmrSheetCorners(OMRSheetCorners omrSheetCorners) {
        this.omrSheetCorners = omrSheetCorners;
    }

    public OMRSheetBlock getOmrSheetBlock() {
        return omrSheetBlock;
    }

    public void setOmrSheetBlock() {

        int w = getWidth();
        int h = getHeight();

        omrSheetBlock = new OMRSheetBlock();

        omrSheetBlock.setBlockWidth((int)(w/3.2));
        omrSheetBlock.setBlockHeight((int)(h/1.39));

        Log.i(TAG,"BlockWidth - " + (int)(w/3.2));
        Log.i(TAG,"BlockHeight - " + (int)(h/1.39));

        omrSheetBlock.setxFirstBlockOffset((int)(w/6.1));
        omrSheetBlock.setyFirstBlockOffset((int)(h/2.7));

        Log.i(TAG,"xFirstBlockOffset - " + (int)(w/6.1));
        Log.i(TAG,"yFirstBlockOffset - " + (int)(h/2.7));

        omrSheetBlock.setxDistanceBetweenBlock((int)(w/6.3));
        omrSheetBlock.setyDistanceBetweenBlock(0);

        Log.i(TAG,"xDistanceBetweenBlock - " + (int)(w/6.3));
        Log.i(TAG,"yDistanceBetweenBlock - " + 0);

        omrSheetBlock.setyDistanceBetweenRows((int)(h/53.0));

        Log.i(TAG,"yDistanceBetweenRows - " + (int)(h/53.0));

        omrSheetBlock.setxDistanceBetweenCircles((int)(w/14.77));
        omrSheetBlock.setyDistanceBetweenCircles((int)(h/26.6));

        Log.i(TAG,"xDistanceBetweenCircles - " + (int)(w/14.77));
        Log.i(TAG,"yDistanceBetweenCircles - " + (int)(h/26.6));
    }

    public Mat getMatOMRSheet() {
        return matOMRSheet;
    }

    public void setMatOMRSheet(Mat matOMRSheet) {
        this.matOMRSheet = matOMRSheet;
    }

    public int getNumberOfBlocks() {
        return numberOfQuestions/ questionsPerBlock;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getOptionsPerQuestions() {
        return optionsPerQuestions;
    }

    public int getWidthOfBoundingSquareForCircle() {
        widthOfBoundingSquareForCircle = (int)(getWidth()/17.0);
        //The width should be even otherwise we will get width difference of 1 in getTotalPixelsInBoundingSquare()
        // as we divide the width in getRectangleCoordinates()
        if(widthOfBoundingSquareForCircle % 2 != 0)
            widthOfBoundingSquareForCircle++;
        return widthOfBoundingSquareForCircle;
    }

    public int getQuestionsPerBlock() {
        return questionsPerBlock;
    }

    public int getTotalPixelsInBoundingSquare() {
        totalPixelsInBoundingSquare = getWidthOfBoundingSquareForCircle() * getWidthOfBoundingSquareForCircle();
        return totalPixelsInBoundingSquare;
    }

    public int getRequiredBlackPixelsInBoundingSquare() {
        requiredBlackPixelsInBoundingSquare = (int) (getTotalPixelsInBoundingSquare() * 0.22);
        return requiredBlackPixelsInBoundingSquare;
    }

    public int[] getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int[] correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}