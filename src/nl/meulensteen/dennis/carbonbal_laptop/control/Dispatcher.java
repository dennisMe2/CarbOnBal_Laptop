/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;
import lombok.extern.log4j.Log4j;
import nl.meulensteen.dennis.carbonbal_laptop.comms.SerialStuff;
import static nl.meulensteen.dennis.carbonbal_laptop.control.Utils.calculateAverages;
import static nl.meulensteen.dennis.carbonbal_laptop.control.Utils.parseValues;
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.CALIBRATION;
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.RPM;
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.SETTINGS;
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.VACUUM;
import nl.meulensteen.dennis.carbonbal_laptop.model.MessageType;
import nl.meulensteen.dennis.carbonbal_laptop.model.Settings;
import nl.meulensteen.dennis.carbonbal_laptop.model.TimeValue;

/**
 *
 * @author dennis
 */
@Log4j
public class Dispatcher {

    private static Dispatcher instance;
    private List<PropertyChangeListener> listeners = new ArrayList<>();
    private List<PropertyChangeListener> integerListeners = new ArrayList<>();
    private List<PropertyChangeListener> calibrationListeners = new ArrayList<>();
    private List<PropertyChangeListener> rpmListeners = new ArrayList<>();
    private List<PropertyChangeListener> settingsListeners = new ArrayList<>();

    private List<List<TimeValue<Integer>>> recordingTuples = new ArrayList<>();
    private List<TimeValue<Integer>> tuples = new ArrayList<>();
    private List<TimeValue<Double>> doubleTuples = new ArrayList<>();
    private List<List<Integer>> calibrationValues = new ArrayList<>();
    private List<Double> averages = Arrays.asList(0.0, 0.0, 0.0, 0.0);
    private Integer counter = 0;
    private RpmCalculator rpmCalculator = new RpmCalculator();
    private SettingsBuilder settingsBuilder = new SettingsBuilder();
    private Settings settings;
    private boolean isRecordingEnabled = false;
   
    private Dispatcher() {
        //nope!
    }

    public static Dispatcher getInstance() {
        if (instance == null) {
            instance = new Dispatcher();
        }
        return instance;
    }

    public void enableRecording(){
        isRecordingEnabled = true;
    }
    
    public void disableRecording(){
        isRecordingEnabled = false;
    }
    
    public void clearRecording(){
        recordingTuples = new ArrayList<>();
    }
    
    public List<List<TimeValue<Integer>>> getRecordingdata(){
        return recordingTuples;
    }
    
    public void addVacuumChangeListener(PropertyChangeListener newListener) {
        listeners.add(newListener);
    }

