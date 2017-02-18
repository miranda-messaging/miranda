package test.keytool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Clark on 2/13/2017.
 */
public class Keytool extends JFrame {
    enum KeytoolDecisions {
        Create,
        Cancel
    }

    private JTextField caTextField;
    private JTextField directoryField;
    private JPanel mainPanel;
    private KeytoolDecisions decision = KeytoolDecisions.Cancel;

    public void build () {
        mainPanel = new JPanel();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Dimension d = new Dimension (screenSize.width/2, screenSize.height/2);
        setSize(d);

        setLocation(screenSize.width/4, screenSize.height/4);

        Insets insets = new Insets(5,5,5,5);
        GridBagConstraints constraints;

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        mainPanel = new JPanel();
        constraints = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets,0,0);
        mainPanel.setLayout(gbl);
        add(mainPanel, constraints);

        JLabel label = new JLabel("Directory");
        constraints = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0);
        mainPanel.add(label,constraints);

        directoryField = new JTextField();
        constraints = new GridBagConstraints(1,0,1,1,1.0,0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets,0,0);
        mainPanel.add(directoryField, constraints);

        label = new JLabel("Certificate Authority Alias");
        constraints = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0);
        mainPanel.add(label, constraints);

        caTextField = new JTextField();
        constraints = new GridBagConstraints(1,1,1,1,1.0,0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0,0);
        mainPanel.add(caTextField, constraints);

        JButton button = new JButton("OK");
        constraints = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0);
        mainPanel.add(button, constraints);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                decision = KeytoolDecisions.Create;
            }
        });

        button = new JButton("Cancel");
        constraints = new GridBagConstraints(1,2,1,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0);
        mainPanel.add(button, constraints);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                decision = KeytoolDecisions.Cancel;
            }
        });
    }


    public static void main (String[] argv) {
        Keytool keytool = new Keytool();
        keytool.build();
        keytool.setVisible(true);
    }
}
