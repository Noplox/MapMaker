package view;

import java.awt.Point;
import javafx.util.converter.PercentageStringConverter;
import model.map.Point2d;

public class CoordinateMapper {
    private static final double PIXEL_COORDINATE_RATIO = 100;  //100px = 1m
    
    private static CoordinateMapper instance = null;
    
    private double zoomPercent = 100;
    private final Point translation = new Point(0, 0);
    
    /**
     * Maps point on canvas to real model coordinates
     * @param point on canvas
     * @return The real position of the canvas point in the model
     */
    public Point2d mapPoint(Point point) {
        Point2d retVal = new Point2d();
        double zoomRatio = zoomPercent / 100;
        retVal.setX((point.x - translation.x) / (zoomRatio * PIXEL_COORDINATE_RATIO));
        retVal.setY((point.y - translation.y) / (zoomRatio * PIXEL_COORDINATE_RATIO));
        return retVal;
    }
    
    /**
     * Maps real model coordinates to screen pixels
     * @param coordinates the real coordinates of the object
     * @return Point on canvas
     */
    public Point mapCoordinates(Point2d coordinates) {
        Point retVal = new Point();
        double zoomRatio = zoomPercent / 100;
        retVal.x = (int) ((coordinates.getX() * zoomRatio * PIXEL_COORDINATE_RATIO) + translation.x);
        retVal.y = (int) ((coordinates.getY() * zoomRatio * PIXEL_COORDINATE_RATIO) + translation.y);
        return retVal;
    }
    
    public Point scaleLength(Point2d realLength) {
        Point retVal = new Point();
        double zoomRatio = zoomPercent / 100;
        retVal.x = (int) (realLength.getX() * zoomRatio * PIXEL_COORDINATE_RATIO);
        retVal.y = (int) (realLength.getY() * zoomRatio * PIXEL_COORDINATE_RATIO);
        return retVal;
    }

    void zoomOut(int percent) {
        zoomPercent -= percent;
        if(zoomPercent < 0) {
            zoomPercent = 1;
        }
    }

    void zoomIn(int percent) {
        zoomPercent += percent;
    }
    
    public void translate(Point translation) {
        this.translation.x += translation.x;
        this.translation.y += translation.y;
    }
    
    private CoordinateMapper() {}
    
    public static CoordinateMapper getInstance() {
        if (instance == null) {
            instance = new CoordinateMapper();
        }
        return instance;
    }
}
