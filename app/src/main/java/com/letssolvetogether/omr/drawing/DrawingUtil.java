package com.letssolvetogether.omr.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.letssolvetogether.omr.OMRSheetConstants;
import com.letssolvetogether.omr.object.OMRSheet;
import com.letssolvetogether.omr.utils.OMRSheetUtil;

import org.opencv.core.Point;

public class DrawingUtil {

    OMRSheet omrSheet;

    private int optionsPerQuestions;
    private int numberOfQuestions;
    private int questionsPerBlock;
    private int correctAnswers[];

    Paint greenPaint, redPaint;

    public DrawingUtil(OMRSheet omrSheet) {
        this.omrSheet = omrSheet;

        optionsPerQuestions = omrSheet.getOptionsPerQuestions();
        numberOfQuestions = omrSheet.getNumberOfQuestions();
        questionsPerBlock = omrSheet.getQuestionsPerBlock();
        correctAnswers = omrSheet.getCorrectAnswers();

        greenPaint = getPaint(Color.GREEN);
        redPaint = getPaint(Color.RED);
    }

    private Paint getPaint(int color){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(5);
        return paint;
    }

    public void drawRectangle(byte[][] studentAnswers){

        int answerPerQuestion;

        for(int i=0; i < numberOfQuestions; i++){
            answerPerQuestion =0;
            for(int j=0; j < optionsPerQuestions; j++){
                if(correctAnswers[i] != 0 && (studentAnswers[i][j] == OMRSheetConstants.CIRCLE_FILLED_FULL || studentAnswers[i][j] == OMRSheetConstants.CIRCLE_FILLED_SEMI)){
                    answerPerQuestion++;
                }
            }
            Canvas canvas = new Canvas(omrSheet.getBmpOMRSheet());

            Point pt[];
            Point leftTopRectPoint, rightBottomRectPoint;

            pt = new OMRSheetUtil(omrSheet).getRectangleCoordinates(i/questionsPerBlock,i%questionsPerBlock,correctAnswers[i]-1);

            leftTopRectPoint = pt[0];
            rightBottomRectPoint = pt[1];

            if(correctAnswers[i]!=0) {
                if (answerPerQuestion == 1 && studentAnswers[i][correctAnswers[i] - 1] == OMRSheetConstants.CIRCLE_FILLED_FULL) {
                    canvas.drawRect(new android.graphics.Rect((int) leftTopRectPoint.x, (int) leftTopRectPoint.y, (int) rightBottomRectPoint.x, (int) rightBottomRectPoint.y), greenPaint);
                } else {
                    for (int j = 0; j < optionsPerQuestions; j++) {

                        pt = new OMRSheetUtil(omrSheet).getRectangleCoordinates(i / questionsPerBlock, i % questionsPerBlock, j);

                        leftTopRectPoint = pt[0];
                        rightBottomRectPoint = pt[1];

                        if (studentAnswers[i][j] == OMRSheetConstants.CIRCLE_FILLED_SEMI || studentAnswers[i][j] == OMRSheetConstants.CIRCLE_FILLED_FULL)
                            canvas.drawRect(new android.graphics.Rect((int) leftTopRectPoint.x, (int) leftTopRectPoint.y, (int) rightBottomRectPoint.x, (int) rightBottomRectPoint.y), redPaint);
                    }
                }
            }
        }
    }
}