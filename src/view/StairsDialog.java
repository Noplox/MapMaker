package view;

import controller.Controller;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import model.map.Level;
import model.map.Point2d;

public class StairsDialog extends JDialog {

    public StairsDialog(Frame owner, Controller controller, Point2d stairsPosition) {
        super(owner, "Select levels to which the staircase leads", true);
        this.controller = controller;
        Level[] allLevels = controller.getAllLevels();
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.CENTER;
        
        lowerLevelLabel = new JLabel("Choose lower level: ");
        add(lowerLevelLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 0;
        lowerLevelComboBox = new JComboBox(allLevels);
        lowerLevelComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lowerLevelSelected();
            }
        });
        add(lowerLevelComboBox, gc);
        
        gc.gridx = 0;
        gc.gridy = 1;
        upperLevelLabel = new JLabel("Choose upper level: ");
        add(upperLevelLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 1;
        upperLevelComboBox = new JComboBox(allLevels);
        upperLevelComboBox.addActionListener((ActionEvent e) -> {
            upperLevelSelected();
        });
        add(upperLevelComboBox, gc);
        
        gc.fill = GridBagConstraints.EAST;
        gc.gridx = 0;
        gc.gridy = 2;
        okButton = new JButton("OK");
        okButton.addActionListener((ActionEvent e) -> {
            try {
                controller.addStaircase(stairsPosition, selectedUpperLevel, selectedLowerLevel);
                dispose();
            } catch(IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(StairsDialog.this, "Upper or lower level must differ from the current level.", "Staircase creation error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(okButton, gc);
        
        gc.fill = GridBagConstraints.WEST;
        gc.gridx = 1;
        gc.gridy = 2;
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> {
            dispose();
        });
        add(cancelButton, gc);
        
        //Set upper and lower selected level to initial comboBox selection
        upperLevelSelected();
        lowerLevelSelected();
        
        setSize(new java.awt.Dimension(350, 150));
        setLocationRelativeTo(owner);
    }
    
    private void upperLevelSelected() {
        //Is it possible that the combobox contains the whole level object?
        selectedUpperLevel = (Level)upperLevelComboBox.getSelectedItem();
    }
    
    private void lowerLevelSelected() {
        selectedLowerLevel = (Level)lowerLevelComboBox.getSelectedItem();
    }
    
    private JLabel upperLevelLabel;
    private JLabel lowerLevelLabel;
    private JComboBox upperLevelComboBox;
    private JComboBox lowerLevelComboBox;
    private JButton okButton;
    private JButton cancelButton;
    private Level selectedUpperLevel;
    private Level selectedLowerLevel;
    
    private Controller controller;
}
