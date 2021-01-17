/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.view;

import nl.meulensteen.dennis.carbonbal_laptop.comms.SerialStuff;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import lombok.extern.log4j.Log4j;
import nl.meulensteen.dennis.carbonbal_laptop.control.Dispatcher;
import nl.meulensteen.dennis.carbonbal_laptop.view.actions.SaveAction;

@Log4j
public class MainScreen extends CarbOnBalDisplay implements ActionListener {

    protected JMenuItem menuItemStartRecording,
            menuItemStopRecording,
            menuItemSaveRecordingAs,
            menuItemClearRecording;

    protected JButton saveAsButton,
            connectButton,
            refreshButton,
            recordButton,
            clearButton,
            disconnectButton,
            stopButton;

    protected JCheckBoxMenuItem menuItemPlot,
            menuItemBar,
            menuItemGuage,
            menuItemCalibration,
            menuItemRpm,
            menuItemSettings;

    protected VacuumChart chart;
    protected BarChart barChart;
    protected MeterChart guageChart;
    protected CalibrationChart calibrationChart;
    protected RpmChart rpmChart;
    protected SettingsChart settingsChart;

    private static final String NAME = "Main Screen";
    JComboBox portList;

    public MainScreen() {
        super(NAME);
        this.setSize(200, 700);
        this.setTitle("CarbOnBal Laptop");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createPreferences();

        //Creating the MenuBar and adding components
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuDisplay = new JMenu("Window");
        JMenu menuHelp = new JMenu("Help");

        menuBar.add(menuFile);

        menuBar.add(menuDisplay);
        menuBar.add(menuHelp);

        menuItemStartRecording = new JMenuItem("Start Recording");
        menuFile.add(menuItemStartRecording);
        menuItemStartRecording.addActionListener(this);

        menuItemStopRecording = new JMenuItem("Stop Recording");
        menuFile.add(menuItemStopRecording);
        menuItemStopRecording.addActionListener(this);

        menuItemSaveRecordingAs = new JMenuItem(new SaveAction(this, "Save As"));
        menuFile.add(menuItemSaveRecordingAs);

        menuItemClearRecording = new JMenuItem("Clear Recording");
        menuFile.add(menuItemClearRecording);
        menuItemClearRecording.addActionListener(this);

        menuItemPlot = new JCheckBoxMenuItem("Plot");
        menuItemPlot.addActionListener(this);
        menuItemPlot.setMnemonic(KeyEvent.VK_P);

        menuItemBar = new JCheckBoxMenuItem("Bar");
        menuItemBar.addActionListener(this);
        menuItemPlot.setMnemonic(KeyEvent.VK_B);

        menuItemGuage = new JCheckBoxMenuItem("Guage");
        menuItemGuage.addActionListener(this);
        menuItemPlot.setMnemonic(KeyEvent.VK_G);

        menuItemCalibration = new JCheckBoxMenuItem("Calibration");
        menuItemCalibration.addActionListener(this);
        menuItemPlot.setMnemonic(KeyEvent.VK_C);

        menuItemRpm = new JCheckBoxMenuItem("RPM");
        menuItemRpm.addActionListener(this);
        menuItemPlot.setMnemonic(KeyEvent.VK_R);

        menuItemSettings = new JCheckBoxMenuItem("Settings");
        menuItemSettings.addActionListener(this);
        menuItemPlot.setMnemonic(KeyEvent.VK_E);

        menuDisplay.add(menuItemPlot);
        menuDisplay.add(menuItemBar);
        menuDisplay.add(menuItemGuage);
        menuDisplay.add(menuItemCalibration);
        menuDisplay.add(menuItemRpm);
        menuDisplay.add(menuItemSettings);

        menuItemStartRecording.setEnabled(true);
        menuItemStopRecording.setEnabled(false);
        menuItemClearRecording.setEnabled(false);
        menuItemSaveRecordingAs.setEnabled(false);

        List<String> portStrings = SerialStuff.getInstance().listSerialPorts();

        portList = new JComboBox(portStrings.toArray(new String[0]));
        portList.setToolTipText("Select from available ports");
        portList.addActionListener((ActionEvent e) -> {
            JComboBox comboBox = (JComboBox) e.getSource();
            Object o = comboBox.getSelectedItem();
        });

        JToolBar toolbar = new JToolBar();
        toolbar.setRollover(true);

        connectButton = makeButton("Connect",
                "icons/32x32/utilities-system-monitor.png",
                "Connect using selected port");
        connectButton.addActionListener((ActionEvent e) -> {
            startSerialComms();
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
        });

        toolbar.add(connectButton);

        disconnectButton = makeButton("Disconnect",
                "icons/32x32/process-stop.png",
                "Disconnect serial port");
        disconnectButton.addActionListener((ActionEvent e) -> {
            stopSerialComms();
            disconnectButton.setEnabled(false);
            connectButton.setEnabled(true);
        });
        disconnectButton.setEnabled(false);
        toolbar.add(disconnectButton);

        toolbar.add(portList);

        refreshButton = makeButton("",
                "icons/32x32/view-refresh.png",
                "Refresh serial port list");
        refreshButton.addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> {
                List<String> myPortStrings = SerialStuff.getInstance().listSerialPorts();
                DefaultComboBoxModel newModel = new DefaultComboBoxModel(myPortStrings.toArray(new String[0]));
                portList.setModel(newModel);
                this.repaint();
            });
        });

        toolbar.add(refreshButton);
        toolbar.addSeparator();
        recordButton = makeButton("Start Recording",
                "icons/32x32/media-record.png",
                "Record data capture");
        toolbar.add(recordButton);

        stopButton = makeButton("Stop Recording",
                "icons/32x32/media-playback-stop.png",
                "Stop recording data capture");
        toolbar.add(stopButton);

        clearButton = makeButton("Clear Recording",
                "icons/32x32/edit-clear.png",
                "Clear recorded data capture");
        toolbar.add(clearButton);

        saveAsButton = makeButton("",
                "icons/32x32/document-save-as.png",
                "Save data capture as...");
        saveAsButton.addActionListener(new SaveAction(this, "Save As"));
        toolbar.add(saveAsButton);

        saveAsButton.setEnabled(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(BorderLayout.NORTH, menuBar);
        topPanel.add(BorderLayout.SOUTH, toolbar);

        JTextArea centerText = new JTextArea();

        //Adding Components to the frame.
        this.getContentPane().add(BorderLayout.NORTH, topPanel);
        this.getContentPane().add(BorderLayout.CENTER, centerText);
        this.setVisible(true);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                SerialStuff.getInstance().closeSerialPort();
            }
        });
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Plot")) {
            if (menuItemPlot.isSelected()) {
                SwingUtilities.invokeLater(() -> {
                    chart = new VacuumChart();
                    chart.setVisible(true);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    chart.dispose();
                });
            }
        }
        if (cmd.equals("Bar")) {
            if (menuItemBar.isSelected()) {
                SwingUtilities.invokeLater(() -> {
                    barChart = new BarChart();
                    barChart.setVisible(true);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    barChart.dispose();
                });
            }
        }

        if (cmd.equals("Guage")) {
            if (menuItemGuage.isSelected()) {
                SwingUtilities.invokeLater(() -> {
                    guageChart = new MeterChart();
                    guageChart.setVisible(true);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    guageChart.dispose();
                });
            }
        }

        if (cmd.equals("Calibration")) {
            if (menuItemCalibration.isSelected()) {
                SwingUtilities.invokeLater(() -> {
                    calibrationChart = new CalibrationChart();
                    calibrationChart.setVisible(true);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    calibrationChart.dispose();
                });
            }
        }

        if (cmd.equals("RPM")) {
            if (menuItemRpm.isSelected()) {
                SwingUtilities.invokeLater(() -> {
                    rpmChart = new RpmChart();
                    rpmChart.setVisible(true);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    rpmChart.dispose();
                });
            }
        }

        if (cmd.equals("Settings")) {
            if (menuItemSettings.isSelected()) {
                SwingUtilities.invokeLater(() -> {
                    SettingsChart settingsChart = new SettingsChart();
                    settingsChart.setVisible(true);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    settingsChart.dispose();
                });
            }
        }

        if (cmd.equals("Connect CarbOnBal")) {
            startSerialComms();
        }

        if (cmd.equals("Start Recording")) {
            menuItemStartRecording.setEnabled(false);
            menuItemStopRecording.setEnabled(true);
            menuItemClearRecording.setEnabled(false);
            menuItemSaveRecordingAs.setEnabled(false);

            recordButton.setEnabled(false);
            stopButton.setEnabled(true);
            clearButton.setEnabled(false);
            saveAsButton.setEnabled(false);
            Dispatcher.getInstance().enableRecording();
        }

        if (cmd.equals("Stop Recording")) {
            menuItemStartRecording.setEnabled(false);
            menuItemStopRecording.setEnabled(false);
            menuItemClearRecording.setEnabled(true);
            menuItemSaveRecordingAs.setEnabled(true);

            recordButton.setEnabled(false);
            stopButton.setEnabled(false);
            clearButton.setEnabled(true);
            saveAsButton.setEnabled(true);
            Dispatcher.getInstance().disableRecording();
        }

        if (cmd.equals("Clear Recording")) {
            menuItemClearRecording.setEnabled(false);
            menuItemStartRecording.setEnabled(true);
            menuItemStopRecording.setEnabled(false);
            menuItemSaveRecordingAs.setEnabled(false);

            recordButton.setEnabled(true);
            stopButton.setEnabled(false);
            clearButton.setEnabled(false);
            saveAsButton.setEnabled(false);

            Dispatcher.getInstance().disableRecording();
            Dispatcher.getInstance().clearRecording();
        }

        if (cmd.equals("Save As")) {
            menuItemSaveRecordingAs.setEnabled(false);
            saveAsButton.setEnabled(false);
            Dispatcher.getInstance().clearRecording();
        }

    }

    private JButton makeButton(String actionCommand, String iconPath, String toolTip) {
        JButton newButton = new JButton();
        newButton.setActionCommand(actionCommand);
        newButton.addActionListener(this);
        String icon = iconPath;
        URL imageURL = getClass().getClassLoader().getResource(icon);
        newButton.setIcon(new ImageIcon(imageURL, actionCommand + " Icon"));
        newButton.setToolTipText(toolTip);

        return newButton;
    }

    private JButton makeTextButton(String text, String actionCommand, String toolTip) {
        JButton newButton = new JButton(text);
        newButton.setActionCommand(actionCommand);
        newButton.setToolTipText(toolTip);
        return newButton;
    }

    private void startSerialComms() {
        new Thread(() -> {
            try {
                SerialStuff.getInstance().initSerialComms((String) portList.getSelectedItem());
                SerialStuff.getInstance().openSerialPort();
            } catch (InterruptedException ex) {
                log.error(ex);
            }
        }).start();
    }

    private void stopSerialComms() {
        new Thread(() -> {
            SerialStuff.getInstance().closeSerialPort();
        }).start();
    }
}
