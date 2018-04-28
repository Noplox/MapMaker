package controller;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import model.Model;
import model.map.*;
import view.ViewInterface;

public class Controller {

    public void newMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public enum Element {BEACON, OBSTACLE, STAIRS, ELEVATOR, POINT_OF_INTEREST, ROUTE};

    public Controller(ViewInterface view) {
        this.view = view;
        this.model = new Model();
        selectedElement = Element.BEACON;
    }
    
    public void elementSelected(Element element) {
        selectedElement = element;
    }

    public Model getModel() {
        return model;
    }
    
    public Map getMap() {
        return model.getMap();
    }
    
    public void importImage(String path, String fileName, int width, int height) {
        try {
            Image levelImage = ImageIO.read(new File(path + "\\" + fileName)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
            model.addLevelImage(levelImage, path, fileName, width, height);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    private final ViewInterface view;
    private Element selectedElement;
    private Model model;
}
