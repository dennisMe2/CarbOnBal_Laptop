/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.view;

import nl.meulensteen.dennis.carbonbal_laptop.model.TimeValue;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;
import nl.meulensteen.dennis.carbonbal_laptop.control.Dispatcher;
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

public class VacuumChart extends CarbOnBalDisplay implements ActionListener, PropertyChangeListener {

    private static final String NAME = "Vacuum Chart";
    private Timer timer = new Timer(250, this);
    private XYDataset dataset;

    public XYSeries series1 = new XYSeries(0);
    public XYSeries series2 = new XYSeries(1);
    public XYSeries series3 = new XYSeries(2);
    public XYSeries series4 = new XYSeries(3);

    public Double data1;

    private long lastInvocation = Instant.now().toEpochMilli();

    public VacuumChart() {
        super(NAME);

        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        createPreferences();

        series1.setMaximumItemCount(2001);
        series2.setMaximumItemCount(2001);
        series3.setMaximumItemCount(2001);
        series4.setMaximumItemCount(2001);

        Dispatcher.getInstance().addIntVacuumChangeListener(this);
        Dispatcher.getInstance().pollVacuum();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        long differenceMilliseconds = Instant.now().toEpochMilli() - lastInvocation;
        if (differenceMilliseconds < 4) {
            return;
        }

        List<TimeValue<Integer>> newValues = (List<TimeValue<Integer>>) event.getNewValue();

        this.series1.addOrUpdate(newValues.get(0).time.doubleValue(), newValues.get(0).value.doubleValue());
        this.series2.addOrUpdate(newValues.get(1).time.doubleValue(), newValues.get(1).value.doubleValue());
        this.series3.addOrUpdate(newValues.get(2).time.doubleValue(), newValues.get(2).value.doubleValue());
        this.series4.addOrUpdate(newValues.get(3).time.doubleValue(), newValues.get(3).value.doubleValue());
        lastInvocation = Instant.now().toEpochMilli();
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
        final Long now = Instant.now().toEpochMilli();

        this.series1.add(now, data1);
        this.series2.add(now, (Double) 3.3);
        this.series3.add(now, (Double) 4.4);
        this.series4.add(now, (Double) 1.1);
    }

}
