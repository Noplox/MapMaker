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
import view.ViewInterface;

public class Controller {
    
    public enum Element {BEACON, OBSTACLE, STAIRS, ELEVATOR, POINT_OF_INTEREST, ROUTE};

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
    
    public void importImage(String path, String fileName, double width, double height) {
        try {
            Image levelImage = ImageIO.read(new File(path + "\\" + fileName));
            model.addLevelImage(levelImage, width, height);
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
    
    public void addPOI(Point2d clickPosition, String name, String description) {
        model.addPOI(clickPosition, name, description);
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

    private boolean isInRadius(Point2d location1, Point2d location2, double radius) {
        double distance = Math.sqrt(Math.pow((location1.getX() - location2.getX()), 2) + Math.pow((location1.getY() - location2.getY()), 2));
        return distance <= radius;
    }

    private final ViewInterface view;
    private Element selectedElement;
    private final Model model;
}
