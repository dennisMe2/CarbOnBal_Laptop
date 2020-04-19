/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import nl.meulensteen.dennis.carbonbal_desktop.control.Dispatcher;
import nl.meulensteen.dennis.carbonbal_desktop.model.Tuple;

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

        Dispatcher.getInstance().addChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        List<Tuple<Double>> newValues = (List<Tuple<Double>>) event.getNewValue();

        for (int i=0;i<4;i++) models[i].setValue(newValues.get(i).value);

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
