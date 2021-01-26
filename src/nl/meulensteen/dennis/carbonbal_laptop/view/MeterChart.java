/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.util.List;

import javax.swing.JPanel;
import nl.meulensteen.dennis.carbonbal_laptop.control.Dispatcher;
import nl.meulensteen.dennis.carbonbal_laptop.model.Settings;
import nl.meulensteen.dennis.carbonbal_laptop.model.TimeValue;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;

/**
 * This program demonstrates how to draw XY line chart with XYDataset using
 * JFreechart library.
 *
 * @author www.codejava.net
 *
 */
public class MeterChart extends CarbOnBalDisplay implements PropertyChangeListener {

    private static final String NAME = "Meter Chart";
    private DefaultValueDataset[] models = {null, null, null, null};
    private int numSensors = 0;
    private long lastInvocation = Instant.now().toEpochMilli();
    public Double data1;

    public MeterChart() {
        super(NAME);
        this.setLayout(new GridLayout());
        JPanel[] chartPanel = {null, null, null, null};

        createPreferences();

        Settings settings = Dispatcher.getInstance().getSettings();
        numSensors = settings.getCylinders();

        for (int i = 0; i < numSensors; i++) {
            chartPanel[i] = createChartPanel(i);
            add(chartPanel[i], BorderLayout.CENTER);
        }

        Dispatcher.getInstance().addVacuumChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        long differenceMilliseconds = Instant.now().toEpochMilli() - lastInvocation;
        if (differenceMilliseconds < 4) {
            return;
        }

        List<TimeValue<Double>> newValues = (List<TimeValue<Double>>) event.getNewValue();

        for (int i = 0; i < numSensors; i++) {
            models[i].setValue(newValues.get(i).value);
        }
        lastInvocation = Instant.now().toEpochMilli();
    }

    private JPanel createChartPanel(int i) {
        String chartTitle = "Carb Vacuum Chart";

        models[i] = new DefaultValueDataset(00.0);
        MeterPlot plot = new MeterPlot(models[i]);
        plot.setRange(new Range(0.00, 1030.00));
        plot.setTickSize(100);
        JFreeChart chart = new JFreeChart(chartTitle,
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);

        return new ChartPanel(chart);
    }

}
