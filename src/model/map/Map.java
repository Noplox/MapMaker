package model.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Map implements Serializable {
    private final List<Level> levels;
    private final List<Route> routes;
    private String name;

    public Map(String name) {
        this.levels = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.name = name;
    }

    public Map(List<Level> levels, List<Route> routes) {
        this.levels = levels;
        this.routes = routes;
    }

    public List<Level> getLevels() {
        return levels;
    }
    
    public void addLevel(Level level) {
        this.levels.add(level);
    }

    public List<Route> getRoutes() {
        return routes;
    }
    
    public void addRoute(Route route) {
        this.routes.add(route);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
