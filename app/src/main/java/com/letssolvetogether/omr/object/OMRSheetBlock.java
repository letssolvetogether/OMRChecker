package com.letssolvetogether.omr.object;

public class OMRSheetBlock {

    private int blockWidth;
    private int blockHeight;

    private int xFirstBlockOffset;
    private int yFirstBlockOffset;

    private int xDistanceBetweenCircles;
    private int yDistanceBetweenCircles;

    private int yDistanceBetweenRows;

    private int xDistanceBetweenBlock;
    private int yDistanceBetweenBlock;

    public int getBlockWidth() {
        return blockWidth;
    }

    public void setBlockWidth(int blockWidth) {
        this.blockWidth = blockWidth;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    public int getxFirstBlockOffset() {
        return xFirstBlockOffset;
    }

    public void setxFirstBlockOffset(int xFirstBlockOffset) {
        this.xFirstBlockOffset = xFirstBlockOffset;
    }

    public int getyFirstBlockOffset() {
        return yFirstBlockOffset;
    }

    public void setyFirstBlockOffset(int yFirstBlockOffset) {
        this.yFirstBlockOffset = yFirstBlockOffset;
    }

    public int getxDistanceBetweenCircles() {
        return xDistanceBetweenCircles;
    }

    public void setxDistanceBetweenCircles(int xDistanceBetweenCircles) {
        this.xDistanceBetweenCircles = xDistanceBetweenCircles;
    }

    public int getyDistanceBetweenCircles() {
        return yDistanceBetweenCircles;
    }

    public void setyDistanceBetweenCircles(int yDistanceBetweenCircles) {
        this.yDistanceBetweenCircles = yDistanceBetweenCircles;
    }

    public int getyDistanceBetweenRows() {
        return yDistanceBetweenRows;
    }

    public void setyDistanceBetweenRows(int yDistanceBetweenRows) {
        this.yDistanceBetweenRows = yDistanceBetweenRows;
    }

    public int getxDistanceBetweenBlock() {
        return xDistanceBetweenBlock;
    }

    public void setxDistanceBetweenBlock(int xDistanceBetweenBlock) {
        this.xDistanceBetweenBlock = xDistanceBetweenBlock;
    }

    public int getyDistanceBetweenBlock() {
        return yDistanceBetweenBlock;
    }

    public void setyDistanceBetweenBlock(int yDistanceBetweenBlock) {
        this.yDistanceBetweenBlock = yDistanceBetweenBlock;
    }
}