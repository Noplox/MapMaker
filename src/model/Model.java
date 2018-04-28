package model;

import java.awt.Image;
import model.map.*;

public class Model implements CanvasInfoProvider {

    @Override
    public CanvasElements getCanvasElements() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Model() {
        newMap();
    }
    
    public void newMap() {
        map = new Map("<untitled>");
        currentLevel = new Level("Ground floor", 0);
        map.addLevel(currentLevel);
    }

    public Map getMap() {
        return map;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
    
    private Map map;
    private Level currentLevel;

    public void addLevelImage(Image levelImage, String path, String fileName, int width, int height) {
        currentLevel.addImage(levelImage, path, fileName, width, height);
    }
}
