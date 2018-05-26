package view;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import model.Model;
import model.map.*;

public class MapCanvas extends JPanel {

    public MapCanvas(Model model, MapCreator gui) {
        this.model = model;
        this.gui = gui;
        this.coordinateMapper = CoordinateMapper.getInstance();
        
        this.addMouseMotionListener(gui);
        this.addMouseListener(gui);
        this.addMouseWheelListener(gui);
    }
    
    public void setModel(Model model) {
        this.model = model;
    }
    
    private class DrawableModel {
        Image levelImage;
        Point imageLocation;// = coordinateMapper.mapCoordinates(new Point2d(0.0d, 0.0d));

        List<Polygon> obstacles;
        List<Point> staircases;
        List<Polygon> routes;
        List<Point> POIs;
        List<Point> beacons;
        List<Polygon> elevators;
        
        Polygon currentObstacle;
        Polygon currentRoute;
    }
    
    public synchronized void prepareModel() {
        drawableModel = new DrawableModel();
        
        //Level image
        Level.ImageContainer levelImageContainer = model.getCurrentLevel().getImage();
        if(levelImageContainer != null) {
            Point imageDimensions = coordinateMapper.scaleLength(new Point2d(levelImageContainer.width, levelImageContainer.height));
            drawableModel.levelImage = levelImageContainer.image.getScaledInstance(imageDimensions.x, imageDimensions.y, Image.SCALE_SMOOTH);
            drawableModel.imageLocation = coordinateMapper.mapCoordinates(new Point2d(0.0d, 0.0d));
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
    }

    @Override
    public synchronized void paint(Graphics g) {
        super.paint(g);
        
        if(drawableModel != null) {
            //first draw level image (if there is one)
            if(drawableModel.levelImage != null) {
                g.drawImage(drawableModel.levelImage, drawableModel.imageLocation.x, drawableModel.imageLocation.y, null);
            }

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
    }

    private DrawableModel drawableModel;
    private final MapCreator gui;
    private Model model;
    private CoordinateMapper coordinateMapper;
}