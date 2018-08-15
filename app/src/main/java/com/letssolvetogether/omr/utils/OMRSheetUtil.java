package com.letssolvetogether.omr.utils;

import com.letssolvetogether.omr.object.OMRSheet;
import com.letssolvetogether.omr.object.OMRSheetBlock;

import org.opencv.core.Point;

public class OMRSheetUtil {

    OMRSheet omrSheet;

    private int blockWidth;
    private int blockHeight;

    private int xFirstBlockOffset;
    private int yFirstBlockOffset;

    private int xDistanceBetweenCircles;
    private int yDistanceBetweenCircles;

    private int yDistanceBetweenRows;

    private int xDistanceBetweenBlock;
    private int yDistanceBetweenBlock;

    public OMRSheetUtil(OMRSheet omrSheet) {
        this.omrSheet = omrSheet;

        OMRSheetBlock omrSheetBlock = omrSheet.getOmrSheetBlock();

        blockWidth = omrSheetBlock.getBlockWidth();
        blockHeight = omrSheetBlock.getBlockHeight();

        xFirstBlockOffset = omrSheetBlock.getxFirstBlockOffset();
        yFirstBlockOffset = omrSheetBlock.getyFirstBlockOffset();

        xDistanceBetweenBlock = omrSheetBlock.getxDistanceBetweenBlock();
        yDistanceBetweenBlock = omrSheetBlock.getyDistanceBetweenBlock();

        xDistanceBetweenCircles = omrSheetBlock.getxDistanceBetweenCircles();
        yDistanceBetweenCircles = omrSheetBlock.getyDistanceBetweenCircles();

        yDistanceBetweenRows = omrSheetBlock.getyDistanceBetweenRows();
    }

    public Point[] getRectangleCoordinates(int block, int questionNo, int option){
        Point ptrect = new Point();
        ptrect.x = xFirstBlockOffset + (block * blockWidth) + (block * xDistanceBetweenBlock) + (option * xDistanceBetweenCircles);
        ptrect.y = yFirstBlockOffset + (block * yDistanceBetweenBlock) + (questionNo * yDistanceBetweenCircles) + (yDistanceBetweenRows * questionNo);
        int c = omrSheet.getWidthOfBoundingSquareForCircle();
        c /= 2;

        Point pt[] = new Point[2];
        pt[0] = new Point(ptrect.x - c, ptrect.y - c);
        pt[1] = new Point(ptrect.x + c, ptrect.y + c);

        return pt;
    }
}
