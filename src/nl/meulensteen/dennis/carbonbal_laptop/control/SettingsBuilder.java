/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.control;

import nl.meulensteen.dennis.carbonbal_laptop.model.Settings;

/**
 *
 * @author dennis
 */
public class SettingsBuilder {
    
    public Settings get(byte[] values){
        if(values.length < 4) return new Settings();
        
        byte settingsVersion = values[0];
        
        if(settingsVersion >=28 && settingsVersion <=31){            
                return fillCommonSettings(values);
         } else{
                return fillCommonSettings(values);
        }
        
    }
    
    private Settings fillCommonSettings(byte[] bytes){
        Settings settings = new Settings();
        
       settings.setVersionId(Integer.valueOf(bytes[0])); 
       settings.setSilent(toBoolean(bytes[1]));
       settings.setAdvanced(toBoolean(bytes[2]));
       settings.setSplashScreen(toBoolean(bytes[3]));
       settings.setCylinders(Integer.valueOf(bytes[4])); 
       settings.setMaster(Integer.valueOf(bytes[5]));
       settings.setButton1(Integer.valueOf(bytes[6]));
       settings.setButton2(Integer.valueOf(bytes[7])); 
       settings.setButton3(Integer.valueOf(bytes[8])); 
       settings.setContrast(Byte.toUnsignedInt(bytes[9]));
       settings.setBrightness(Byte.toUnsignedInt(bytes[10]));
       settings.setGraphType(Integer.valueOf(bytes[11])); 
       settings.setRpmDamping(Integer.valueOf(bytes[12]));
       settings.setUnits(Integer.valueOf(bytes[13]));
       settings.setZoom(Integer.valueOf(bytes[14]));
       settings.setCalibrationMax(Integer.valueOf(bytes[15])); 
       settings.setDamping(Integer.valueOf(bytes[16])); 
       
        return settings;
    }
    
    private Boolean toBoolean(byte b){
        return (b != 0);
    }
    
}
