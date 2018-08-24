package com.letssolvetogether.omr.evaluation;

import com.letssolvetogether.omr.OMRSheetConstants;
import com.letssolvetogether.omr.object.OMRSheet;

public class EvaluationUtil {

    private static String TAG="EvaluationUtil";

    private int optionsPerQuestions;
    private int numberOfQuestions;

    public EvaluationUtil(OMRSheet omrSheet) {

        optionsPerQuestions = omrSheet.getOptionsPerQuestions();
        numberOfQuestions = omrSheet.getNumberOfQuestions();
    }

    public int calculateScore(byte[][] studentAnswers, int[] correctAnswers){
        int score = 0;
        int answerPerQuestion;

        for(int i=0; i < numberOfQuestions; i++){
            answerPerQuestion =0;
            for(int j=0; j < optionsPerQuestions; j++){
                if(correctAnswers[i] != 0 && (studentAnswers[i][j] == OMRSheetConstants.CIRCLE_FILLED_FULL || studentAnswers[i][j] == OMRSheetConstants.CIRCLE_FILLED_SEMI)){
                    answerPerQuestion++;
                }
            }
            if(answerPerQuestion == 1 && studentAnswers[i][correctAnswers[i]-1] == OMRSheetConstants.CIRCLE_FILLED_FULL){
                score++;
            }
        }
        return score;
    }
}