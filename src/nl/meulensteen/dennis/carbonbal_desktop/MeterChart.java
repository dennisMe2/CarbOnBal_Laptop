/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This program demonstrates how to draw XY line chart with XYDataset using
 * JFreechart library.
 *
 * @author www.codejava.net
 *
 */
public class MeterChart extends JFrame implements PropertyChangeListener {
    private CategoryDataset dataset;
    private SerialStuff serialStuff;
    private DefaultValueDataset[] models = {null,null,null,null};

    public Double data1;

    public MeterChart() {
        super("Meter Chart");
        this.setLayout(new GridLayout());
        JPanel[] chartPanel = {null,null,null,null};
        
        for (int i=0;i<4;i++) {
            chartPanel[i] = createChartPanel(i);
            add(chartPanel[i], BorderLayout.CENTER);
        }
        setSize( 1280,320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        SerialStuff.getInstance().addChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        Tuple[] newValues = (Tuple[]) event.getNewValue();

        for (int i=0;i<4;i++) models[i].setValue(newValues[i].value);

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
