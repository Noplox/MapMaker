package view;

import controller.Controller;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import model.map.Point2d;

public class ImportImageDialog extends JDialog {

    public ImportImageDialog(Frame owner, Controller controller, String imagePath) {
        super(owner, "Select plan coordinates", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        
        gc.fill = GridBagConstraints.CENTER;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 2;
        startLabel = new JLabel("Choose plan starting coordinate: ");
        add(startLabel, gc);
        
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        firstX = new JTextField();
        add(firstX, gc);
        
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 1;
        gc.gridy = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        firstY = new JTextField();
        add(firstY, gc);
        
        gc.fill = GridBagConstraints.CENTER;
        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 2;
        endLabel = new JLabel("Choose plan final coordinate: ");
        add(endLabel, gc);
        
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.gridy = 3;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        lastX = new JTextField();
        add(lastX, gc);
        
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 1;
        gc.gridy = 3;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        lastY = new JTextField();
        add(lastY, gc);
        
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
                double x1 = Double.parseDouble(firstX.getText());
                double y1 = Double.parseDouble(firstY.getText());
                double x2 = Double.parseDouble(lastX.getText());
                double y2 = Double.parseDouble(lastY.getText());
                
                Point2d firstCoordinate = new Point2d(x1, y1);
                Point2d lastCoordinate = new Point2d(x2, y2);
                controller.importImage(imagePath, firstCoordinate, lastCoordinate);
                dispose();
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Illegal arguments passed", "Image import error", JOptionPane.ERROR_MESSAGE);
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

    private final JTextField firstX, firstY, lastX, lastY;
    private final JLabel startLabel, endLabel;
    private final JButton okButton;
    private final JButton cancelButton;
}
