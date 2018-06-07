package view;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import model.Model;
import model.map.*;

public class MapCanvas extends JPanel implements ComponentListener {

    public MapCanvas(Model model, MapCreator gui) {
        this.model = model;
        this.gui = gui;
        this.coordinateMapper = CoordinateMapper.getInstance();
        
        this.addMouseMotionListener(gui);
        this.addMouseListener(gui);
        this.addMouseWheelListener(gui);
        this.addComponentListener(this);
    }
    
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        coordinateMapper.setCanvasSize(new Point(getSize().width, getSize().height));
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
    
    private class DrawableModel {
        
        private class Line {
            public int x1, y1, x2, y2;
        }
        
        Image levelImage;
        Point imageLocation;
        Point imageDimensions;

        List<Polygon> obstacles;
        List<Point> staircases;
        List<Polygon> routes;
        List<Point> POIs;
        List<Point> beacons;
        List<Polygon> elevators;
        
        Polygon currentObstacle;
        Polygon currentRoute;
        
        List<Line> grid;
    }
    
    public synchronized void prepareModel() {
        drawableModel = new DrawableModel();
        
        //Level image
        Level.ImageContainer levelImageContainer = model.getCurrentLevel().getImage();
        if(levelImageContainer != null) {
            Point firstPoint = coordinateMapper.mapCoordinates(levelImageContainer.firstCoordinate);
            
            if(coordinateMapper.getRotation() == 20) {
                System.out.println("dabeg");
            }
            
            Point xDim = coordinateMapper.scaleLength(
                new Point2d(Math.abs(levelImageContainer.lastCoordinate.x - levelImageContainer.firstCoordinate.x), 0));
            
            Point yDim = coordinateMapper.scaleLength(
                new Point2d(0, Math.abs(levelImageContainer.lastCoordinate.y - levelImageContainer.firstCoordinate.y)));
            
            drawableModel.imageDimensions = new Point(xDim.x, yDim.y);
                    
            drawableModel.levelImage = levelImageContainer.image.getScaledInstance(
                    drawableModel.imageDimensions.x,
                    drawableModel.imageDimensions.y,
                    Image.SCALE_SMOOTH);
            drawableModel.imageLocation = coordinateMapper.mapCoordinatesNoRotation(levelImageContainer.firstCoordinate);;
        }
        
        //Obstacles
        drawableModel.obstacles = new LinkedList<>();
        for(Obstacle obstacle : model.getCurrentLevel().getObstacles()) {
            Point2d[] points = obstacle.getPoints();
            int n = points.length;
            int[] xpoints = new int[n];
            int[] ypoints = new int[n];
            for(int i = 0; i < n; i++) {
                xpoints[i] = coordinateMapper.mapCoordinates(points[i]).x;
                ypoints[i] =  coordinateMapper.mapCoordinates(points[i]).y;
            }
            
            drawableModel.obstacles.add(new Polygon(xpoints, ypoints, n));
        }
        
        //Staircases
        drawableModel.staircases = new LinkedList<>();
        for(Staircase staircase : model.getCurrentLevel().getStaircases()) {
            drawableModel.staircases.add(coordinateMapper.mapCoordinates(staircase.getLocation()));
        }
        
        //Routes
        drawableModel.routes = new LinkedList<>();
        for(Route currentRoute : model.getMap().getRoutes()) {
            LinkedList<Integer> xPoints = new LinkedList<>();
            LinkedList<Integer> yPoints = new LinkedList<>();
            for(Checkpoint cur : currentRoute.getCheckpoints()) {
                if(cur.getLevel() == model.getCurrentLevel()) {
                    xPoints.add(coordinateMapper.mapCoordinates(cur.getLocation()).x);
                    yPoints.add(coordinateMapper.mapCoordinates(cur.getLocation()).y);
                }
            }
            
            int n = xPoints.size();
            int[] xpoints = new int[n];
            int[] ypoints = new int[n];
            for(int i = 0; i < n; i++) {
                xpoints[i] = xPoints.get(i);
                ypoints[i] = yPoints.get(i);
            }
            drawableModel.routes.add(new Polygon(xpoints, ypoints, n));
        }
        
        //Points of interest
        drawableModel.POIs = new LinkedList<>();
        for(PointOfInterest poi : model.getCurrentLevel().getPointsOfInterest()) {
            drawableModel.POIs.add(coordinateMapper.mapCoordinates(poi.getLocation()));
        }
        
        //Bluetooth beacons
        drawableModel.beacons = new LinkedList<>();
        for(BluetoothBeacon beacon : model.getCurrentLevel().getBluetoothBeacons()) {
            drawableModel.beacons.add(coordinateMapper.mapCoordinates(new Point2d(beacon.getLocation().getX(), beacon.getLocation().getY())));
        }
        
        //Elevators
        drawableModel.elevators = new LinkedList<>();
        for(Elevator elevator : model.getElevators()) {
            if(elevator.getLevels().contains(model.getCurrentLevel())) {
                Point elevatorPosition = coordinateMapper.mapCoordinates(elevator.getLocation());
                
                int[] xpoints = {elevatorPosition.x - 8, elevatorPosition.x, elevatorPosition.x + 8, elevatorPosition.x};
                int[] ypoints = {elevatorPosition.y, elevatorPosition.y - 8, elevatorPosition.y, elevatorPosition.y + 8};
                drawableModel.elevators.add(new Polygon(xpoints, ypoints, 4));
            }
        }
        
        //Current obstacle
        Obstacle currentObstacle = model.getNewObstacle();
        if(currentObstacle != null) {

            Point2d[] points = currentObstacle.getPoints();
            int n = points.length;
            //+ 1 (the point where the mouse is)
            int[] xpoints = new int[n + 1];
            int[] ypoints = new int[n + 1];
            for(int i = 0; i < n; i++) {
                xpoints[i] = coordinateMapper.mapCoordinates(points[i]).x;
                ypoints[i] = coordinateMapper.mapCoordinates(points[i]).y;
            }
            Point2d p = model.getTempObstaclePoint();
            if(p != null) {
                xpoints[n] = coordinateMapper.mapCoordinates(p).x;
                ypoints[n] = coordinateMapper.mapCoordinates(p).y;
                drawableModel.currentObstacle = new Polygon(xpoints, ypoints, n + 1);
            } else {
                drawableModel.currentObstacle = new Polygon(xpoints, ypoints, n);
            }
        }
        
        //Current route
        Route currentRoute = model.getNewRoute();
        if(currentRoute != null) {
            
            LinkedList<Integer> xPoints = new LinkedList<>();
            LinkedList<Integer> yPoints = new LinkedList<>();
            for(Checkpoint cur : currentRoute.getCheckpoints()) {
                if(cur.getLevel() == model.getCurrentLevel()) {
                    xPoints.add(coordinateMapper.mapCoordinates(cur.getLocation()).x);
                    yPoints.add(coordinateMapper.mapCoordinates(cur.getLocation()).y);
                }
            }
            
            int n = xPoints.size();
            int[] xpoints = new int[n + 1];
            int[] ypoints = new int[n + 1];
            for(int i = 0; i < n; i++) {
                xpoints[i] = xPoints.get(i);
                ypoints[i] = yPoints.get(i);
            }
            Point2d p = model.getTempRoutePoint();
            if(p != null) {
                xpoints[n] = coordinateMapper.mapCoordinates(p).x;
                ypoints[n] = coordinateMapper.mapCoordinates(p).y;
                drawableModel.currentRoute = new Polygon(xpoints, ypoints, n + 1);
            } else {
                drawableModel.currentRoute = new Polygon(xpoints, ypoints, n);
            }
        }
        
        //Grid
        drawableModel.grid = new LinkedList<>();
        Dimension size = getSize();
        if(size.width != 0 && size.height != 0) {
            Point2d[] circumscribedRectangleCoordinates = getCircumscribedRectangle(new Point(size.width, size.height));
            Point2d firstCoordinate = circumscribedRectangleCoordinates[0];
            Point2d lastCoordinate = circumscribedRectangleCoordinates[1];

            //Prepare coordinates to round numbers wider than canvas area

            if(firstCoordinate.getX() < lastCoordinate.getX()) {
                firstCoordinate.setX(Math.floor(firstCoordinate.getX()));
                lastCoordinate.setX(Math.ceil(lastCoordinate.getX()));
                for(int i = (int)firstCoordinate.getX(); i <= lastCoordinate.getX(); i++) {
                    DrawableModel.Line gridLine = drawableModel.new Line();
                    Point startPoint = coordinateMapper.mapCoordinates(new Point2d(i, firstCoordinate.getY()));
                    Point endPoint = coordinateMapper.mapCoordinates(new Point2d(i, lastCoordinate.getY()));
                    gridLine.x1 = startPoint.x;
                    gridLine.y1 = startPoint.y;
                    gridLine.x2 = endPoint.x;
                    gridLine.y2 = endPoint.y;
                    drawableModel.grid.add(gridLine);
                }
            } else {
                firstCoordinate.setX(Math.ceil(firstCoordinate.getX()));
                lastCoordinate.setX(Math.floor(lastCoordinate.getX()));
                for(int i = (int)firstCoordinate.getX(); i >= lastCoordinate.getX(); i--) {
                    DrawableModel.Line gridLine = drawableModel.new Line();
                    Point startPoint = coordinateMapper.mapCoordinates(new Point2d(i, firstCoordinate.getY()));
                    Point endPoint = coordinateMapper.mapCoordinates(new Point2d(i, lastCoordinate.getY()));
                    gridLine.x1 = startPoint.x;
                    gridLine.y1 = startPoint.y;
                    gridLine.x2 = endPoint.x;
                    gridLine.y2 = endPoint.y;
                    drawableModel.grid.add(gridLine);
                }
            }

            if(firstCoordinate.getY() < lastCoordinate.getY()) {
                firstCoordinate.setY(Math.floor(firstCoordinate.getY()));
                lastCoordinate.setY(Math.ceil(lastCoordinate.getY()));
                for(int i = (int)firstCoordinate.getY(); i <= lastCoordinate.getY(); i++) {
                    DrawableModel.Line gridLine = drawableModel.new Line();
                    Point startPoint = coordinateMapper.mapCoordinates(new Point2d(firstCoordinate.getX(), i));
                    Point endPoint = coordinateMapper.mapCoordinates(new Point2d(lastCoordinate.getX(), i));
                    gridLine.x1 = startPoint.x;
                    gridLine.y1 = startPoint.y;
                    gridLine.x2 = endPoint.x;
                    gridLine.y2 = endPoint.y;
                    drawableModel.grid.add(gridLine);
                }
            } else {
                for(int i = (int)firstCoordinate.getY(); i >= lastCoordinate.getY(); i--) {
                    firstCoordinate.setY(Math.ceil(firstCoordinate.getY()));
                    lastCoordinate.setY(Math.floor(lastCoordinate.getY()));
                    DrawableModel.Line gridLine = drawableModel.new Line();
                    Point startPoint = coordinateMapper.mapCoordinates(new Point2d(firstCoordinate.getX(), i));
                    Point endPoint = coordinateMapper.mapCoordinates(new Point2d(lastCoordinate.getX(), i));
                    gridLine.x1 = startPoint.x;
                    gridLine.y1 = startPoint.y;
                    gridLine.x2 = endPoint.x;
                    gridLine.y2 = endPoint.y;
                    drawableModel.grid.add(gridLine);
                }
            }
        }
    }

