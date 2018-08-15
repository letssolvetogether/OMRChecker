package com.letssolvetogether.omr.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.letssolvetogether.omr.object.OMRSheet;
import com.letssolvetogether.omr.utils.OMRSheetUtil;

import org.opencv.core.Point;

public class DrawingUtil {

    OMRSheet omrSheet;

    private int optionsPerQuestions;
    private int numberOfQuestions;
    private int questionsPerBlock;
    private int correctAnswers[];


    public DrawingUtil(OMRSheet omrSheet) {
        this.omrSheet = omrSheet;

        optionsPerQuestions = omrSheet.getOptionsPerQuestions();
        numberOfQuestions = omrSheet.getNumberOfQuestions();
        questionsPerBlock = omrSheet.getQuestionsPerBlock();
        correctAnswers = omrSheet.getCorrectAnswers();
    }

    public void drawRectangle(boolean[][] studentAnswers){

        int answerPerQuestion;

        for(int i=0; i < numberOfQuestions; i++){
            answerPerQuestion =0;
            for(int j=0; j < optionsPerQuestions; j++){
                if(correctAnswers[i] != 0 && studentAnswers[i][j]){
                    answerPerQuestion++;
                }
            }
            if(answerPerQuestion==1 && studentAnswers[i][correctAnswers[i]-1]){
                Canvas canvas = new Canvas(omrSheet.getBmpOMRSheet());

                Point pt[] = new OMRSheetUtil(omrSheet).getRectangleCoordinates(i/questionsPerBlock,i%questionsPerBlock,correctAnswers[i]-1);

                Point leftTopRectPoint = pt[0];
                Point rightBottomRectPoint = pt[1];

                Paint greenPaint = new Paint();
                greenPaint.setStyle(Paint.Style.STROKE);
                greenPaint.setColor(Color.GREEN);
                greenPaint.setStrokeWidth(5);

                canvas.drawRect(new android.graphics.Rect((int)leftTopRectPoint.x,(int)leftTopRectPoint.y,(int)rightBottomRectPoint.x,(int)rightBottomRectPoint.y),greenPaint);
            }
        }
    }
}