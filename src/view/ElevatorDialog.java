package view;

import controller.Controller;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import model.map.Level;
import model.map.Point2d;

public class ElevatorDialog extends JDialog {

    public ElevatorDialog(Frame owner, Controller controller, Point2d elevatorPosition) {
        super(owner, "Select levels to which the staircase leads", true);
        this.controller = controller;
        Level[] allLevels = controller.getAllLevels();
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        
        gc.fill = GridBagConstraints.CENTER;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 2;
        levelLabel = new JLabel("Choose levels to which the elevator leads: ");
        add(levelLabel, gc);
        
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 2;
        gc.gridheight = 4;
        levelList = new JList(allLevels);
        levelList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        levelScrollPane = new JScrollPane(levelList);
        add(levelScrollPane, gc);
        
        gc.fill = GridBagConstraints.EAST;
        gc.gridx = 0;
        gc.gridy = 5;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        okButton = new JButton("OK");
        okButton.addActionListener((ActionEvent e) -> {
            try {
                List<Level> selectedLevels = levelList.getSelectedValuesList();
                if(selectedLevels.size() < 2) {
                    throw new IllegalArgumentException("Elevator must lead to at least two floors");
                }
                controller.addElevator(elevatorPosition ,selectedLevels);
                dispose();
            } catch(IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(ElevatorDialog.this, ex.getMessage(), "Elevator creation error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(okButton, gc);
        
        gc.fill = GridBagConstraints.WEST;
        gc.gridx = 1;
        gc.gridy = 5;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> {
            dispose();
        });
        add(cancelButton, gc);

        setSize(new java.awt.Dimension(350, 250));
        setLocationRelativeTo(owner);
    }

    private JList levelList;
    private JScrollPane levelScrollPane;
    private final JLabel levelLabel;
    private final JButton okButton;
    private final JButton cancelButton;

    private final Controller controller;
}
