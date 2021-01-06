/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.control;

import java.util.ArrayList;
import java.util.List;
import nl.meulensteen.dennis.carbonbal_desktop.model.Button1;
import nl.meulensteen.dennis.carbonbal_desktop.model.Button2;
import nl.meulensteen.dennis.carbonbal_desktop.model.Button3;
import nl.meulensteen.dennis.carbonbal_desktop.model.GraphType;
import nl.meulensteen.dennis.carbonbal_desktop.model.Settings;
import nl.meulensteen.dennis.carbonbal_desktop.model.SettingsRow;
import nl.meulensteen.dennis.carbonbal_desktop.model.Units;

/**
 *
 * @author dennis
 */
public class SettingsFormatter {
    
    public static List<String[]> formatSettings(Settings settings){
        List<String[]> settingsList = new ArrayList<>();
        
        settingsList.add(new SettingsRow(1, "Settings Version", settings.getVersionId().toString()).getRow());
        settingsList.add(new SettingsRow(2, "Silent Mode", boolToDisEnabled(settings.getSilent())).getRow());
        settingsList.add(new SettingsRow(3, "Advanced Menu Mode", boolToDisEnabled(settings.getAdvanced())).getRow());
        settingsList.add(new SettingsRow(4, "Splash SCreen Enabled", boolToDisEnabled(settings.getSplashScreen())).getRow());
        settingsList.add(new SettingsRow(5,"Number of Cylinders", settings.getCylinders().toString()).getRow());
        settingsList.add(new SettingsRow(6,"Master Cylinder", settings.getMaster().toString()).getRow());
        settingsList.add(new SettingsRow(7, "Left / Up Button Function", Button1.getDescription(settings.getButton1())).getRow());
        settingsList.add(new SettingsRow(8, "Right / Down Button Function", Button2.getDescription(settings.getButton2())).getRow());
        settingsList.add(new SettingsRow(9, "Cancel Button Function", Button3.getDescription(settings.getButton3())).getRow());
        settingsList.add(new SettingsRow(10, "Screen Contrast", settings.getContrast().toString()).getRow());
        settingsList.add(new SettingsRow(11, "Screen Brightness", settings.getBrightness().toString()).getRow());
        settingsList.add(new SettingsRow(12,"Number of Cylinders", GraphType.getDescription(settings.getGraphType())).getRow());
        settingsList.add(new SettingsRow(13, "RPM Damping", settings.getRpmDamping().toString()).getRow());
        settingsList.add(new SettingsRow(14,"Units of Measurement", Units.getDescription(settings.getUnits())).getRow());
        settingsList.add(new SettingsRow(15, "Max Zoom Range In Relative Display Mode", settings.getZoom().toString()).getRow());
        settingsList.add(new SettingsRow(16, "Max Calibration Deviation For Display", settings.getCalibrationMax().toString()).getRow());
        settingsList.add(new SettingsRow(17, "Damping", settings.getDamping().toString()).getRow());
        return settingsList;
        
    }
    
    static String boolToDisEnabled(Boolean bool){
        return (bool) ? "Enabled" : "Disabled";
    }
    
}
