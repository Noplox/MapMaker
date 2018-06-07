package model.map;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;

public class PointOfInterest extends MapElement implements Serializable {
    private Point2d location;
    private String name;
    private String description;
    private transient Image image;

    public PointOfInterest() {
    }

    public PointOfInterest(Point2d location, String name, String description, Image image) {
        this.location = location;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public Point2d getLocation() {
        return location;
    }

    public void setLocation(Point2d location) {
        this.location = location;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if(image != null) {
            out.writeInt(1);
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

    @Override
    public String toString() {
        return "Point of interest \"" + name + '\"';
    }
    
    
}
