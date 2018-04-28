package model.map;

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class Level implements Serializable {
    
    private class ImageContainer {
        public Image image;
        public String path;
        public String filename;
        public int width, height;
        
        public ImageContainer(){}

        public ImageContainer(Image image, String path, String filename, int width, int height) {
            this.image = image;
            this.path = path;
            this.filename = filename;
            this.width = width;
            this.height = height;
        }
    }
    
    private double floorHeight; //y coordinate of the floor
    private List<Obstacle> obstacles;
    private List<PointOfInterest> pointsOfInterest;
    private List<Staircase> stairs;
    private List<Elevator> elevators;
    private List<BluetoothBeacon> bluetoothBeacons;
    private String name;
    //variable to store floorplan in jpg/png
    private ImageContainer image;

    public Level(String name) {
        this.name = name;
        obstacles = new ArrayList<>();
        pointsOfInterest = new ArrayList<>();
        stairs = new ArrayList<>();
        elevators = new ArrayList<>();
        bluetoothBeacons = new ArrayList<>();
    }

    public Level(String name, double floorHeight) {
        this.name = name;
        this.floorHeight = floorHeight;
        obstacles = new ArrayList<>();
        pointsOfInterest = new ArrayList<>();
        stairs = new ArrayList<>();
        elevators = new ArrayList<>();
        bluetoothBeacons = new ArrayList<>();
    }

    public double getFloorHeight() {
        return floorHeight;
    }

    public void setFloorHeight(double floorHeight) {
        this.floorHeight = floorHeight;
    }
    
    public void addObstacle(Obstacle o) {
        obstacles.add(o);
    }
    
    public Obstacle[] getObstacles() {
        Obstacle[] retVal = new Obstacle[obstacles.size()];
        retVal = obstacles.toArray(retVal);
        return retVal;
    }
    
    public void addPointOfInterest(PointOfInterest p) {
        pointsOfInterest.add(p);
    }
    
    public PointOfInterest[] getPointsOfInterest() {
        PointOfInterest[] retVal = new PointOfInterest[pointsOfInterest.size()];
        retVal = pointsOfInterest.toArray(retVal);
        return retVal;
    }
    
    public void addStaircase(Staircase s) {
        stairs.add(s);
    }
    
    public Staircase[] getStaircases() {
        Staircase[] retVal = new Staircase[stairs.size()];
        retVal = stairs.toArray(retVal);
        return retVal;
    }
    
    public void addElevator(Elevator e) {
        elevators.add(e);
    }
    
    public Elevator[] getElevators() {
        Elevator[] retVal = new Elevator[elevators.size()];
        retVal = elevators.toArray(retVal);
        return retVal;
    }
    
    public void addBluetoothBeacon(BluetoothBeacon b) {
        bluetoothBeacons.add(b);
    }
    
    public BluetoothBeacon[] getBluetoothBeacons() {
        BluetoothBeacon[] retVal = new BluetoothBeacon[bluetoothBeacons.size()];
        retVal = bluetoothBeacons.toArray(retVal);
        return retVal;
    }

    public void addImage(Image levelImage, String path, String fileName, int width, int height) {
        image = new ImageContainer(levelImage, path, fileName, width, height);
    }
}
