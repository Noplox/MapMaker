package controller;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import model.Model;
import model.map.*;
import view.CoordinateMapper;
import view.ViewInterface;

public class Controller {
    
    public enum Element {
        BEACON, OBSTACLE, STAIRS, ELEVATOR, POINT_OF_INTEREST, ROUTE, DELETE
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
    
    public void addPOI(Point2d clickPosition, String name, String description, String path) {
        Image POIImage = null;
        if(!"".equals(path) && path != null) {
            try {
                POIImage = ImageIO.read(new File(path));
            } catch (IOException ex) {
                POIImage = null;
            }
        }
        model.addPOI(clickPosition, name, description, POIImage);
    }
    
    public void addBeacon(Point2d clickPosition, String uuid, int major, int minor, double height, int txPower) {
        model.addBeacon(clickPosition, UUID.fromString(uuid), major, minor, height, txPower);
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

    public void deleteElement(MapElement toDelete) {
        model.getCurrentLevel().getBluetoothBeacons().remove(toDelete);
        model.getElevators().remove(toDelete);
        model.getCurrentLevel().getPointsOfInterest().remove(toDelete);
        model.getCurrentLevel().getStaircases().remove(toDelete);
        model.getRoutes().remove(toDelete);
        model.getCurrentLevel().getObstacles().remove(toDelete);
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
        
        return null;
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
