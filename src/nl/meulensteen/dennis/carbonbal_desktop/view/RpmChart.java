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
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JPanel;
import nl.meulensteen.dennis.carbonbal_desktop.control.Dispatcher;
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
public class RpmChart extends JFrame implements PropertyChangeListener {
    private DefaultValueDataset model;
    
     public RpmChart() {
        super("RPM");
        this.setLayout(new GridLayout());
        JPanel chartPanel = createChartPanel(0);
        
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        
        this.setSize(Integer.valueOf(prefs.get("RPM_CHART_SCREEN_WIDTH", String.valueOf(640))), Integer.valueOf(prefs.get("RPM_CHART_SCREEN_HEIGHT", String.valueOf(480))));
        this.setLocation(Integer.valueOf(prefs.get("RPM_CHART_SCREEN_X_POS", String.valueOf(320))), Integer.valueOf(prefs.get("RPM_CHART_SCREEN_Y_POS", String.valueOf(240)))); 
    
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                prefs.put("RPM_CHART_SCREEN_WIDTH", String.valueOf(e.getComponent().getSize().width));
                prefs.put("RPM_CHART_SCREEN_HEIGHT", String.valueOf(e.getComponent().getSize().height));
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                prefs.put("RPM_CHART_SCREEN_X_POS", String.valueOf(e.getComponent().getLocation().x));
                prefs.put("RPM_CHART_SCREEN_Y_POS", String.valueOf(e.getComponent().getLocation().y));
            }
        });

 
        add(chartPanel, BorderLayout.CENTER);
       
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       
        Dispatcher.getInstance().addRpmChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        Double newValue = (Double) event.getNewValue();
        model.setValue(newValue);
    }

    private JPanel createChartPanel(int i) {
        String chartTitle = "RPM Chart";

        model = new DefaultValueDataset(00.0);
        DialPlot plot = new DialPlot(model);
        plot.setDialFrame(new StandardDialFrame());
        plot.addLayer(new DialValueIndicator(0));
        plot.addLayer(new DialPointer.Pointer());

        StandardDialScale scale = new StandardDialScale(0, 5000,
        -120, -300, 1000, 999 );
        scale.setTickRadius(0.88);
        scale.setTickLabelOffset(0.20);
        plot.addScale(0, scale);
        
        plot.addLayer(new StandardDialRange(0, 900, Color.blue));
        plot.addLayer(new StandardDialRange(1100, 2400, Color.green));
        plot.addLayer(new StandardDialRange(2600, 5000, Color.red));


        
        JFreeChart chart = new JFreeChart(chartTitle,
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        
        return new ChartPanel(chart);
    }

    
}
