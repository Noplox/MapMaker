package model.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that defines obstacles as a polygon lying on the plane of the floor it's on.
 * */
public class Obstacle implements Serializable {
    private List<Point2d> points;

    public Obstacle() {
        points = new ArrayList<>();
    }
    
    public void addPoint(Point2d p) {
        points.add(p);
    }
    
    public Point2d[] getPoints() {
        Point2d[] retVal = new Point2d[points.size()];
        retVal = points.toArray(retVal);
        return retVal;
    }
}
