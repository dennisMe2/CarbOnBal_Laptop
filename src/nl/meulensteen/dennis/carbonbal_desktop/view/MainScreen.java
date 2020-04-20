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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;


public class MainScreen extends JFrame implements ActionListener {
    JComboBox portList;
   
    
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
        JMenu menuDisplay = new JMenu("Window");
        JMenu menuHelp = new JMenu("Help");
        
        menuBar.add(menuFile);
        
        menuBar.add(menuDisplay);
        menuBar.add(menuHelp);
        JMenuItem menuItemOpen = new JMenuItem("Open");
//        menuItemOpen.addActionListener((ActionEvent e) -> {
//            startSerialComms();
//        });  


        JMenuItem menuItemSaveAs = new JMenuItem("Save as");
        menuFile.add(menuItemOpen);
       
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

        List<String> portStrings = SerialStuff.getInstance().listSerialPorts();

        portList = new JComboBox(portStrings.toArray(new String[0]));
        //portList.setSelectedIndex(4);
        
        portList.addActionListener((ActionEvent e) -> {
            JComboBox comboBox = (JComboBox) e.getSource();
            Object o = comboBox.getSelectedItem();
        });  
    

        JToolBar toolbar = new JToolBar();  
        toolbar.setRollover(true);  
        JButton connectButton = new JButton("Connect");  
        connectButton.addActionListener((ActionEvent e) -> {
            startSerialComms();
        });  
        toolbar.add(connectButton);  
        toolbar.addSeparator();   
        toolbar.add(portList);  
        toolbar.addSeparator();  
        JButton refreshButton = new JButton("Refresh");  
        refreshButton.addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> {
                List<String> myPortStrings = SerialStuff.getInstance().listSerialPorts();
                DefaultComboBoxModel newModel = new DefaultComboBoxModel(myPortStrings.toArray(new String[0]));
                portList.setModel(newModel);
                this.repaint();
            });
        });
        
        toolbar.add(refreshButton);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(BorderLayout.NORTH, menuBar);
        topPanel.add(BorderLayout.SOUTH, toolbar);
       
        JTextArea centerText = new JTextArea();

        //Adding Components to the frame.
        this.getContentPane().add(BorderLayout.NORTH, topPanel);
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
    
        
        if (cmd.equals("Connect CarbOnBal")) {
            startSerialComms();
        }

    }

    private void startSerialComms() {
        Runnable serialWorker = () -> {
            try {
                SerialStuff.getInstance().initSerialComms((String) portList.getSelectedItem());
                SerialStuff.getInstance().openSerialPort();
            } catch (InterruptedException ex) {
                Logger.getLogger(XYLineChart.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        Thread thread = new Thread(serialWorker);
        thread.start();
    }
}
