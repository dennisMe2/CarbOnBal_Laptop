/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.model;

import java.util.Arrays;
import lombok.Getter;
import static nl.meulensteen.dennis.carbonbal_laptop.model.Button1.values;

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
    
    public static String getDescription(int value) {
        return Arrays.asList(values()).get(value).getDescription();
    }
}
