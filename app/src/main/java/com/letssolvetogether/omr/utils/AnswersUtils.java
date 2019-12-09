package com.letssolvetogether.omr.utils;

public class AnswersUtils {

    public static int[] strtointAnswers(String strAnswers){

        int intAnswers[] = null;

        if(strAnswers!=null && !strAnswers.isEmpty()){
            String[] splittedCorrectAnswers = strAnswers.split(",");

            intAnswers = new int[splittedCorrectAnswers.length];
            for(int i=0; i<splittedCorrectAnswers.length; i++)
                intAnswers[i] = Integer.parseInt(splittedCorrectAnswers[i]);
        }

        return intAnswers;
    }

    public static String inttostrAnswers(int[] answers){

        String strAnswers = "";

        for(int i =0; i<answers.length; i++) {
            strAnswers += answers[i] + ",";
        }

        return strAnswers;
    }
}
