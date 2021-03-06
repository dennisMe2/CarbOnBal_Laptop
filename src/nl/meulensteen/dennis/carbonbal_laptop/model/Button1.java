/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.model;

import java.util.Arrays;
import lombok.Getter;

/**
 *
 * @author dennis
 */
@Getter
public enum Button1 {
    CONTRAST("Change contrast"),
    RESET_AVERAGING("Reset averaging"),
    DAMPING("Change damping");
    
    private final String description;
    
    Button1(String description){
        this.description = description;
    }
    
    public static String getDescription(int value) {
        return Arrays.asList(values()).get(value).getDescription();
    }
    
}
