package model.map;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;



public class Level implements Serializable {
    
    public class ImageContainer implements Serializable {
        public transient Image image;
        public double width, height;
        
        public ImageContainer(){}

        public ImageContainer(Image image, double width, double height) {
            this.image = image;
            this.width = width;
            this.height = height;
        }
        
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            if(image != null) {
                out.writeInt(1);
//                BufferedImage bufImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//                Graphics2D g = bufImage.createGraphics();
//                g.drawImage(image, 0, 0, null);
//                g.dispose();
                ImageIO.write((BufferedImage)image, "png", out);
            }
        }
        
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            final int imageCnt = in.readInt();
            if(imageCnt == 1) {
                image = ImageIO.read(in);
            }
        }
    }
    
    private double floorHeight; //y coordinate of the floor
    private List<Obstacle> obstacles;
    private List<PointOfInterest> pointsOfInterest;
    private List<Staircase> stairs;
    private List<BluetoothBeacon> bluetoothBeacons;
    private String name;
    //variable to store floorplan in jpg/png
    private ImageContainer image;

    public Level(String name) {
        this.name = name;
        obstacles = new ArrayList<>();
        pointsOfInterest = new ArrayList<>();
        stairs = new ArrayList<>();
        bluetoothBeacons = new ArrayList<>();
    }

    public Level(String name, double floorHeight) {
        this.name = name;
        this.floorHeight = floorHeight;
        obstacles = new ArrayList<>();
        pointsOfInterest = new ArrayList<>();
        stairs = new ArrayList<>();
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
    
    public void addBluetoothBeacon(BluetoothBeacon b) {
        bluetoothBeacons.add(b);
    }
    
    public BluetoothBeacon[] getBluetoothBeacons() {
        BluetoothBeacon[] retVal = new BluetoothBeacon[bluetoothBeacons.size()];
        retVal = bluetoothBeacons.toArray(retVal);
        return retVal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void addImage(Image levelImage, double width, double height) {
        image = new ImageContainer(levelImage, width, height);
    }
    
    public ImageContainer getImage() {
        return image;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
