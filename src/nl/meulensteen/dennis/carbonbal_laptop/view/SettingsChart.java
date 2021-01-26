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
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import nl.meulensteen.dennis.carbonbal_laptop.control.Dispatcher;
import nl.meulensteen.dennis.carbonbal_laptop.control.SettingsFormatter;
import nl.meulensteen.dennis.carbonbal_laptop.model.Settings;

/**
 *
 * @author dennis
 */
public class SettingsChart extends CarbOnBalDisplay implements PropertyChangeListener {
    private static final String NAME = "Settings";
    DefaultTableModel model;

    public SettingsChart() {

        super(NAME);
        this.setLayout(new GridLayout());
        JPanel chartPanel = createChartPanel(0);

        createPreferences();

        add(chartPanel, BorderLayout.CENTER);

        Dispatcher.getInstance().addSettingsChangeListener(this);
        Dispatcher.getInstance().pollSettings();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        Settings newSettings = (Settings) event.getNewValue();
        if (newSettings != null && newSettings.getVersionId() != null) {
            List<String[]> tableData = SettingsFormatter.formatSettings(newSettings);

            model.setRowCount(0);
            for (String[] row : tableData) {
                model.addRow(row);
            }

            model.fireTableDataChanged();
            pack();
        }
    }

    private JPanel createChartPanel(int i) {
        String column[] = {"ID", "NAME", "VALUE"};
        model = new DefaultTableModel(column, 0);

        JTable table = new JTable(model);
        table.setBounds(30, 40, 200, 300);

        TableColumnModel columnModel = table.getColumnModel();

        columnModel.getColumn(0).setPreferredWidth(5);
        columnModel.getColumn(1).setPreferredWidth(250);
        columnModel.getColumn(2).setPreferredWidth(100);

        JScrollPane sp = new JScrollPane(table);
        JPanel f = new JPanel();
        f.add(sp);
        f.setSize(300, 400);
        f.setVisible(true);
        return f;
    }

}
