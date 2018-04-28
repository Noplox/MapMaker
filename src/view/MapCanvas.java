package view;

import java.awt.Canvas;
import model.CanvasInfoProvider;

public class MapCanvas extends Canvas {

    public MapCanvas(CanvasInfoProvider infoProvider) {
        this.infoProvider = infoProvider;
    }

    private CanvasInfoProvider infoProvider;
}
