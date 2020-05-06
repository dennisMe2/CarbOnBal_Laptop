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
public enum GraphType {
    ABSOLUTE_BAR("Absolute Bar Graph"),
    RELATIVE_BAR("Relative Bar Graph"),
    DIAGNOSTIC("Diagnostic Display");
    
    
    private final String description;
    
    GraphType(String description){
        this.description = description;
    }
}