    @Override
    public synchronized void paint(Graphics g) {
        super.paint(g);
        
        if(drawableModel != null) {
            //first draw level image (if there is one)
            if(drawableModel.levelImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                Dimension canvasSize = getSize();
                g2d.rotate(
                        Math.toRadians(coordinateMapper.getRotation()),
                        canvasSize.width / 2,
                        canvasSize.height / 2);
                g2d.drawImage(
                        drawableModel.levelImage,
                        drawableModel.imageLocation.x,
                        drawableModel.imageLocation.y,
//                        drawableModel.imageDimensions.x,
//                        drawableModel.imageDimensions.y,
                        this);
            }
            
            //Grid
            g.setColor(Color.LIGHT_GRAY);
            drawableModel.grid.forEach((gridLine) -> {
                g.drawLine(gridLine.x1, gridLine.y1, gridLine.x2, gridLine.y2);
            });

            //Drawing obstacles
            g.setColor(Color.BLUE);
            drawableModel.obstacles.forEach((obstacle) -> {
                g.fillPolygon(obstacle);
            });

            //Drawing staircases (maybe temporary)
            g.setColor(Color.RED);
            drawableModel.staircases.forEach((stairLocation) -> {
                g.fillRoundRect(stairLocation.x, stairLocation.y, 8, 8, 0, 0);
            });

            //Drawing routes
            g.setColor(Color.GREEN);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawableModel.routes.forEach((currentRoute) -> {
                g2d.drawPolyline(currentRoute.xpoints, currentRoute.ypoints, currentRoute.npoints);
            });

            //Drawing points of interest (orange dot)
            g.setColor(Color.ORANGE);
            drawableModel.POIs.forEach((poiPosition) -> {
                g.fillOval(poiPosition.x, poiPosition.y, 8, 8);
            });

            //Drawing bluetooth beacons (light blue dot)
            g.setColor(Color.CYAN);
            drawableModel.beacons.forEach((beaconPosition) -> {
                g.fillOval(beaconPosition.x, beaconPosition.y, 8, 8);
            });

            //Drawing elevators
            g.setColor(Color.RED);
            drawableModel.elevators.forEach((elevatorPosition) -> {
               g.fillPolygon(elevatorPosition);
            });

            //Drawing current obstacle(if being made)
            g.setColor(Color.DARK_GRAY);

            if(drawableModel.currentObstacle != null) {
                g.drawPolyline(drawableModel.currentObstacle.xpoints, drawableModel.currentObstacle.ypoints, drawableModel.currentObstacle.npoints);
            }

            //Drawing current route (if being made)
            g.setColor(Color.DARK_GRAY);
            if(drawableModel.currentRoute != null) {
                g.drawPolyline(drawableModel.currentRoute.xpoints, drawableModel.currentRoute.ypoints, drawableModel.currentRoute.npoints);
            }
        }
        //Drawing compass
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(20, 20, 30, 30);
        int[] xpoints = {32, 38, 35};
        int[] ypoints = {35, 35, 20};
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(
                Math.toRadians(coordinateMapper.getRotation()),
                35, 35);
        
        g2d.setColor(Color.RED);
        g2d.fillPolygon(xpoints, ypoints, 3);
        
    }
    
