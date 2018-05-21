package com.letssolvetogether.omr.utils;

public class OMRUtils {

    public static int[] strtointAnswers(String strAnswers){

        String[] splittedCorrectAnswers = strAnswers.split(",");

        int intAnswers[] = new int[20];
        for(int i=0; i<splittedCorrectAnswers.length; i++)
            intAnswers[i] = Integer.parseInt(splittedCorrectAnswers[i]);

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
