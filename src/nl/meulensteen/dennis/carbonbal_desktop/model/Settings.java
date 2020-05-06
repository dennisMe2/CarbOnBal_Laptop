/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.model;

import lombok.Data;

/**
 *
 * @author dennis
 */
@Data
public class Settings {
    private Integer versionId;
    private Boolean silent;
    private Boolean advanced;
    private Boolean splashScreen;
    private Integer cylinders;
    private Integer master;
    private Integer button1;
    private Integer button2;
    private Integer button3;
    private Integer contrast;
    private Integer brightness;
    private Integer graphType;
    private Integer rpmDamping;
    private Integer units;
    private Integer zoom;
    private Integer calibrationMax;
    private Integer damping;
}
