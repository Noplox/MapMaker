package model.map;

import java.io.Serializable;

public class Point2d implements Serializable {
    private double x, y;

    public Point2d(){}

    public Point2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
