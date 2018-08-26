package com.project.dp130634.indoornavigation.controller;

import com.project.dp130634.indoornavigation.model.Model;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import com.project.dp130634.indoornavigation.model.map.*;
import com.project.dp130634.indoornavigation.view.CoordinateMapper;
import com.project.dp130634.indoornavigation.view.ViewInterface;

public class Controller {
    
    public enum Element {
        BEACON, OBSTACLE, STAIRS, ELEVATOR, POINT_OF_INTEREST, ROUTE, SET_ORIGIN, DELETE
    };

    public Controller(ViewInterface view) {
        this.view = view;
        this.model = new Model();
        selectedElement = Element.BEACON;
    }
    
    public void elementSelected(Element element) {
        selectedElement = element;
        model.clearTempObjects();
    }
    
    public Element getSelectedElement() {
        return this.selectedElement;
    }

    public Model getModel() {
        return model;
    }
    
    public Map getMap() {
        return model.getMap();
    }
    
    public void newMap() {
        model.newMap();
        model.clearTempObjects();
        view.refresh(model);
    }

    public void addLevel(String name, double height) {
        model.addLevel(name, height);
        model.clearTempObjects();
        view.refresh(model);
    }
    
    public void selectLevel(String level) {
        for(Level cur : model.getAllLevels()) {
            if(cur.getName().equals(level)) {
                model.setCurrentLevel(cur);
            }
        }
        model.clearTempObjects();
        view.refresh(model);
    }
    
    public Level getSelectedLevel() {
        return model.getCurrentLevel();
    }
    
