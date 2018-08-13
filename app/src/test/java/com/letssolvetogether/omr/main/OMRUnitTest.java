package com.letssolvetogether.omr.main;

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
    Mat matOMR;
    OMRTestActivity omrTestActivity;

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
        omrSheet.setCorrectAnswers(new int[]{1,0,3,0,2,4,0,0,0,0,5,3,0,0,0,0,0,0,0,5});

        for(int i=1;i<=3;i++){
            matOMR = Imgcodecs.imread("testimages/omr_"+i+".jpg");
            omrSheet.setMatOMRSheet(matOMR);
            omrSheet.setWidth(matOMR.cols());
            omrSheet.setHeight(matOMR.rows());
            omrSheet.setOmrSheetBlock();
            int score = new EvaluationUtil(omrSheet).getScore();
            assertEquals(score,7);
        }
    }
}