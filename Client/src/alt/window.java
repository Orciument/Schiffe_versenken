package alt;

import javax.swing.*;
import java.awt.*;

public class window extends JFrame {
    JFrame jFrame = new JFrame();

    public static void main(String[] args) {
        new window();
    }

    public window() {

        setSize(1500, 1050);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Schiffe Versenken");
        setLayout(null);

        connect();
        update(getGraphics());
    }

    public JPanel oneField() {
        JPanel jPanelField = new JPanel();
        //Table
        JTable jTable = new JTable(10, 10);
        jTable.setBounds(10, 10, 1000, 1000);
        jTable.setRowHeight(100);
        jTable.setColumnSelectionAllowed(false);
        jTable.setRowSelectionAllowed(false);
        jTable.setVisible(true);

        for (int i = 0; i < 10; i++) {
            jTable.getColumnModel().getColumn(i).setPreferredWidth(100);
            jTable.getColumnModel().getColumn(i).setMinWidth(100);
            for (int j = 0; j < 10; j++) {
                jTable.setValueAt("0", i, j);
            }
        }
        //Table
        jPanelField.setBounds(10, 10, 1000, 1000);
        jPanelField.setVisible(true);
        jPanelField.add(jTable);
        return jPanelField;
    }

    public void connect() {
        JLabel jLabelName = new JLabel();
        JTextField jTextFieldName = new JTextField();
        JLabel jLabelHostname = new JLabel();
        JTextField jTextFieldHostname = new JTextField();
        JLabel jLabelPort = new JLabel();
        JTextField jTextFieldPort = new JTextField();
        JButton jButtonConfirm = new JButton();

        Container cp = new Container();
        cp.setBounds(40,0,400,1000);

        cp.setBackground(Color.WHITE);
        jLabelName.setBounds(0, 20, 400, 20);
        jLabelName.setText("Bitte gebe einen Namen ein:");
        cp.add(jLabelName);
        jTextFieldName.setBounds(0, 60, 400, 40);
        cp.add(jTextFieldName);
        jLabelHostname.setBounds(0, 100, 400, 40);
        jLabelHostname.setText("Bitte gebe die IP ein:");
        cp.add(jLabelHostname);
        jTextFieldHostname.setBounds(0, 140, 400, 40);
        cp.add(jTextFieldHostname);
        jLabelPort.setBounds(0, 180, 400, 40);
        jLabelPort.setText("Bitte gebe den Port ein ein:");
        cp.add(jLabelPort);
        jTextFieldPort.setBounds(0,220, 400,40);
        cp.add(jTextFieldPort);
        jButtonConfirm.setBounds(0, 270, 75, 25);
        jButtonConfirm.setText("Bestätigen");
        jButtonConfirm.setMargin(new Insets(2, 2, 2, 2));
        jButtonConfirm.addActionListener(evt -> {

        });
        cp.add(jButtonConfirm);
        add(cp);
    }

    public JPanel connectPanel() {
        JPanel connectPanel = new JPanel();
        connectPanel.setBounds(100,100,400,1000);


        JLabel jLabelName = new JLabel("Bitte gebe einen Namen ein:");
        jLabelName.setVisible(true);
        connectPanel.add(jLabelName);
        JTextField jTextFieldName = new JTextField(20);
        jLabelName.setVisible(true);
        connectPanel.add((jTextFieldName));

        JLabel jLabelHostname = new JLabel("Bitte gebe die IP ein:");
        jLabelName.setVisible(true);
        connectPanel.add(jLabelHostname);
        JTextField jTextFieldHostname = new JTextField(20);
        jLabelName.setVisible(true);
        connectPanel.add(jTextFieldHostname);

        JLabel jLabelPort = new JLabel("Bitte gebe den Port ein ein:");
        jLabelName.setVisible(true);
        connectPanel.add(jLabelPort);
        JTextField jTextFieldPort = new JTextField(20);
        jLabelName.setVisible(true);
        connectPanel.add(jTextFieldPort);

        JButton jButtonConfirm = new JButton();
        jButtonConfirm.setVisible(true);
        jButtonConfirm.setText("Bestätigen");
        connectPanel.add(jButtonConfirm);

        connectPanel.setVisible(true);
        return connectPanel;

    }


}
