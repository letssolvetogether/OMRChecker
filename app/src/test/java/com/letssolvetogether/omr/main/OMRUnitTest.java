package com.letssolvetogether.omr.main;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.core.Mat;
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
    OMRTestActivity OMRTestActivity;

    @Before
    public void initOpenCV() {
        System.loadLibrary("opencv_java342");
        OMRTestActivity = Robolectric.setupActivity(OMRTestActivity.class);
    }

    @Test
    public void test(){
        //OMRTestActivity is not added in manifest file
    }
}