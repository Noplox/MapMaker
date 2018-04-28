package model.map;

import java.io.Serializable;

public class PointOfInterest implements Serializable {
    private Point2d location;
    private String name;
    private String description;

    public PointOfInterest() {
    }

    public PointOfInterest(Point2d location, String name, String description) {
        this.location = location;
        this.name = name;
        this.description = description;
    }

    public Point2d getLocation() {
        return location;
    }

    public void setLocation(Point2d location) {
        this.location = location;
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
}
