/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static nl.meulensteen.dennis.carbonbal_desktop.control.Utils.calculateAverages;
import nl.meulensteen.dennis.carbonbal_desktop.model.TimeValue;

/**
 *
 * @author dennis
 */
public class Dispatcher {

    private static Dispatcher instance;
    private List<PropertyChangeListener> listeners = new ArrayList<>();
    private List<PropertyChangeListener> integerListeners = new ArrayList<>();
    private List<TimeValue<Integer>> tuples = new ArrayList<>();
    private List<TimeValue<Double>> doubleTuples = new ArrayList<>();

    private List<Double> averages = Arrays.asList(0.0, 0.0, 0.0, 0.0);

    private Dispatcher() {

    }

    public static Dispatcher getInstance() {
        if (instance == null) {
            instance = new Dispatcher();
        }
        return instance;
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listeners.add(newListener);
    }

    public void addIntegerChangeListener(PropertyChangeListener newListener) {
        integerListeners.add(newListener);
    }

    private void notifyListeners(String property, List<TimeValue<Double>> oldValues, List<TimeValue<Double>> newValues) {
        for (PropertyChangeListener name : listeners) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValues, newValues));
        }
    }

    private void notifyIntegerListeners(String property, List<TimeValue<Integer>> oldValues, List<TimeValue<Integer>> newValues) {
        for (PropertyChangeListener name : integerListeners) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValues, newValues));
        }
    }

    public void processNewValues(List<TimeValue<Integer>> newValues) {
        
        notifyIntegerListeners("tuples", tuples, tuples = newValues);

        averages = calculateAverages(averages, newValues);

        List<TimeValue<Double>> newDoubleTuples = Utils.getTimeValues(newValues.get(0).time, averages);

        notifyListeners("tuples", doubleTuples, doubleTuples = newDoubleTuples);
    }

}
