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
public enum Button3 {
    FREEZE("Freeze Display"),
    RESET_AVERAGING("Reset Averaging"),
    RPM("RPM Display");
        
    private final String description;
    
    Button3(String description){
        this.description = description;
    }
}
