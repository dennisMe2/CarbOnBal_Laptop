/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 * @author dennis
 */
@Getter
@Setter
@Accessors(chain=true)
@AllArgsConstructor
public class SettingsRow {
    Integer id;
    String name;
    String value;
    
    public String[] getRow(){
        return new String[]{id.toString(), name, value};
    }
    
}
