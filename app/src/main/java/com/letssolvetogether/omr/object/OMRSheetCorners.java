package com.letssolvetogether.omr.object;

import org.opencv.core.Point;

public class OMRSheetCorners {
    //top corners
    private Point topLeftCorner;
    private Point topRightCorner;

    //bottom corners
    private Point bottomLeftCorner;
    private Point bottomRightCorner;

    public Point getTopLeftCorner() {
        return topLeftCorner;
    }

    public void setTopLeftCorner(Point topLeftCorner) {
        this.topLeftCorner = topLeftCorner;
    }

    public Point getTopRightCorner() {
        return topRightCorner;
    }

    public void setTopRightCorner(Point topRightCorner) {
        this.topRightCorner = topRightCorner;
    }

    public Point getBottomLeftCorner() {
        return bottomLeftCorner;
    }

    public void setBottomLeftCorner(Point bottomLeftCorner) {
        this.bottomLeftCorner = bottomLeftCorner;
    }

    public Point getBottomRightCorner() {
        return bottomRightCorner;
    }

    public void setBottomRightCorner(Point bottomRightCorner) {
        this.bottomRightCorner = bottomRightCorner;
    }
}