    public void importImage(String imagePath, Point2d firstCoordinate, Point2d lastCoordinate) {
        try {
            Image levelImage = ImageIO.read(new File(imagePath));
            model.addLevelImage(levelImage, firstCoordinate, lastCoordinate);
            model.clearTempObjects();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    public void loadMap(String path, String mapName) {
        try(FileInputStream fin = new FileInputStream(path + "\\" + mapName);
                ObjectInputStream ois = new ObjectInputStream(fin)) {
            Map newMap = (Map)ois.readObject();
            model.clearTempObjects();
            model.setMap(newMap);
            selectLevel("Ground floor");
            view.refresh(model);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
        
    public void saveMap(String path, String mapName) {
        try(FileOutputStream fout = new FileOutputStream(path + "\\" + mapName, true);
                ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            model.getMap().setName(mapName);
            oos.writeObject(model.getMap());
            view.refresh(model);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
    }
    
    public void addObstaclePoint(Point2d obstaclePoint) {
        model.addObstaclePoint(obstaclePoint);
    }

    public void finalizeObstacle(Point2d obstaclePoint) {
        model.finalizeObstacle(obstaclePoint);
        view.refresh(model);
    }
    
    public void addPOI(Point2d clickPosition, String name, String contentURL) {
        model.addPOI(clickPosition, name, contentURL);
    }
    
    public void addBeacon(Point2d clickPosition, String uuid, int major, int minor, double height) {
        model.addBeacon(clickPosition, UUID.fromString(uuid), major, minor, height);
    }

    public void mouseMoved(Point2d position) {
        if(model.getNewObstacle() != null) {
            model.setTempObstaclePoint(position);
            //view.tick();
        }
        if(model.getNewRoute() != null) {
            model.setTempRoutePoint(position);
            //view.tick();
        }
    }
    
    public void addRoutePoint(Point2d clickPosition) {
        model.addRoutePoint(clickPosition);
    }
    
    public void finalizeRoute(Point2d clickPosition, String name) {
        model.finalizeRoute(clickPosition, name);
        //view.tick();
    }
    
    public Level[] routeLevelFinish(Point2d clickPosition) {
        Set<Level> retVal = new HashSet<>();
        for(Staircase cur : model.getCurrentLevel().getStaircases()) {
            if(isInRadius(cur.getLocation(), clickPosition, 0.5d)) {
                retVal.add(cur.getLowerLevel());
                retVal.add(cur.getUpperLevel());
            }
        }
        
        for(Elevator cur : model.getElevators()) {
            if(isInRadius(cur.getLocation(), clickPosition, 0.5d) && cur.getLevels().contains(model.getCurrentLevel())){
                for(Level lvl : cur.getLevels()) {
                    if(lvl != model.getCurrentLevel()) {
                        retVal.add(lvl);
                    }
                }
            }
        }
        
        return retVal.toArray(new Level[0]);
    }

    public void continueRouteOnLevel(Level chosenLevel, Point2d clickPosition) {
        model.setCurrentLevel(chosenLevel);
        addRoutePoint(clickPosition);
        view.refresh(model);
    }

    public Level[] getAllLevels() {
        return model.getAllLevels().toArray(new Level[0]);
    }

    public Level getCurrentLevel() {
        return model.getCurrentLevel();
    }
    

    public void addStaircase(Point2d stairsPosition, Level upperLevel, Level lowerLevel) {
        if(upperLevel == model.getCurrentLevel()) {
            upperLevel = null;
        }
        if(lowerLevel == model.getCurrentLevel()) {
            lowerLevel = null;
        }
        if(upperLevel == null && lowerLevel == null) {
            throw new IllegalArgumentException("Unspecified lower and upper level for staircase");
        }
        model.getCurrentLevel().addStaircase(new Staircase(stairsPosition, upperLevel, lowerLevel));
        //Make staircases on upper and lower level as well
        if(upperLevel != null) {
            upperLevel.addStaircase(new Staircase(stairsPosition, null, model.getCurrentLevel()));
        }
        if(lowerLevel != null) {
            lowerLevel.addStaircase(new Staircase(stairsPosition, model.getCurrentLevel(), null));
        }
    }
    
    public void addElevator(Point2d location, List<Level> selectedLevels) {
        model.addElevator(new Elevator(location, selectedLevels));
    }

    /**
     * Method finds where is the element that needs to be deleted and deletes it.
     * @param toDelete element to find and delete
     */
    public void deleteElement(MapElement toDelete) {
        model.getCurrentLevel().getBluetoothBeacons().remove(toDelete);
        model.getElevators().remove(toDelete);
        model.getCurrentLevel().getPointsOfInterest().remove(toDelete);
        model.getCurrentLevel().getStaircases().remove(toDelete);
        model.getRoutes().remove(toDelete);
        model.getCurrentLevel().getObstacles().remove(toDelete);
        if(model.getCurrentLevel().getImage() == toDelete) {
            model.getCurrentLevel().setImage(null);
        }
    }
    
    public void addCompassRotation(int rotation) {
        model.setCompassRotation(model.getCompassRotation() + rotation);
    }
    
    public MapElement findElement(Point2d clickPosition) {
        final double detectionThreshold = 0.1d;
        for(BluetoothBeacon cur : model.getCurrentLevel().getBluetoothBeacons()) {
            Point2d bbLocation = new Point2d(cur.getLocation().getX(), cur.getLocation().getY());
            if(isInRadius(bbLocation, clickPosition, detectionThreshold)) {
                return cur;
            }
        }
        
        for(Elevator cur : model.getElevators()) {
            if(cur.getLevels().contains(model.getCurrentLevel()) &&
                    isInRadius(cur.getLocation(), clickPosition, detectionThreshold)) {
                return cur;
            }
        }
        
        for(PointOfInterest cur : model.getCurrentLevel().getPointsOfInterest()) {
            if(isInRadius(cur.getLocation(), clickPosition, detectionThreshold)) {
                return cur;
            }
        }
        
        for(Staircase cur : model.getCurrentLevel().getStaircases()) {
            if(isInRadius(cur.getLocation(), clickPosition, detectionThreshold)) {
                return cur;
            }
        }
        
        MapElement cur = findRoute(clickPosition, detectionThreshold);
        if(cur != null) {
            return cur;
        }
        
        cur = findObstacle(clickPosition, detectionThreshold);
        if(cur != null) {
            return cur;
        }
        
        Level.ImageContainer plan = model.getCurrentLevel().getImage();
        if(plan != null) {
            Point2d planPoints[] = new Point2d[4];
            planPoints[0] = plan.firstCoordinate;
            planPoints[1] = new Point2d(plan.firstCoordinate.x, plan.lastCoordinate.y);
            planPoints[2] = plan.lastCoordinate;
            planPoints[3] = new Point2d(plan.lastCoordinate.x, plan.firstCoordinate.y);
            
            if(isInPolygon(planPoints, clickPosition)) {
                return plan;
            }
        }
        
        return null;
    }
    
    /**
     * Sets new origin for coordinate system.
     * All elements on all floors change their coordinates, 
     * so their position relative to the new (0, 0) changes.
     * 
     * Floors other than the current need to change 
     * so elevators and staircases don't slide out
     * 
     * @param origin coordinates of the new (0, 0);
     */
    public void setOrigin(Point2d origin)
    {
        for(Level curLevel : model.getAllLevels()) {
            //Beacons
            for(BluetoothBeacon curElem : curLevel.getBluetoothBeacons()) {
                curElem.getLocation().setX(curElem.getLocation().getX() - origin.x);
                curElem.getLocation().setY(curElem.getLocation().getY() - origin.y);
            }
            //POIs
            for(PointOfInterest curElem : curLevel.getPointsOfInterest()) {
                curElem.getLocation().x -= origin.x;
                curElem.getLocation().y -= origin.y;
            }
            //Obstacles
            for(Obstacle curElem : curLevel.getObstacles()) {
                for(Point2d obstaclePoint : curElem.getPoints()) {
                    obstaclePoint.x -= origin.x;
                    obstaclePoint.y -= origin.y;
                }
            }
            //Staircases
            for(Staircase curElem : curLevel.getStaircases()) {
                curElem.getLocation().x -= origin.x;
                curElem.getLocation().y -= origin.y;
            }
            
            //Plan
            if(curLevel.getImage() != null) {
                curLevel.getImage().firstCoordinate.x -= origin.x;
                curLevel.getImage().firstCoordinate.y -= origin.y;
                curLevel.getImage().lastCoordinate.x -= origin.x;
                curLevel.getImage().lastCoordinate.y -= origin.y;
            }
        }
        //Elevators
        for(Elevator curElem : model.getElevators()) {
            curElem.getLocation().x -= origin.x;
            curElem.getLocation().y -= origin.y;
        }
        
        //Routes
        for(Route curElem : model.getRoutes()) {
            for(Checkpoint checkpoint : curElem.getCheckpoints()) {
                checkpoint.getLocation().x -= origin.x;
                checkpoint.getLocation().y -= origin.y;
            }
        }
    }
    
    private Route findRoute(Point2d clickPosition, double detectionThreshold) {
        for(Route cur : model.getRoutes()) {
            Checkpoint[] checkpoints = cur.getCheckpoints();
            for(int i = 1; i < checkpoints.length; i++) {
                if(checkpoints[i - 1].getLevel() == model.getCurrentLevel() &&
                        checkpoints[i].getLevel() == model.getCurrentLevel()) {
                    if(isNearLineSegment(
                            checkpoints[i - 1].getLocation(),
                            checkpoints[i].getLocation(),
                            clickPosition,
                            detectionThreshold)) {
                        return cur;
                    }
                }
            }
        }
        return null;
    }
    
    private Obstacle findObstacle(Point2d clickPosition, double detectionThreshold) {
        for(Obstacle cur : model.getCurrentLevel().getObstacles()) {
            if(isInPolygon(cur.getPoints(), clickPosition)) {
                return cur;
            }
        }
        
        return null;
    }
    
    private boolean isNearLineSegment(Point2d seg1, Point2d seg2, Point2d location, double detectionThreshold) {
        double a = CoordinateMapper.pointDistance(seg2, location);
        double b = CoordinateMapper.pointDistance(seg1, location);
        double c = CoordinateMapper.pointDistance(seg1, seg2);
        if(a + b <= c + (detectionThreshold / 4)) {
            return true;
        }
        return false;
    }

    private boolean isInPolygon(Point2d[] points, Point2d clickPosition) {
        int j = points.length - 1;
        boolean oddNodes = false;

        for (int i = 0; i < points.length; i++)
        {
            if (points[i].y < clickPosition.y && points[j].y >= clickPosition.y ||
                points[j].y < clickPosition.y && points[i].y >= clickPosition.y)
            {
                if (points[i].x +
                    (clickPosition.y - points[i].y)/(points[j].y - points[i].y)*(points[j].x - points[i].x) < clickPosition.x)
                {
                    oddNodes = !oddNodes;
                }
            }
            j = i;
        }

        return oddNodes;
    }

    private boolean isInRadius(Point2d location1, Point2d location2, double radius) {
        double distance = Math.sqrt(Math.pow((location1.getX() - location2.getX()), 2) + Math.pow((location1.getY() - location2.getY()), 2));
        return distance <= radius;
    }

    private final ViewInterface view;
    private Element selectedElement;
    private final Model model;
}
