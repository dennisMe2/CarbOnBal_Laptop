/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.view;

import nl.meulensteen.dennis.carbonbal_desktop.model.TimeValue;
import nl.meulensteen.dennis.carbonbal_desktop.comms.SerialStuff;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import nl.meulensteen.dennis.carbonbal_desktop.control.Dispatcher;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class XYLineChart extends JFrame implements ActionListener, PropertyChangeListener {

    private Timer timer = new Timer(250, this);
    private XYDataset dataset;

    public XYSeries series1 = new XYSeries(0);
    public XYSeries series2 = new XYSeries(1);
    public XYSeries series3 = new XYSeries(2);
    public XYSeries series4 = new XYSeries(3);

    public Double data1;

    public XYLineChart() {
        super("XY Line Chart Example with JFreechart");


        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        
        this.setSize(Integer.valueOf(prefs.get("LINE_CHART_SCREEN_WIDTH", String.valueOf(640))), Integer.valueOf(prefs.get("LINE_CHART_SCREEN_HEIGHT", String.valueOf(480))));
        this.setLocation(Integer.valueOf(prefs.get("LINE_CHART_SCREEN_X_POS", String.valueOf(320))), Integer.valueOf(prefs.get("LINE_CHART_SCREEN_Y_POS", String.valueOf(240)))); 
    
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                prefs.put("LINE_CHART_SCREEN_WIDTH", String.valueOf(e.getComponent().getSize().width));
                prefs.put("LINE_CHART_SCREEN_HEIGHT", String.valueOf(e.getComponent().getSize().height));
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                prefs.put("LINE_CHART_SCREEN_X_POS", String.valueOf(e.getComponent().getLocation().x));
                prefs.put("LINE_CHART_SCREEN_Y_POS", String.valueOf(e.getComponent().getLocation().y));
            }
        });
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dispatcher.getInstance().addIntegerChangeListener(this);
    }

    
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        List<TimeValue<Integer>> newValues = (List<TimeValue<Integer>>) event.getNewValue();

        this.series1.addOrUpdate(newValues.get(0).time.doubleValue(), newValues.get(0).value.doubleValue());
    }

    private JPanel createChartPanel() {
        String chartTitle = "Carb Vacuum Chart";
        String xAxisLabel = "Time";
        String yAxisLabel = "Vac";

        dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        ValueAxis xAxis = chart.getXYPlot().getDomainAxis();
        xAxis.setAutoRange(true);
        xAxis.setFixedAutoRange(1000.0);
        chart.getXYPlot().getRangeAxis().setRange(100, 1030.0);

//		boolean showLegend = false;
        customizeChart(chart);

        // saves the chart as an image files
//		File imageFile = new File("XYLineChart.png");
//		int width = 640;
//		int height = 480;
//		
//		try {
//			ChartUtils.saveChartAsPNG(imageFile, chart, width, height);
//		} catch (Exception ex) {
//			System.err.println(ex);
//		}
        return new ChartPanel(chart);
    }

    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
        dataset.addSeries(series4);
        return dataset;
    }

    private void customizeChart(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // sets paint color for each series
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.BLUE);
        renderer.setSeriesPaint(2, Color.MAGENTA);

        // sets thickness for series (using strokes)
        for (int i = 0; i < 4; i++) {
            renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        }

        renderer.setDefaultLinesVisible(true);

        for (int i = 0; i < 4; i++) {
            renderer.setSeriesShapesVisible(i, false);
        }

        // sets paint color for plot outlines
        plot.setOutlinePaint(Color.BLUE);
        plot.setOutlineStroke(new BasicStroke(2.0f));

        // sets renderer for lines
        plot.setRenderer(renderer);

        // sets plot background
        plot.setBackgroundPaint(Color.DARK_GRAY);

        // sets paint color for the grid lines
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Double now = new Double(new Millisecond().getMillisecond());

        this.series1.add(now, data1);
        this.series2.add(now, (Double) 3.3);
        this.series3.add(now, (Double) 4.4);
        this.series4.add(now, (Double) 1.1);
    }

}
