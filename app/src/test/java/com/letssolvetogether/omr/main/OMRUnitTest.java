package com.letssolvetogether.omr.main;

import android.support.constraint.BuildConfig;

import com.letssolvetogether.omr.detection.DetectionUtil;
import com.letssolvetogether.omr.evaluation.EvaluationUtil;
import com.letssolvetogether.omr.object.OMRSheet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OMRUnitTest {
    Mat roiOfOMR;
    OMRTestActivity omrTestActivity;
    byte[][] studentAnswers;
    int[] correctAnswers;

    @Before
    public void initOpenCV() {
        System.loadLibrary("opencv_java342");
        omrTestActivity = Robolectric.setupActivity(OMRTestActivity.class);
    }

    @Test
    public void testOMRScore(){
        //OMRTestActivity is not added in manifest file
        OMRSheet omrSheet = new OMRSheet();
        omrSheet.setNumberOfQuestions(20);
        omrSheet.setOmrSheetBlock();
        correctAnswers = new int[]{1,0,3,0,2,4,0,0,0,0,5,3,0,0,0,0,0,0,0,5};

        for(int i=1;i<=2;i++){
            System.out.println("i = "+i);
            roiOfOMR = Imgcodecs.imread("testimages/omr_"+i+".jpg");
            omrSheet.setMatOMRSheet(roiOfOMR);
            omrSheet.setWidth(roiOfOMR.cols());
            omrSheet.setHeight(roiOfOMR.rows());
            omrSheet.setOmrSheetBlock();

            studentAnswers = new DetectionUtil(omrSheet).getStudentAnswers(roiOfOMR);
            int score = new EvaluationUtil(omrSheet).calculateScore(studentAnswers,correctAnswers);
            assertEquals(score,7);
        }
    }

    @Test
    public void testSingleOMRScore1(){
        //OMRTestActivity is not added in manifest file
        OMRSheet omrSheet = new OMRSheet();
        omrSheet.setNumberOfQuestions(20);
        omrSheet.setOmrSheetBlock();
        correctAnswers = new int[]{1,2,3,4,2,4,2,5,1,0,5,3,5,3,2,0,0,4,0,5};

        roiOfOMR = Imgcodecs.imread("testimages/omr_orig.jpg");
        omrSheet.setMatOMRSheet(roiOfOMR);
        omrSheet.setWidth(roiOfOMR.cols());
        omrSheet.setHeight(roiOfOMR.rows());
        omrSheet.setOmrSheetBlock();

        studentAnswers = new DetectionUtil(omrSheet).getStudentAnswers(roiOfOMR);
        int score = new EvaluationUtil(omrSheet).calculateScore(studentAnswers,correctAnswers);
        assertEquals(score,14);
    }

    @Test
    public void testSingleOMRScore2(){
        //OMRTestActivity is not added in manifest file
        OMRSheet omrSheet = new OMRSheet();
        omrSheet.setNumberOfQuestions(20);
        omrSheet.setOmrSheetBlock();
        correctAnswers = new int[]{1,2,3,2,4,5,4,3,2,1,5,4,3,2,1,2,3,4,5,4};

        roiOfOMR = Imgcodecs.imread("testimages/omr_5.jpg");
        omrSheet.setMatOMRSheet(roiOfOMR);
        omrSheet.setWidth(roiOfOMR.cols());
        omrSheet.setHeight(roiOfOMR.rows());
        omrSheet.setOmrSheetBlock();

        studentAnswers = new DetectionUtil(omrSheet).getStudentAnswers(roiOfOMR);
        int score = new EvaluationUtil(omrSheet).calculateScore(studentAnswers,correctAnswers);
        assertEquals(score,19);
    }
}