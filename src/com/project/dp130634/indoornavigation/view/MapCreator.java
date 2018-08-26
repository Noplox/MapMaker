package com.project.dp130634.indoornavigation.view;

import com.project.dp130634.indoornavigation.controller.Controller;
import com.project.dp130634.indoornavigation.controller.Controller.Element;
import com.project.dp130634.indoornavigation.model.Model;
import com.project.dp130634.indoornavigation.model.map.Level;
import com.project.dp130634.indoornavigation.model.map.Point2d;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author John
 */
public class MapCreator extends JFrame implements ViewInterface, MouseMotionListener, MouseListener, MouseWheelListener {
    public static final String APP_NAME = "Map creator";
    public static final int MAX_TICK_RATE = 300;
    public static final int COMPASS_POSITION = 20;
    public static final int COMPASS_RADIUS = 30;
    public static final int[] COMPASS_NEEDLE_X = {32, 38, 35};
    public static final int[] COMPASS_NEEDLE_Y = {35, 35, 20};

    private class LevelSelectListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            levelSelected();
        }
        
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MapCreator myMap = new MapCreator();
                myMap.setVisible(true);
            }
        });
    }
    
    public MapCreator() {
        this.levelVector = new Vector();
        this.controller = new Controller(this);
        this.canvas = new MapCanvas(controller.getModel(), this);
        this.levelSelectListener = new LevelSelectListener();
        this.coordinateMapper = CoordinateMapper.getInstance();
        initComponents();
        setIcon();
        newMap();
        Thread tickerThread = new Thread(ticker);
        tickerThread.start();
    }
    
    private void initComponents() {
        placementButtonGroup = new ButtonGroup();
        sidebar = new JToolBar();
        bluetoothBeaconRadio = new JRadioButton();
        elevatorRadio = new JRadioButton();
        obstacleRadio = new JRadioButton();
        pointOfInterestRadio = new JRadioButton();
        routeRadio = new JRadioButton();
        staircaseRadio = new JRadioButton();
        setOriginRadio = new JRadioButton();
        deleteRadio = new JRadioButton();
        bottomBar = new JToolBar();
        levelComboBox = new JComboBox(levelVector);
        addLevelButton = new JButton("Add new level");
        loadMapButton = new JButton("Load map");
        newMapButton = new JButton("New map");
        saveMapButton = new JButton("Save map");
        importImageButton = new JButton("Import plan");
        x = new JLabel("x: ");
        y = new JLabel("y: ");
        xNumber = new JTextField();
        yNumber = new JTextField();
        
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(200, 200, 200));
        setMinimumSize(new java.awt.Dimension(640, 480));
        setPreferredSize(new java.awt.Dimension(1024, 768));
        setSize(new java.awt.Dimension(800, 600));
        setTitle(APP_NAME + " - " + controller.getMap().getName());
        
        sidebar.setOrientation(SwingConstants.VERTICAL);
        sidebar.setRollover(true);
        sidebar.setFloatable(false);
        bottomBar.setFloatable(false);
        
        createSidebar();
        createBottomBar();
        
        setLayout(new GridBagLayout());
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 9;
        gc.weighty = 60;
        gc.gridx = 0;
        gc.gridy = 0;
        canvas.setBackground(Color.white);
        add(canvas, gc);
        
        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 0;
        gc.weighty = 9;
        add(sidebar, gc);
        
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 10;
        gc.weighty = 1;
        add(bottomBar, gc);
        
    }
    
    private void setIcon() {
        java.net.URL url = ClassLoader.getSystemResource("com/project/dp130634/indoornavigation/resources/icon.png");
        setIconImage(Toolkit.getDefaultToolkit().createImage(url));
    }
    
    private void createSidebar() {
        sidebar.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.CENTER;
        
        newMapButton.setVerticalTextPosition(AbstractButton.CENTER);
        newMapButton.setHorizontalTextPosition(AbstractButton.CENTER);
        newMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newMap();
            }
        });
        sidebar.add(newMapButton, gc);
        
        loadMapButton.setVerticalTextPosition(AbstractButton.CENTER);
        loadMapButton.setHorizontalTextPosition(AbstractButton.CENTER);
        loadMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMap();
            }
        });
        gc.gridy++;
        sidebar.add(loadMapButton, gc);
        
        saveMapButton.setVerticalTextPosition(AbstractButton.CENTER);
        saveMapButton.setHorizontalTextPosition(AbstractButton.CENTER);
        saveMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMap();
            }
        });
        gc.gridy++;
        sidebar.add(saveMapButton, gc);
        
        // <editor-fold defaultstate="collapsed" desc="Radio button generation">
        placementButtonGroup.add(bluetoothBeaconRadio);
        bluetoothBeaconRadio.setText("Bluetooth Beacon");
        bluetoothBeaconRadio.setSelected(true);
        bluetoothBeaconRadio.setFocusable(false);
        bluetoothBeaconRadio.setHorizontalAlignment(SwingConstants.CENTER);
        bluetoothBeaconRadio.setHorizontalTextPosition(SwingConstants.CENTER);
        bluetoothBeaconRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bluetoothBeaconRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        bluetoothBeaconRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elementSelected(Element.BEACON);
            }
            
        });
        gc.gridy++;
        sidebar.add(bluetoothBeaconRadio, gc);
        
        placementButtonGroup.add(elevatorRadio);
        elevatorRadio.setText("Elevator");
        elevatorRadio.setFocusable(false);
        elevatorRadio.setHorizontalAlignment(SwingConstants.CENTER);
        elevatorRadio.setHorizontalTextPosition(SwingConstants.CENTER);
        elevatorRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        elevatorRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        elevatorRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elementSelected(Element.ELEVATOR);
            }
            
        });
        gc.gridy++;
        sidebar.add(elevatorRadio, gc);
        
        placementButtonGroup.add(obstacleRadio);
        obstacleRadio.setText("Obstacle");
        obstacleRadio.setFocusable(false);
        obstacleRadio.setHorizontalAlignment(SwingConstants.CENTER);
        obstacleRadio.setHorizontalTextPosition(SwingConstants.CENTER);
        obstacleRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        obstacleRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        obstacleRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elementSelected(Element.OBSTACLE);
            }
            
        });
        gc.gridy++;
        sidebar.add(obstacleRadio, gc);
        
        placementButtonGroup.add(pointOfInterestRadio);
        pointOfInterestRadio.setText("Point of Interest");
        pointOfInterestRadio.setFocusable(false);
        pointOfInterestRadio.setHorizontalAlignment(SwingConstants.CENTER);
        pointOfInterestRadio.setHorizontalTextPosition(SwingConstants.CENTER);
        pointOfInterestRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pointOfInterestRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        pointOfInterestRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elementSelected(Element.POINT_OF_INTEREST);
            }
            
        });
        gc.gridy++;
        sidebar.add(pointOfInterestRadio, gc);
        
        placementButtonGroup.add(routeRadio);
        routeRadio.setText("Route");
        routeRadio.setFocusable(false);
        routeRadio.setHorizontalAlignment(SwingConstants.CENTER);
        routeRadio.setHorizontalTextPosition(SwingConstants.CENTER);
        routeRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        routeRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        routeRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elementSelected(Element.ROUTE);
            }
            
        });
        gc.gridy++;
        sidebar.add(routeRadio, gc);
        
        placementButtonGroup.add(staircaseRadio);
        staircaseRadio.setText("Staircase");
        staircaseRadio.setFocusable(false);
        staircaseRadio.setHorizontalAlignment(SwingConstants.CENTER);
        staircaseRadio.setHorizontalTextPosition(SwingConstants.CENTER);
        staircaseRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        staircaseRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        staircaseRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elementSelected(Element.STAIRS);
            }
            
        });
        gc.gridy++;
        sidebar.add(staircaseRadio, gc);
        
        placementButtonGroup.add(setOriginRadio);
        setOriginRadio.setText("Set coordinate origin");
        setOriginRadio.setFocusable(false);
        setOriginRadio.setHorizontalAlignment(SwingConstants.CENTER);
        setOriginRadio.setHorizontalTextPosition(SwingConstants.CENTER);
        setOriginRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        setOriginRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        setOriginRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elementSelected(Element.SET_ORIGIN);
            }
            
        });
        gc.gridy++;
        sidebar.add(setOriginRadio, gc);
        
        placementButtonGroup.add(deleteRadio);
        deleteRadio.setText("Delete element");
        deleteRadio.setFocusable(false);
        deleteRadio.setHorizontalAlignment(SwingConstants.CENTER);
        deleteRadio.setHorizontalTextPosition(SwingConstants.CENTER);
        deleteRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elementSelected(Element.DELETE);
            }
            
        });
        gc.gridy++;
        sidebar.add(deleteRadio, gc);
        // </editor-fold>  
        
        importImageButton.setVerticalTextPosition(AbstractButton.CENTER);
        importImageButton.setHorizontalTextPosition(AbstractButton.CENTER);
        importImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importImage();
            }
        });
        gc.gridy++;
        sidebar.add(importImageButton, gc);
        
        levelComboBox.setFocusable(false);
        levelComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelComboBox.addActionListener(levelSelectListener);
        gc.gridy++;
        sidebar.add(levelComboBox, gc);
        
        addLevelButton.setVerticalTextPosition(AbstractButton.CENTER);
        addLevelButton.setHorizontalTextPosition(AbstractButton.CENTER);
        addLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLevel();
            }
        });
        gc.gridy++;
        sidebar.add(addLevelButton, gc);
    }
    
    private void createBottomBar() {
        bottomBar.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        
        
        x.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomBar.add(x, gc);
        
        gc.gridx++;
        xNumber.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    canvasAction(1);
            }
        });
        
        bottomBar.add(xNumber, gc);
        
        y.setHorizontalAlignment(SwingConstants.RIGHT);
        gc.gridx++;
        bottomBar.add(y, gc);

        gc.gridx++;
        yNumber.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    canvasAction(1);
                    xNumber.requestFocus();
            }
        });
        
        bottomBar.add(yNumber, gc);
        
        gc.gridx++;
        bottomBar.add(new JLabel(), gc);
    }
    
    private void elementSelected(Element element) {                                         
        controller.elementSelected(element);
    }
    
    private void levelSelected() {
        LevelComboItem lvl = (LevelComboItem)levelComboBox.getSelectedItem();
        controller.selectLevel(lvl.getKey());
    }
    
    private void addLevel() {
        JTextField levelName = new JTextField();
        JTextField floorHeight = new JTextField();
        final JComponent[] inputs = new JComponent[] {
            new JLabel("Name"),
            levelName,
            new JLabel("floorHeight"),
            floorHeight
        };
        int result = JOptionPane.showConfirmDialog(this, inputs, "Add level", JOptionPane.PLAIN_MESSAGE);
        if(result == JOptionPane.OK_OPTION) {
            try {
                String name = levelName.getText();
                double height = Double.parseDouble(floorHeight.getText());
                controller.addLevel(name, height);
            } catch(NumberFormatException e) {}
        }
    }
    
    private void newMap() {
        controller.newMap();
    }
    
    private void loadMap() {
        JFileChooser c = new JFileChooser();
        c.setFileFilter(new FileFilter() {

            public String getDescription() {
                return "MapCreator files (*.map)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(".map");
                }
            }
         });
        int retVal = c.showOpenDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            controller.loadMap(c.getCurrentDirectory().toString(), c.getSelectedFile().getName());
        }
    }
    
    private void saveMap() {
        JFileChooser c = new JFileChooser();
        int retVal = c.showSaveDialog(this);
        if(retVal == JFileChooser.APPROVE_OPTION) {
            String filename = c.getSelectedFile().getName();
            if(!filename.endsWith(".map")) {
                filename += ".map";
            }
            controller.saveMap(c.getCurrentDirectory().toString(), c.getSelectedFile().getName());
        }
    }
    
    private void importImage() {
        JFileChooser c = new JFileChooser();
        c.setFileFilter(new FileFilter() {

            public String getDescription() {
                return "All supported types (*.jpg, *.jpeg, *.png)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(".jpg") || filename.endsWith(".jpeg")
                            || filename.endsWith(".png");
                }
            }
         });
        int rVal = c.showOpenDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            ImportImageDialog importDialog = new ImportImageDialog(this, controller, c.getSelectedFile().toPath().toString());
            importDialog.setVisible(true);            
        }
    }
    
    private void canvasAction(int clicks) {
        try {
            double x = Double.parseDouble(xNumber.getText());
            double y = Double.parseDouble(yNumber.getText());
            Point2d clickPosition = new Point2d(x, y);
            //Different behaviours needed for different elements.
            //For example, when adding POIs, a dialog should query the user for POI name and description
            //for elevators, the floors accessed by it should be defined
            //same for staircases, except they require one floor above and/or below
            //for beacons the user needs to enter beacon data (uuid, major/minor, tx power...)
            Controller.Element selectedElement = controller.getSelectedElement();
            switch (selectedElement) {
                case BEACON: {
                    JTextField uuid = new JTextField();
                    JTextField majorFld = new JTextField();
                    JTextField minorFld = new JTextField();
                    JTextField height = new JTextField();
                    final JComponent[] inputs = new JComponent[] {
                        new JLabel("UUID"),
                        uuid,
                        new JLabel("major"),
                        majorFld,
                        new JLabel("minor"),
                        minorFld,
                        new JLabel("Height"),
                        height
                    };
                    int result = JOptionPane.showConfirmDialog(this, inputs, "Beacon info", JOptionPane.PLAIN_MESSAGE);
                    if(result == JOptionPane.OK_OPTION) {
                        try {
                            double h = Double.parseDouble(height.getText());
                            int major = Integer.parseInt(majorFld.getText());
                            int minor = Integer.parseInt(minorFld.getText());
                            controller.addBeacon(clickPosition, uuid.getText(), major, minor, h);
                        } catch(IllegalArgumentException e) {
                            //show error dialog
                            JOptionPane.showMessageDialog(this, "Illegal arguments passed", "Beacon creation error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;
                }

                case OBSTACLE: {
                    if(clicks == 2) {
                        controller.finalizeObstacle(clickPosition);
                    }
                    else {
                        controller.addObstaclePoint(clickPosition);
                    }
                    break;
                }

                case ELEVATOR: {
                    ElevatorDialog dialog = new ElevatorDialog(this, controller, clickPosition);
                    dialog.setVisible(true);
                    break;
                }

                case POINT_OF_INTEREST: {
                    
                    
                    JTextField name = new JTextField();
                    JTextField contentURLField = new JTextField();
                    
                    final JComponent[] inputs = new JComponent[] {
                        new JLabel("Name"),
                        name,
                        new JLabel("Content URL"),
                        contentURLField,
                    };
                    int result = JOptionPane.showConfirmDialog(this, inputs, "Point of interest info", JOptionPane.PLAIN_MESSAGE);
                    if(result == JOptionPane.OK_OPTION) {
                        controller.addPOI(clickPosition, name.getText(), contentURLField.getText());
                    }
                    break;
                }
                
                case ROUTE: {
                    if(clicks == 2) {
                        Level[] nextLevels = controller.routeLevelFinish(clickPosition);
                        if(nextLevels.length != 0) {
                            //Show dialog with options on which level to continue route creation
                            Level chosenLevel = (Level)JOptionPane.showInputDialog(
                                    this, 
                                    "Select next level for route",
                                    "Level selection",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    nextLevels,
                                    controller.getCurrentLevel());
                            if(chosenLevel != null) {
                                //continue switch current level and continue route on chosenLevel
                                controller.continueRouteOnLevel(chosenLevel, clickPosition);
                            }
                        } else {
                            //Show dialog with route name textbox and finalize route
                            JTextField name = new JTextField();
                            final JComponent[] inputs = new JComponent[] {
                                new JLabel("Route name"),
                                name,
                            };
                            int result = JOptionPane.showConfirmDialog(this, inputs, "Route name", JOptionPane.PLAIN_MESSAGE);
                            if(result == JOptionPane.OK_OPTION) {
                                controller.finalizeRoute(clickPosition, name.getText());
                            }
                        }
                    }
                    else {
                        controller.addRoutePoint(clickPosition);
                    }
                    break;
                }

                case STAIRS: {
                    //POSSIBLE IMPROVEMENT: if clicked on already existing staircase, edit that one
                    StairsDialog dialog = new StairsDialog(this, controller, clickPosition);
                    dialog.setVisible(true);
                    break;
                }
                
                case SET_ORIGIN: {
                    Point toTranslate = coordinateMapper.scaleLength(clickPosition);
                    controller.setOrigin(clickPosition);
                    coordinateMapper.translateNoRotation(toTranslate);
                    break;
                }
                
                case DELETE: {
                    com.project.dp130634.indoornavigation.model.map.MapElement toDelete = controller.findElement(clickPosition);
                    if(toDelete != null) {
                        int choice = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to delete " + toDelete + "?",
                                "Delete element",
                                JOptionPane.YES_NO_OPTION);
                        if(choice == JOptionPane.YES_OPTION) {
                            controller.deleteElement(toDelete);
                        }
                    }
                    break;
                }
            }
        } catch(NumberFormatException ex) {}
    }
    
    @Override
    public void refresh(Model model) {
        levelComboBox.removeActionListener(levelSelectListener);
        levelComboBox.removeAllItems();
        int i = 0;
        for(Level cur : model.getAllLevels()) {
            levelComboBox.addItem(new LevelComboItem(cur));
            if(cur == model.getCurrentLevel()) {
                levelComboBox.setSelectedIndex(i);
            }
            i++;
        }
        levelComboBox.addActionListener(levelSelectListener);
        canvas.setModel(model);
        setTitle(APP_NAME + " - " + controller.getMap().getName());
    }
    
    public void tick() {
        canvas.prepareModel();
        canvas.repaint();
    }
    
    // <editor-fold desc="Mouse listeners">
    @Override
    public void mouseDragged(MouseEvent e) {
        if(compassClicked) {
            if(mouseLocation != null) {
                int rotation = e.getPoint().x - mouseLocation.x;
                controller.addCompassRotation(rotation);
            }
        } else {
            if(SwingUtilities.isLeftMouseButton(e)) {
                if(mouseLocation != null) {
                    Point translation = new Point();
                    translation.x = e.getPoint().x - mouseLocation.x;
                    translation.y = e.getPoint().y - mouseLocation.y;
                    coordinateMapper.translate(translation);
                }
            }
            if(SwingUtilities.isMiddleMouseButton(e)) {
                if(mouseLocation != null) {
                    int rotation = e.getPoint().x - mouseLocation.x;
                    coordinateMapper.rotate(rotation);
                }
            }
        }
        mouseLocation = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLocation = e.getPoint();
        Point2d point = coordinateMapper.mapPoint(mouseLocation);

        
        
        xNumber.setText(String.format("%1$,.2f", point.getX()));
        yNumber.setText(String.format("%1$,.2f", point.getY()));
        controller.mouseMoved(point);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        canvasAction(e.getClickCount());
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.getWheelRotation() != 0) {
            if(e.getWheelRotation() > 0) {
                coordinateMapper.zoomOut(10);
            } else {
                coordinateMapper.zoomIn(10);
            }    
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point2d clickLocation = new Point2d(e.getX(), e.getY());
        int compassCenter = COMPASS_POSITION + (COMPASS_RADIUS / 2);
        Point2d compassLocation = new Point2d(compassCenter, compassCenter);
        if(CoordinateMapper.isInRadius(clickLocation, compassLocation, COMPASS_RADIUS)) {
            compassClicked = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        compassClicked = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Graphical elements">
    private ButtonGroup placementButtonGroup;
    private JRadioButton bluetoothBeaconRadio;
    private JRadioButton elevatorRadio;
    private JRadioButton obstacleRadio;
    private JRadioButton pointOfInterestRadio;
    private JRadioButton routeRadio;
    private JRadioButton staircaseRadio;
    private JRadioButton setOriginRadio;
    private JRadioButton deleteRadio;
    private JComboBox levelComboBox;
    private final Vector<String> levelVector;
    private JButton addLevelButton;
    private JButton loadMapButton;
    private JButton newMapButton;
    private JButton saveMapButton;
    private JButton importImageButton;
    private JToolBar sidebar;
    private JToolBar bottomBar;
    private JLabel x, y;
    private JTextField xNumber, yNumber;
    // </editor-fold>
    
    private final Controller controller;
    private final MapCanvas canvas;
    private final LevelSelectListener levelSelectListener;
    private final CoordinateMapper coordinateMapper;
    private Point mouseLocation;
    private boolean compassClicked = false;
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            try {
                int tickRate = MAX_TICK_RATE;
                while(true) {
                    Thread.sleep(1000/tickRate);
                    tick();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(MapCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    };
}
