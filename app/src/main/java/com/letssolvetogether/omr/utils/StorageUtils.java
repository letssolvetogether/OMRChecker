/*
 * Copyright 2021 letssolvetogether
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.letssolvetogether.omr.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageUtils {

    public static void storeImageOnDevice(Mat mat, String imageName){
        FileOutputStream out = null;
        File imageFile = new File(Environment.getExternalStorageDirectory(), imageName+".jpg");

        Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);

        try {
            if(!imageFile.exists()) {
                imageFile.createNewFile();
            }
            out = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void storeImageForJUnitTest(Mat mat, String imageName){
        Imgcodecs.imwrite("testimages\\verification\\"+imageName+".jpg", mat);
    }
}
