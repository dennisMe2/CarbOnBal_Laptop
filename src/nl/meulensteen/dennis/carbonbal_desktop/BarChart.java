/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;

public class BarChart extends JFrame implements PropertyChangeListener {
    private CategoryDataset dataset;
    private SerialStuff serialStuff;
    private DefaultCategoryDataset model;

    public XYSeries series1 = new XYSeries(0);
    public XYSeries series2 = new XYSeries(1);
    public XYSeries series3 = new XYSeries(2);
    public XYSeries series4 = new XYSeries(3);

    public Double data1;

    public BarChart() {
        super("Bar Chart");

        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        setSize(640, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        SerialStuff.getInstance().addChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        Tuple[] newValues = (Tuple[]) event.getNewValue();

        model.setValue(newValues[0].value, "1", "C1");
        model.setValue(newValues[1].value, "1", "C2");
        model.setValue(newValues[2].value, "1", "C3");
        model.setValue(newValues[3].value, "1", "C4");
    }

    private JPanel createChartPanel() {
        String chartTitle = "Carb Vacuum Chart";
        String xAxisLabel = "Carb";
        String yAxisLabel = "Vac";

        dataset = createDataset(new Double[]{0.0, 0.0, 0.0, 0.0});

        JFreeChart chart = ChartFactory.createBarChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        chart.getCategoryPlot().getRangeAxis().setLowerBound(0.0);
        chart.getCategoryPlot().getRangeAxis().setUpperBound(1030.0);

        return new ChartPanel(chart);
    }

    private CategoryDataset createDataset(Double[] values) {
        model = new DefaultCategoryDataset();

        model.addValue(values[0], "1", "C1");
        model.addValue(values[1], "1", "C2");
        model.addValue(values[2], "1", "C3");
        model.addValue(values[3], "1", "C4");
        return model;
    }

}
