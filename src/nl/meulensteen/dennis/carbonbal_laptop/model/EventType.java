/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.model;

/**
 *
 * @author dennis
 */
public enum EventType {
    SETTINGS("settings", 0xe2),
    CALIBRATION("calibration", 0xe1),
    VACUUM("vacuum", 0xe0),
    RPM("rpm", 0x00)
    ;
    
    private String value;
    private int command;
    
    EventType(String value, int command){
        this.value = value;
        this.command = command;
    }
    
    public String getValue(){
        return value;
    }
    public int getCommand(){
        return command;
    }
    
}
