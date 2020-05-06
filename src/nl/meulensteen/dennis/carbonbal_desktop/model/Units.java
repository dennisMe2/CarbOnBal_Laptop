/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.model;

import lombok.Getter;

/**
 *
 * @author dennis
 */
@Getter
public enum Units {
    RAW("Raw Sensor Values"),
    RAW_DESCENDING("Descending Raw Sensor Values"),
    MILLIBAR_HPA("Millibar / HPa"),
    MILLIBAR_HPA_DESCENDING("Millibar / HPa Descending"),
    CM_HG("Cm of Mercury"),
    CM_HG_DESCENDING("Cm of Mercury Descending"),
    IN_HG("Inches of Mercury"),
    IN_HG_DESCENDING("Inches of Mercury Descending");
    
    
    private final String description;
    
    Units(String description){
        this.description = description;
    }
    
}
