package com.letssolvetogether.omr.object;

import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.util.Log;

public class OMRSheet extends ViewModel {

    private static String TAG="OMRSheet";

    private Bitmap bmpOMRSheet;
    private OMRSheetCorners omrSheetCorners;
    private OMRSheetBlock omrSheetBlock;

    private int numberOfQuestions;
    private int optionsPerQuestions = 5;
    private int questionsPerBlock = 10;
    private int widthOfBoundingSquareForCircle;

    private int numberOfFilledPixelsInBoundingSquare;

    public OMRSheet(int numberOfQuestions) {
        omrSheetBlock = new OMRSheetBlock();
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getWidth(){
        return bmpOMRSheet.getWidth();
    }

    public int getHeight(){
        return bmpOMRSheet.getHeight();
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

        int w = bmpOMRSheet.getWidth();
        int h = bmpOMRSheet.getHeight();

        omrSheetBlock.setBlockWidth((int)(w/3.2));
        omrSheetBlock.setBlockHeight((int)(h/1.39));

        Log.i(TAG,"BlockWidth - " + (int)(w/3.2));
        Log.i(TAG,"BlockHeight - " + (int)(h/1.85));

        omrSheetBlock.setxFirstBlockOffset(w/8);
        omrSheetBlock.setyFirstBlockOffset((int)(h/3.2));

        Log.i(TAG,"xFirstBlockOffset - " + (w/8));
        Log.i(TAG,"yFirstBlockOffset - " + (int)(h/3.2));

        omrSheetBlock.setxDistanceBetweenBlock((int)(w/4.68));
        omrSheetBlock.setyDistanceBetweenBlock(0);

        Log.i(TAG,"xDistanceBetweenBlock - " + (int)(h/6.2));
        Log.i(TAG,"yDistanceBetweenBlock - " + 0);

        omrSheetBlock.setyDistanceBetweenRows((int)(h/55.65));

        Log.i(TAG,"yDistanceBetweenRows - " + (int)(h/55.65));

        omrSheetBlock.setxDistanceBetweenCircles((int)(w/14.77));
        omrSheetBlock.setyDistanceBetweenCircles((int)(h/26.6));

        Log.i(TAG,"xDistanceBetweenCircles - " + (int)(w/14.77));
        Log.i(TAG,"yDistanceBetweenCircles - " + (int)(h/26.6));
    }

    public int getNumberOfBlocks() {
        return numberOfQuestions/ questionsPerBlock;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public int getOptionsPerQuestions() {
        return optionsPerQuestions;
    }

    public int getWidthOfBoundingSquareForCircle() {
        widthOfBoundingSquareForCircle = (int)(getWidth()/18.46);
        return widthOfBoundingSquareForCircle;
    }

    public int getQuestionsPerBlock() {
        return questionsPerBlock;
    }

    public int getNumberOfFilledPixelsInBoundingSquare() {
        int totalPixelsInBoundingSquare = getWidthOfBoundingSquareForCircle() * getWidthOfBoundingSquareForCircle();
        numberOfFilledPixelsInBoundingSquare = (int) (totalPixelsInBoundingSquare * 0.8);
        return numberOfFilledPixelsInBoundingSquare;
    }
}