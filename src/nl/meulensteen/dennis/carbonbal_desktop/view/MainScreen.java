/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.view;

import nl.meulensteen.dennis.carbonbal_desktop.comms.SerialStuff;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MainScreen extends JFrame implements ActionListener {

    public MainScreen() {
        this.setSize(200, 700);
        this.setTitle("CarbOnBal Desktop");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        
        this.setSize(Integer.valueOf(prefs.get("MAIN_SCREEN_WIDTH", String.valueOf(640))), Integer.valueOf(prefs.get("MAIN_SCREEN_HEIGHT", String.valueOf(480))));
        this.setLocation(Integer.valueOf(prefs.get("MAIN_SCREEN_X_POS", String.valueOf(320))), Integer.valueOf(prefs.get("MAIN_SCREEN_Y_POS", String.valueOf(240)))); 
    
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                prefs.put("MAIN_SCREEN_WIDTH", String.valueOf(e.getComponent().getSize().width));
                prefs.put("MAIN_SCREEN_HEIGHT", String.valueOf(e.getComponent().getSize().height));
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                prefs.put("MAIN_SCREEN_X_POS", String.valueOf(e.getComponent().getLocation().x));
                prefs.put("MAIN_SCREEN_Y_POS", String.valueOf(e.getComponent().getLocation().y));
            }
        });


        //Creating the MenuBar and adding components
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuDisplay = new JMenu("Display");
        JMenu menuHelp = new JMenu("Help");

        menuBar.add(menuFile);
        menuBar.add(menuDisplay);
        menuBar.add(menuHelp);
        JMenuItem menuItemOpen = new JMenuItem("Open");
        JMenuItem menuItemSelectSerial = new JMenuItem("SelectSerialPort");
        menuItemSelectSerial.addActionListener(this);
        
        JMenuItem menuItemStartSerial = new JMenuItem("Connect CarbOnBal");
        menuItemStartSerial.addActionListener(this);

        JMenuItem menuItemSaveAs = new JMenuItem("Save as");
        menuFile.add(menuItemOpen);
        menuFile.add(menuItemSelectSerial);
        menuFile.add(menuItemStartSerial);
        menuFile.add(menuItemSaveAs);

        JMenuItem menuItemPlot = new JMenuItem("Plot");
        menuItemPlot.addActionListener(this);

        JMenuItem menuItemBar = new JMenuItem("Bar");
        menuItemBar.addActionListener(this);

        JMenuItem menuItemGuage = new JMenuItem("Guage");
        menuItemGuage.addActionListener(this);

        menuDisplay.add(menuItemPlot);
        menuDisplay.add(menuItemBar);
        menuDisplay.add(menuItemGuage);

        //Creating the panel at bottom and adding components
        JPanel bottomPanel = new JPanel();
        JLabel enterText = new JLabel("Enter Text");
        JTextField enterTextField = new JTextField(10);
        JButton send = new JButton("Send");
        JButton reset = new JButton("Reset");
        bottomPanel.add(enterText);
        bottomPanel.add(enterText);
        bottomPanel.add(enterTextField);
        bottomPanel.add(send);
        bottomPanel.add(reset);

        JTextArea centerText = new JTextArea();

        //Adding Components to the frame.
        this.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
        this.getContentPane().add(BorderLayout.NORTH, menuBar);
        this.getContentPane().add(BorderLayout.CENTER, centerText);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Plot")) {
            SwingUtilities.invokeLater(() -> {
                XYLineChart chart = new XYLineChart();
                chart.setVisible(true);
            });
        }
        if (cmd.equals("Bar")) {
            SwingUtilities.invokeLater(() -> {
                BarChart chart = new BarChart();
                chart.setVisible(true);
            });
        }
        if (cmd.equals("Guage")) {
            SwingUtilities.invokeLater(() -> {
                MeterChart chart = new MeterChart();
                chart.setVisible(true);
            });
        }
        //"SelectSerialPort" 
        if (cmd.equals("SelectSerialPort")) {
        
            
        }
        
        if (cmd.equals("Connect CarbOnBal")) {
            Runnable serialWorker = () -> {
                try {
                    //SerialStuff.getInstance().selectSerialPort();
                    SerialStuff.getInstance().initSerialComms();
               } catch (InterruptedException ex) {
                    Logger.getLogger(XYLineChart.class.getName()).log(Level.SEVERE, null, ex);
                }
            };
            Thread thread = new Thread(serialWorker);
            thread.start();
        }
    }
}
