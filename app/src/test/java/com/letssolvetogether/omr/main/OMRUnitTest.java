package com.letssolvetogether.omr.main;

import com.letssolvetogether.omr.detection.DetectionUtil;
import com.letssolvetogether.omr.evaluation.EvaluationUtil;
import com.letssolvetogether.omr.exceptions.UnsupportedCameraResolutionException;
import com.letssolvetogether.omr.object.OMRSheet;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class OMRUnitTest {
    Mat roiOfOMR;
    byte[][] studentAnswers;
    int[] correctAnswers;

    final private static String OPENCV_LIB_NAME = "opencv_java430";

    /**
     * You must have "opencv_java430.dll" file at app/opencv_java430.dll location
     * to run these testcases
     */
    @Before
    public void initOpenCV() {
        System.loadLibrary(OPENCV_LIB_NAME);
    }

    @Test
    public void testOMRScoreAll() throws UnsupportedCameraResolutionException {
        //OMRTestActivity is not added in manifest file
        OMRSheet omrSheet = new OMRSheet();
        omrSheet.setNumberOfQuestions(20);
        omrSheet.setOmrSheetBlock();
        int[][] correctAnswers = new int[][]{{1,0,3,0,2,4,0,0,0,0,5,3,0,0,0,0,0,0,0,5},
                                            {1,2,3,2,4,5,4,3,2,1,5,4,3,2,1,2,3,4,5,4}};

        String resolution[] = {"720p","960p","1080p","1536p","1920p","2448p","3120p"};
        int correctAnswerForSet[] = {7,19};

        int i,j,k;

        for(i=0; i<resolution.length; i++){ //no of resolutions
            for(j=0; j<getNumberOfFiles("testimages/"+resolution[i]); j++){ //no of set
                for(k=0; k<getNumberOfFiles("testimages/"+resolution[i]+"/set"+(j+1)); k++){ //no of images
                    System.out.println("==================================================");
                    System.out.println(resolution[i] +" - set"+(j+1)+" - omr_"+(k+1)+".jpg");
                    System.out.println("==================================================");
                    roiOfOMR = Imgcodecs.imread("testimages/"+resolution[i]+"/set"+(j+1)+"/omr_"+(k+1)+".jpg");
                    omrSheet.setMatOMRSheet(roiOfOMR);
                    omrSheet.setWidth(roiOfOMR.cols());
                    omrSheet.setHeight(roiOfOMR.rows());
                    omrSheet.setOmrSheetBlock();

                    studentAnswers = new DetectionUtil(omrSheet).getStudentAnswers(roiOfOMR);
                    int score = new EvaluationUtil(omrSheet).calculateScore(studentAnswers,correctAnswers[j]);
                    assertEquals(score,correctAnswerForSet[j]);
                }

            }
        }
    }

    @Test
    public void testSingleOMRScore() throws UnsupportedCameraResolutionException {
        //OMRTestActivity is not added in manifest file
        OMRSheet omrSheet = new OMRSheet();
        omrSheet.setNumberOfQuestions(20);
        omrSheet.setOmrSheetBlock();
        //correctAnswers = new int[]{1,0,3,0,2,4,0,0,0,0,5,3,0,0,0,0,0,0,0,5};
        correctAnswers = new int[]{1,2,3,2,4,5,4,3,2,1,5,4,3,2,1,2,3,4,5,4};

        roiOfOMR = Imgcodecs.imread("testimages/omr_single.jpg");
        omrSheet.setMatOMRSheet(roiOfOMR);
        omrSheet.setWidth(roiOfOMR.cols());
        omrSheet.setHeight(roiOfOMR.rows());
        omrSheet.setOmrSheetBlock();

        studentAnswers = new DetectionUtil(omrSheet).getStudentAnswers(roiOfOMR);
        int score = new EvaluationUtil(omrSheet).calculateScore(studentAnswers,correctAnswers);
        System.out.println("Score = "+score);
        //assertEquals(score,7);
        assertEquals(score,19);
    }

    public int getNumberOfFiles(String path){
        return new File(path).list().length;
    }
}