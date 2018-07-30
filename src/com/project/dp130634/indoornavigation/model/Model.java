package com.project.dp130634.indoornavigation.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.project.dp130634.indoornavigation.model.map.*;

public class Model {
    
    public Model() {
        newObstacle = null;
        tempObstaclePoint = new Point2d();
        newMap();
    }
    
    public void newMap() {
        map = new Map("<untitled>");
        currentLevel = new Level("Ground floor", 0.0d);
        map.addLevel(currentLevel);
    }

    public Map getMap() {
        return map;
    }

    public Obstacle getNewObstacle() {
        return newObstacle;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
    
    public List<Level> getAllLevels() {
        return map.getLevels();
    }
    
    public void addLevelImage(Image levelImage, Point2d firstCoordinate, Point2d lastCoordinate) {
        currentLevel.addImage(levelImage, firstCoordinate, lastCoordinate);
    }

    public void addLevel(String name, double height) {
        map.addLevel(new Level(name, height));
    }

    public void setCurrentLevel(Level level) {
        currentLevel = level;
    }
    
    public void addObstacle(Obstacle newObstacle) {
        currentLevel.addObstacle(newObstacle);
    }
    
    public void addObstaclePoint(Point2d obstaclePoint) {
        if(newObstacle == null) {
            newObstacle = new Obstacle();
        }
        newObstacle.addPoint(obstaclePoint);
    }

    public void finalizeObstacle(Point2d obstaclePoint) {
        if(newObstacle != null) {
            newObstacle.addPoint(obstaclePoint);
            addObstacle(newObstacle);
            newObstacle = null;
        }
    }
    
    public void setTempObstaclePoint(Point2d tempPoint) {
        tempObstaclePoint = tempPoint;
    }

    public Point2d getTempObstaclePoint() {
        return tempObstaclePoint;
    }

    public Point2d getTempRoutePoint() {
        return tempRoutePoint;
    }

    public void setTempRoutePoint(Point2d tempRoutePoint) {
        this.tempRoutePoint = tempRoutePoint;
    }
    
    public void addRoutePoint(Point2d clickPosition) {
        if(newRoute == null) {
            newRoute = new Route();
        }
        newRoute.addCheckpoint(new Checkpoint(currentLevel, clickPosition));
    }

    public void finalizeRoute(Point2d clickPosition, String name) {
        if(newRoute != null) {
            newRoute.addCheckpoint(new Checkpoint(currentLevel, clickPosition));
            newRoute.setName(name);
            map.addRoute(newRoute);
            newRoute = null;
        }
    }
    
    public void addPOI(Point2d clickPosition, String name, String contentURL) {
        currentLevel.addPointOfInterest(new PointOfInterest(clickPosition, name, contentURL));
    }
    
    public void addBeacon(Point2d clickPosition, UUID uuid, int major, int minor, double height) {
        Location beaconLocation = new Location(clickPosition.getX(), clickPosition.getY(), currentLevel.getFloorHeight() + height, 0, 0, 0, 0, 0, 0);
        BluetoothBeacon newBeacon = new BluetoothBeacon(beaconLocation, uuid, major, minor);
        currentLevel.addBluetoothBeacon(newBeacon);
    }
    
     public Route getNewRoute() {
        return newRoute;
    }
     
    public List<Elevator> getElevators() {
        return map.getElevators();
    }
    
    public List<Route>getRoutes() {
        return map.getRoutes();
    }
    
    public void addElevator(Elevator elevator) {
        map.addElevator(elevator);
    }

    public void addStaircase(Point2d stairsPosition, Level upperLevel, Level lowerLevel) {
        currentLevel.addStaircase(new Staircase(stairsPosition, upperLevel, lowerLevel));
    }
    
    public void clearTempObjects() {
        newObstacle = null;
        newRoute = null;
        tempObstaclePoint = null;
        tempRoutePoint = null;
    }
    
    public void setMap(Map newMap) {
        this.map = newMap;
        clearTempObjects();
    }

    public int getCompassRotation() {
        return map.getCompassRotation();
    }

    public void setCompassRotation(int compassRotation) {
        map.setCompassRotation(compassRotation);
    }
    
    private Map map;
    private Obstacle newObstacle;
    private Level currentLevel;
    private Point2d tempObstaclePoint;
    private Route newRoute;
    private Point2d tempRoutePoint;
}
