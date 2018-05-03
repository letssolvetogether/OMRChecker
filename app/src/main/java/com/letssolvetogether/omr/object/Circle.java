package com.letssolvetogether.omr.object;

public class Circle {
    private double cx;
    private double cy;
    private int radius;

    public Circle(double cx, double cy, int radius) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
    }

    public double getCx() {
        return cx;
    }

    public void setCx(double cx) {
        this.cx = cx;
    }

    public double getCy() {
        return cy;
    }

    public void setCy(double cy) {
        this.cy = cy;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
