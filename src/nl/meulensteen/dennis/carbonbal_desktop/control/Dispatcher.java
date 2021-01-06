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
import static nl.meulensteen.dennis.carbonbal_desktop.control.Utils.parseValues;
import nl.meulensteen.dennis.carbonbal_desktop.model.MessageType;
import nl.meulensteen.dennis.carbonbal_desktop.model.Settings;
import nl.meulensteen.dennis.carbonbal_desktop.model.TimeValue;

/**
 *
 * @author dennis
 */
public class Dispatcher {

    private static Dispatcher instance;
    private List<PropertyChangeListener> listeners = new ArrayList<>();
    private List<PropertyChangeListener> integerListeners = new ArrayList<>();
    private List<PropertyChangeListener> calibrationListeners = new ArrayList<>();
    private List<PropertyChangeListener> rpmListeners = new ArrayList<>();
    private List<PropertyChangeListener> settingsListeners = new ArrayList<>();

    private List<TimeValue<Integer>> tuples = new ArrayList<>();
    private List<TimeValue<Double>> doubleTuples = new ArrayList<>();
    private List<List<Integer>> calibrationValues = new ArrayList<>();
    private List<Double> averages = Arrays.asList(0.0, 0.0, 0.0, 0.0);
    private Integer counter = 0;
    private RpmCalculator rpmCalculator = new RpmCalculator();
    private SettingsBuilder settingsBuilder = new SettingsBuilder();
    private Settings settings;
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
    
    public void addSettingsChangeListener(PropertyChangeListener newListener) {
        settingsListeners.add(newListener);
    }
    
    
    public void addCalibrationChangeListener(PropertyChangeListener newListener) {
        calibrationListeners.add(newListener);
    }
    public void addRpmChangeListener(PropertyChangeListener newListener) {
        rpmListeners.add(newListener);
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
    
    public void pollSettingsChanges(){
        if(settings != null){
            notifySettingsListeners("Settings", settings,settings);
        }
    }
    
    private void notifySettingsListeners(String property, Settings oldValues, Settings newValues) {
        for (PropertyChangeListener name : settingsListeners) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValues, newValues));
        }
    }
    
    
     private void notifyCalibrationListeners(String property, List<List<Integer>> oldValues, List<List<Integer>> newValues) {
        for (PropertyChangeListener name : calibrationListeners) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValues, newValues));
        }
    }
    
     private void notifyRpmListeners(String property, Double oldValue, Double newValue) {
        for (PropertyChangeListener name : rpmListeners) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }
     
    private MessageType determineMessageType(byte[] delimitedMessage){
        switch (delimitedMessage[2]){
            case (byte) 0xE0:
                return MessageType.CARB_VACUUM;
            case (byte) 0xE1:
                return MessageType.CALIBRATION;
            case (byte) 0xE2:
                return MessageType.SETTINGS;
            case (byte) 0xE3:
                return MessageType.DIAGNOSTICS;
            case (byte) 0xE4:
                return MessageType.END_DATA;
            default:
                return MessageType.ERROR;
        }
    }
    
    public byte[] stripHeader(byte[] values){
        return Arrays.copyOfRange(values, 3, values.length);
    }

    public void processNewValues(byte[] delimitedMessage) {
        
        MessageType messageType = determineMessageType(delimitedMessage);
        byte[] values = stripHeader(delimitedMessage);
        
        switch(messageType.name()){
            case ("CARB_VACUUM"):
                processCarbVacuumValues(values);
                break;
            case ("CALIBRATION"):
                processCalibrationValues(values);
                break;
            case("SETTINGS"):
                processSettingsValues(values);
                break;
            case("DIAGNOSTICS"):
                
                break;
            case("END_DATA"):
                
                break;
            default:
                System.out.println("Unrecognized packet error");
                
        }
        
        
    }
    
     private void processSettingsValues(byte[] newRawValues) {
        this.settings = settingsBuilder.get(newRawValues);
        notifySettingsListeners("settings", settings, settings);
    }
    
    private void processCalibrationValues(byte[] newRawValues) {
        List<Integer> dataPoint = new ArrayList<>();
        dataPoint.add(Integer.valueOf(newRawValues[0]));
        dataPoint.add(Integer.valueOf(newRawValues[1]));
        dataPoint.add(Integer.valueOf(newRawValues[2]));
        dataPoint.add(Integer.valueOf(newRawValues[3]));
        
        calibrationValues.add(dataPoint);
        if(calibrationValues.size() == 256){
            notifyCalibrationListeners("calibration", calibrationValues, calibrationValues);
        }
    }
    
    private void processCarbVacuumValues(byte[] newRawValues){
        List<Integer> newIntValues = parseValues(newRawValues);    
        
        List<TimeValue<Integer>> newValues = Utils.getRawTimeValues(counter++, newIntValues);
        
        notifyIntegerListeners("tuples", tuples, tuples = newValues);

        averages = calculateAverages(averages, newValues);

        List<TimeValue<Double>> newDoubleTuples = Utils.getTimeValues(newValues.get(0).time, averages);
        notifyListeners("tuples", doubleTuples, doubleTuples = newDoubleTuples);
        
        Double rpm = rpmCalculator.calculateAverageRpm(newValues);    
        notifyRpmListeners("rpm", rpm, rpm);
    }

}
