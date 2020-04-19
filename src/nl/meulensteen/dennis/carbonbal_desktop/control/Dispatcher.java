/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.meulensteen.dennis.carbonbal_desktop.comms.SerialStuff;
import static nl.meulensteen.dennis.carbonbal_desktop.control.Utils.calculateAverages;
import static nl.meulensteen.dennis.carbonbal_desktop.control.Utils.getAveragedTuples;
import nl.meulensteen.dennis.carbonbal_desktop.model.Tuple;
import org.jfree.data.time.Millisecond;

/**
 *
 * @author dennis
 */
public class Dispatcher implements ActionListener {

    private static Dispatcher instance;
    private List<PropertyChangeListener> listeners = new ArrayList<>();
    private List<PropertyChangeListener> integerListeners = new ArrayList<>();
    private List<Tuple<Integer>> tuples = new ArrayList<>();
    private List<Tuple<Double>> doubleTuples = new ArrayList<>();
    

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
    
    private void notifyListeners(String property, List<Tuple<Double>> oldValues, List<Tuple<Double>> newValues) {
        for (PropertyChangeListener name : listeners) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValues, newValues));
        }
    }

    private void notifyIntegerListeners(String property, List<Tuple<Integer>> oldValues, List<Tuple<Integer>> newValues) {
        for (PropertyChangeListener name : integerListeners) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValues, newValues));
        }
    }
    
    
    public void processNewValues(List<Tuple<Integer>> newValues) {
        
        notifyIntegerListeners( "tuples", tuples, tuples = newValues );
        
       
        averages = calculateAverages(averages, newValues);
       
        List<Tuple<Double>> newDoubleTuples = Utils.getTuples(newValues.get(0).time, averages);
        
        notifyListeners( "tuples", doubleTuples, doubleTuples = newDoubleTuples );
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Double now = new Double(new Millisecond().getMillisecond());
   
    }
    
    
    
}
