/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JPanel;
import nl.meulensteen.dennis.carbonbal_laptop.control.Dispatcher;
import nl.meulensteen.dennis.carbonbal_laptop.model.TimeValue;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultValueDataset;

/**
 * This program demonstrates how to draw XY line chart with XYDataset using
 * JFreechart library.
 *
 * @author www.codejava.net
 *
 */
public class MeterChart extends JFrame implements PropertyChangeListener {
    private CategoryDataset dataset;
    private DefaultValueDataset[] models = {null,null,null,null};

    public Double data1;
     private long lastInvocation = Instant.now().toEpochMilli();

    public MeterChart() {
        super("Meter Chart");
        this.setLayout(new GridLayout());
        JPanel[] chartPanel = {null,null,null,null};
        
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        
        this.setSize(Integer.valueOf(prefs.get("METER_CHART_SCREEN_WIDTH", String.valueOf(640))), Integer.valueOf(prefs.get("METER_CHART_SCREEN_HEIGHT", String.valueOf(480))));
        this.setLocation(Integer.valueOf(prefs.get("METER_CHART_SCREEN_X_POS", String.valueOf(320))), Integer.valueOf(prefs.get("METER_CHART_SCREEN_Y_POS", String.valueOf(240)))); 
    
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                prefs.put("METER_CHART_SCREEN_WIDTH", String.valueOf(e.getComponent().getSize().width));
                prefs.put("METER_CHART_SCREEN_HEIGHT", String.valueOf(e.getComponent().getSize().height));
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                prefs.put("METER_CHART_SCREEN_X_POS", String.valueOf(e.getComponent().getLocation().x));
                prefs.put("METER_CHART_SCREEN_Y_POS", String.valueOf(e.getComponent().getLocation().y));
            }
        });

        
        for (int i=0;i<4;i++) {
            chartPanel[i] = createChartPanel(i);
            add(chartPanel[i], BorderLayout.CENTER);
        }
       
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       
        Dispatcher.getInstance().addChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        long differenceMilliseconds = Instant.now().toEpochMilli() - lastInvocation;
        if(differenceMilliseconds < 4) return;
        
        List<TimeValue<Double>> newValues = (List<TimeValue<Double>>) event.getNewValue();

        for (int i=0;i<4;i++) models[i].setValue(newValues.get(i).value);
        lastInvocation = Instant.now().toEpochMilli();
    }

    private JPanel createChartPanel(int i) {
        String chartTitle = "Carb Vacuum Chart";

        models[i] = new DefaultValueDataset(00.0);
        MeterPlot plot = new MeterPlot(models[i]);
        plot.setRange(new Range(0.00,1030.00));
        
        JFreeChart chart = new JFreeChart(chartTitle,
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        
        return new ChartPanel(chart);
    }


}