    private Point2d getCircumscribedPoint(Point2d a, Point2d b, Point2d furtherPoint) {
        double theta = Math.toRadians(coordinateMapper.getRotation() % 90);
        if(theta == 0) {
            return a;
        }
        if(theta > 0) {
            theta = Math.toRadians(90) - theta;
        }
        if(coordinateMapper.getRotation() == -45 || coordinateMapper.getRotation() == 135) {
                theta = Math.toRadians(-44);    // ;)
        }
        double tanT = Math.tan(theta);
        
        
        double mc;
        double mb;
        double xa = a.getX();
        double ya = a.getY();
        double xb = b.getX();
        double yb = b.getY();
        
        //Case I
        mc = (yb - ya + (xa * tanT) - (xb * tanT)) / (xb - xa - (ya * tanT) + (yb * tanT));
        mb = -1 / mc;
        Point2d candidate1 = new Point2d();
        candidate1.x = (yb - ya + (mc * xa) - (mb * xb)) / (mc - mb);
        candidate1.y = (mb * candidate1.x) - (mb * xb) + yb;
        
        //Case II
        mc = (yb - ya - (xa * tanT) + (xb * tanT)) / (xb - xa + (ya * tanT) - (yb * tanT));
        mb = -1 / mc;
        Point2d candidate2 = new Point2d();
        candidate2.x = (yb - ya + (mc * xa) - (mb * xb)) / (mc - mb);
        candidate2.y = (mb * candidate2.x) - (mb * xb) + yb;
        
        if(CoordinateMapper.pointDistance(furtherPoint, candidate1) < CoordinateMapper.pointDistance(furtherPoint, candidate2)) {
            return candidate2;
        } else {
            return candidate1;
        }
    }
    
