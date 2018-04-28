/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import controller.Controller.Element;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;
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
import javax.swing.filechooser.FileFilter;
import model.Model;

/**
 *
 * @author John
 */
public class MapCreator extends JFrame implements ViewInterface{
    public static final String APP_NAME = "Map creator";
    
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
        this.canvas = new MapCanvas(controller.getModel());
        initComponents();
        newMap();
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
        saveMapButton.setEnabled(false);
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
                elementSelected(e, Element.BEACON);
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
                elementSelected(e, Element.ELEVATOR);
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
                elementSelected(e, Element.OBSTACLE);
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
                elementSelected(e, Element.POINT_OF_INTEREST);
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
                elementSelected(e, Element.ROUTE);
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
                elementSelected(e, Element.STAIRS);
            }
            
        });
        gc.gridy++;
        sidebar.add(staircaseRadio, gc);
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
        levelComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                levelSelected(e);
            }
            
        });
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
        bottomBar.add(xNumber, gc);
        
        y.setHorizontalAlignment(SwingConstants.RIGHT);
        gc.gridx++;
        bottomBar.add(y, gc);

        gc.gridx++;
        bottomBar.add(yNumber, gc);
        
        gc.gridx++;
        bottomBar.add(new JLabel(), gc);
    }
    
    private void elementSelected(java.awt.event.ActionEvent evt, Element element) {                                         
        controller.elementSelected(element);
    }
    
    private void levelSelected(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }
    
    private void addLevel() {
        // TODO add your handling code here:
    }
    
    private void newMap() {
        controller.newMap();
    }
    
    private void loadMap() {
        // TODO add your handling code here:
    }
    
    private void saveMap() {
        // TODO add your handling code here:
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
            //Open dialog to ask for dimensions
            JTextField width = new JTextField();
            JTextField height = new JTextField();
            final JComponent[] inputs = new JComponent[] {
                new JLabel("Width"),
                width,
                new JLabel("Height"),
                height
            };
            int result = JOptionPane.showConfirmDialog(this, inputs, "Input dimensions", JOptionPane.PLAIN_MESSAGE);
            if(result == JOptionPane.OK_OPTION) {
                try {
                    int x = Integer.parseInt(width.getText());
                    int y = Integer.parseInt(height.getText());
                    controller.importImage(c.getCurrentDirectory().toString(), c.getSelectedFile().getName(), x, y);
                } catch(NumberFormatException e) {}
            }
            
            
        }
    }
    
    @Override
    public void refresh(Model model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    // <editor-fold defaultstate="collapsed" desc="Graphical elements">
    private ButtonGroup placementButtonGroup;
    private JRadioButton bluetoothBeaconRadio;
    private JRadioButton elevatorRadio;
    private JRadioButton obstacleRadio;
    private JRadioButton pointOfInterestRadio;
    private JRadioButton routeRadio;
    private JRadioButton staircaseRadio;
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
    private MapCanvas canvas;
}
