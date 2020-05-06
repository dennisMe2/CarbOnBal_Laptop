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
public enum Button2 {
    BRIGHTNESS("Brightness"),
    RPM("RPM Display"),
    RPM_DAMPING("RPM Damping");
    
    private final String description;
    
    Button2(String description){
        this.description = description;
    }
}