    private Point2d[] getCircumscribedRectangle(Point canvasSize) {
        Point2d[] retVal = new Point2d[2];
        
        if(coordinateMapper.getRotation() % 90 == 0) {
            retVal[0] = coordinateMapper.mapPoint(new Point(0, 0));
            retVal[1] = coordinateMapper.mapPoint(canvasSize);
            return retVal;
        }
        
        Point2d a = coordinateMapper.mapPoint(new Point(0, 0));
        Point2d b = coordinateMapper.mapPoint(new Point(canvasSize.x , 0));
        Point2d furtherPoint = coordinateMapper.mapPoint(canvasSize);
        
        retVal[0] = getCircumscribedPoint(a, b, furtherPoint);
        
        
        a = coordinateMapper.mapPoint(canvasSize);
        b = coordinateMapper.mapPoint(new Point(0, canvasSize.y));
        furtherPoint = coordinateMapper.mapPoint(new Point(0, 0));
        
        retVal[1] = getCircumscribedPoint(a, b, furtherPoint);
       
        
        return retVal;
    }
    
    private boolean almostEqual(double a, double b, double eps){
        return Math.abs(a-b)<eps;
    }

    private DrawableModel drawableModel;
    private final MapCreator gui;
    private Model model;
    private CoordinateMapper coordinateMapper;
}