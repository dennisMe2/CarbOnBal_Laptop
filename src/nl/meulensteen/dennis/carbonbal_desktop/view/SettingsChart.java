/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import nl.meulensteen.dennis.carbonbal_desktop.control.Dispatcher;
import nl.meulensteen.dennis.carbonbal_desktop.control.SettingsFormatter;
import nl.meulensteen.dennis.carbonbal_desktop.model.Settings;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;

/**
 *
 * @author dennis
 */
public class SettingsChart extends JFrame implements PropertyChangeListener {

    DefaultTableModel model;
    
     public SettingsChart() {

         
        super("Settings");
        this.setLayout(new GridLayout());
        JPanel chartPanel = createChartPanel(0);
        
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        
        this.setSize(Integer.valueOf(prefs.get("SETTINGS_CHART_SCREEN_WIDTH", String.valueOf(640))), Integer.valueOf(prefs.get("SETTINGS_CHART_SCREEN_HEIGHT", String.valueOf(480))));
        this.setLocation(Integer.valueOf(prefs.get("SETTINGS_CHART_SCREEN_X_POS", String.valueOf(320))), Integer.valueOf(prefs.get("SETTINGS_CHART_SCREEN_Y_POS", String.valueOf(240)))); 
    
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                prefs.put("SETTINGS_CHART_SCREEN_WIDTH", String.valueOf(e.getComponent().getSize().width));
                prefs.put("SETTINGS_CHART_SCREEN_HEIGHT", String.valueOf(e.getComponent().getSize().height));
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                prefs.put("SETTINGS_CHART_SCREEN_X_POS", String.valueOf(e.getComponent().getLocation().x));
                prefs.put("SETTINGS_CHART_SCREEN_Y_POS", String.valueOf(e.getComponent().getLocation().y));
            }
        });

 
        add(chartPanel, BorderLayout.CENTER);
       
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       
        Dispatcher.getInstance().addSettingsChangeListener(this);
        Dispatcher.getInstance().pollSettingsChanges();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        Settings newSettings = (Settings) event.getNewValue();
        List<String[]> tableData = SettingsFormatter.formatSettings(newSettings);
        
        model.setRowCount(0);
        for(String[] row : tableData){
            model.addRow(row);
        }
        
        model.fireTableDataChanged();
    }

    private JPanel createChartPanel(int i) {
        String chartTitle = "SETTINGS Chart";
        
        String column[]={"ID","NAME","VALUE"};         
        model = new DefaultTableModel(column,0);
       
        
        JTable table=new JTable(model);    
        table.setBounds(30,40,200,300);       
        
        TableColumnModel columnModel = table.getColumnModel();
        
        columnModel.getColumn(0).setPreferredWidth(5);
        columnModel.getColumn(1).setPreferredWidth(250);
        columnModel.getColumn(2).setPreferredWidth(100);

        
        JScrollPane sp=new JScrollPane(table);  
        JPanel f=new JPanel();    
        f.add(sp);          
        f.setSize(300,400);    
        f.setVisible(true);    
       return f;
    }

    
}
