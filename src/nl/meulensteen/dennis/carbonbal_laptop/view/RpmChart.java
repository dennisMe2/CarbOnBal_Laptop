/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import nl.meulensteen.dennis.carbonbal_laptop.control.Dispatcher;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;

/**
 *
 * @author dennis
 */
public class RpmChart extends CarbOnBalDisplay implements PropertyChangeListener {

    private static final String NAME = "RPM Chart";
    private DefaultValueDataset model;

    public RpmChart() {
        super(NAME);
        this.setLayout(new GridLayout());
        JPanel chartPanel = createChartPanel(0);

        createPreferences();

        add(chartPanel, BorderLayout.CENTER);

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
                -120, -300, 1000, 999);
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