    public void addIntVacuumChangeListener(PropertyChangeListener newListener) {
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
    
    public void removeMeFromListeners(PropertyChangeListener uninterestedListener){
        if(null == uninterestedListener) return;
        
        rpmListeners.remove(uninterestedListener);
        integerListeners.remove(uninterestedListener);
        listeners.remove(uninterestedListener);
        calibrationListeners.remove(uninterestedListener);
        settingsListeners.remove(uninterestedListener);
        log.info("listener removed:" + uninterestedListener.getClass().getSimpleName());
    }
       
    public void pollVacuum(){
        SerialStuff.getInstance().getVacuum();
    }
    
    private void notifyVacuumListeners(String property, List<TimeValue<Double>> oldValues, List<TimeValue<Double>> newValues) {
        for (PropertyChangeListener name : listeners) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                   name.propertyChange(new PropertyChangeEvent(this, property, oldValues, newValues));
               }
            });
            
        }
    }

    private void notifyIntVacuumListeners(String property, List<TimeValue<Integer>> oldValues, List<TimeValue<Integer>> newValues) {
        for (PropertyChangeListener name : integerListeners) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                   name.propertyChange(new PropertyChangeEvent(this, property, oldValues, newValues));
               }
            });
        }
    }
    
    public Settings getSettings(){
        return settings;
    }
    
    public void pollSettings(){
        SerialStuff.getInstance().getSettings();
    }
    
    public void notifySettingsChanges(){
        if(settings != null){
            notifySettingsListeners(SETTINGS.getValue(), settings, settings);
        }
    }
    
    private void notifySettingsListeners(String property, Settings oldValues, Settings newValues) {
        for (PropertyChangeListener name : settingsListeners) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                   name.propertyChange(new PropertyChangeEvent(this, property, oldValues, newValues));
               }
            });
        }
    }
    
    public void pollCalibration(){
        SerialStuff.getInstance().getCalibration();
    }
    public void pollCalibrationChanges(){
        if(calibrationValues != null){
            notifyCalibrationListeners(CALIBRATION.getValue(), calibrationValues, calibrationValues);
        }
    }
    
     private void notifyCalibrationListeners(String property, List<List<Integer>> oldValues, List<List<Integer>> newValues) {
        for (PropertyChangeListener name : calibrationListeners) {
             SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                   name.propertyChange(new PropertyChangeEvent(this, property, oldValues, newValues));
               }
            });
        }
    }
    
     private void notifyRpmListeners(String property, Double oldValue, Double newValue) {
        for (PropertyChangeListener name : rpmListeners) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                   name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
               }
            });
        }
    }
     
    private MessageType determineMessageType(byte[] delimitedMessage){
       if(delimitedMessage.length < 3) return MessageType.ERROR;
       byte typeDescriptor = getMessageTypeDescriptor(delimitedMessage);
       
        switch (typeDescriptor){
            case (byte) 0xE0:
                return MessageType.CARB_VACUUM;
            case (byte) 0xE1:
                return MessageType.CALIBRATION;
            case (byte) 0xE2:
                return MessageType.SETTINGS;// bytes: -2 -3
            case (byte) 0xE3:
                return MessageType.DIAGNOSTICS;
            case (byte) 0xE4:
                return MessageType.END_DATA;
            default:
                return MessageType.ERROR;
        }
    }
    
    private byte getMessageTypeDescriptor(byte[] message){
        int length = message[message.length-3];
        int reportedSize = message.length-4 -length;
        if(reportedSize >=0 && reportedSize<message.length){
            return message[reportedSize];
        }
        return 0x00;
    }
    
    public byte[] stripHeaderAndFooter(byte[] values){
        if (values.length < 3) return new byte[] {(byte)0x00};
        
        return Arrays.copyOfRange(values, 1, values.length-3);
    }

    public void processNewValues(byte[] delimitedMessage) {
        
        MessageType messageType = determineMessageType(delimitedMessage);
        byte[] values = stripHeaderAndFooter(delimitedMessage);
        
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
         if(newRawValues != null){
            this.settings = settingsBuilder.get(newRawValues);
            notifySettingsListeners(SETTINGS.getValue(), settings, settings);
         }
    }
    
    private void processCalibrationValues(byte[] newRawValues) {
        List<Integer> dataPoint = new ArrayList<>();
        dataPoint.add(Integer.valueOf(newRawValues[0]));
        dataPoint.add(Integer.valueOf(newRawValues[1]));
        dataPoint.add(Integer.valueOf(newRawValues[2]));
        dataPoint.add(Integer.valueOf(newRawValues[3]));
        
        calibrationValues.add(dataPoint);
        if(calibrationValues.size() == 256){
            notifyCalibrationListeners(CALIBRATION.getValue(), calibrationValues, calibrationValues);
            calibrationValues = new ArrayList<>();
        }
    }
    
    private void processCarbVacuumValues(byte[] newRawValues){
        List<Integer> newIntValues = parseValues(newRawValues);    
        
        List<TimeValue<Integer>> newValues = Utils.getRawTimeValues(counter++, newIntValues);
        
        if(this.isRecordingEnabled){
            this.recordingTuples.add(newValues);
        }
        
        notifyIntVacuumListeners(VACUUM.getValue(), tuples, tuples = newValues);

        averages = calculateAverages(averages, newValues);

        List<TimeValue<Double>> newDoubleTuples = Utils.getTimeValues(newValues.get(0).time, averages);
        notifyVacuumListeners(VACUUM.getValue(), doubleTuples, doubleTuples = newDoubleTuples);
        
        Double rpm = rpmCalculator.calculateAverageRpm(newValues);    
        notifyRpmListeners(RPM.getValue(), rpm, rpm);
    }

}
