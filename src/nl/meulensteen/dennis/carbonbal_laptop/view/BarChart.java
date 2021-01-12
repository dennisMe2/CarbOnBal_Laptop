/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.view;

import java.awt.BorderLayout;
import java.awt.Color;
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
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.SETTINGS;
import nl.meulensteen.dennis.carbonbal_laptop.model.Settings;
import nl.meulensteen.dennis.carbonbal_laptop.model.TimeValue;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;

public class BarChart extends JFrame implements PropertyChangeListener {
    private CategoryDataset dataset;
    private DefaultCategoryDataset model;
    private int numSensors = 0;
    public XYSeries series1 = new XYSeries(0);
    public XYSeries series2 = new XYSeries(1);
    public XYSeries series3 = new XYSeries(2);
    public XYSeries series4 = new XYSeries(3);
    private long lastInvocation = Instant.now().toEpochMilli();
    public Double data1;

    public BarChart() {
        super("Bar Chart");
        
        Settings settings = Dispatcher.getInstance().getSettings();
        if(settings == null){
            numSensors = 4;
        }else{
            numSensors = settings.getCylinders();
        }
        
        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        
        this.setSize(Integer.valueOf(prefs.get("BAR_CHART_SCREEN_WIDTH", String.valueOf(640))), Integer.valueOf(prefs.get("BAR_CHART_SCREEN_HEIGHT", String.valueOf(480))));
        this.setLocation(Integer.valueOf(prefs.get("BAR_CHART_SCREEN_X_POS", String.valueOf(320))), Integer.valueOf(prefs.get("BAR_CHART_SCREEN_Y_POS", String.valueOf(240)))); 
    
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                prefs.put("BAR_CHART_SCREEN_WIDTH", String.valueOf(e.getComponent().getSize().width));
                prefs.put("BAR_CHART_SCREEN_HEIGHT", String.valueOf(e.getComponent().getSize().height));
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                prefs.put("BAR_CHART_SCREEN_X_POS", String.valueOf(e.getComponent().getLocation().x));
                prefs.put("BAR_CHART_SCREEN_Y_POS", String.valueOf(e.getComponent().getLocation().y));
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dispatcher.getInstance().addVacuumChangeListener(this);
        Dispatcher.getInstance().addSettingsChangeListener(this);
        
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(SETTINGS.getValue())){
            Settings settings = (Settings) event.getNewValue();
            numSensors = settings.getCylinders();
            return;
        }
        
        long differenceMilliseconds = Instant.now().toEpochMilli() - lastInvocation;
        if(differenceMilliseconds < 10) return;
       
        List<TimeValue<Double>> newValues = (List<TimeValue<Double>>) event.getNewValue();

        if(numSensors >=1) model.setValue(newValues.get(0).value, "1", "C1");
        if(numSensors >=2) model.setValue(newValues.get(1).value, "2", "C2");
        if(numSensors >=3) model.setValue(newValues.get(2).value, "3", "C3");
        if(numSensors >=4) model.setValue(newValues.get(3).value, "4", "C4");
      
        lastInvocation = Instant.now().toEpochMilli();
    }

    private JPanel createChartPanel() {
        String chartTitle = "Carb Vacuum Chart";
        String xAxisLabel = "Carb";
        String yAxisLabel = "Vac";

        if(numSensors == 1) dataset = createDataset(new Double[]{0.0});
        if(numSensors == 2) dataset = createDataset(new Double[]{0.0, 0.0});
        if(numSensors == 3) dataset = createDataset(new Double[]{0.0, 0.0, 0.0});
        if(numSensors == 4) dataset = createDataset(new Double[]{0.0, 0.0, 0.0, 0.0});

        JFreeChart chart = ChartFactory.createBarChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        chart.getCategoryPlot().getRangeAxis().setLowerBound(0.0);
        chart.getCategoryPlot().getRangeAxis().setUpperBound(1030.0);

        
        CategoryPlot categoryplot = chart.getCategoryPlot();
        BarRenderer bar = new BarRenderer();
        bar.setItemMargin(0.1); //reduce the width between the bars.
        
        bar.setMaximumBarWidth(1.0 / numSensors);
        if(numSensors >=1) bar.setSeriesPaint(0, Color.RED); 
        if(numSensors >=2) bar.setSeriesPaint(1,Color.GREEN);
        if(numSensors >=3) bar.setSeriesPaint(2,Color.BLUE);
        if(numSensors >=4) bar.setSeriesPaint(3,Color.ORANGE);
        
        categoryplot.setRenderer(bar);
        categoryplot.setBackgroundPaint(new Color(192, 192, 192));
        
        return new ChartPanel(chart);
    }

    private CategoryDataset createDataset(Double[] values) {
        model = new DefaultCategoryDataset();

        if(numSensors >=1) model.addValue(values[0], "1", "C1");
        if(numSensors >=2) model.addValue(values[1], "1", "C2");
        if(numSensors >=3) model.addValue(values[2], "1", "C3");
        if(numSensors >=4) model.addValue(values[3], "1", "C4");
        return model;
    }

}
