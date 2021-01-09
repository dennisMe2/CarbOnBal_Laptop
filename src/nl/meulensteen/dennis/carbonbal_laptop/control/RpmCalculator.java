/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.control;

import java.util.List;
import nl.meulensteen.dennis.carbonbal_laptop.model.TimeValue;

/**
 *
 * @author dennis
 */
public class RpmCalculator {
    Integer previousDownTurn = 0;
    Integer downTurn = 122;
    
    Integer previousValue = 1024;
    Integer descendCount = 0;
    Integer ascendCount = 0;
    Boolean descending = false;
    Boolean previouslyDescending = false;
    
    Double averageRpm=0.0D;
    
    public Double calculateAverageRpm( List<TimeValue<Integer>> intValues) {
        Integer currentValue = intValues.get(0).value; 
        Integer delta = 1000;
                
        if (currentValue < previousValue){
            descendCount++;
            ascendCount--;
        }
        
        if (currentValue > previousValue){
            ascendCount++;
            descendCount--;
        }
        
        if(descendCount > 2 ){
            descending = true;
            ascendCount =0;
            descendCount =0;
        }
        
        if(ascendCount > 4 ){
            descending = false;
            ascendCount =0;
            descendCount =0;
        }
        
        
        if(previouslyDescending == true && descending == false ){
            downTurn = intValues.get(0).time;
            delta = 120000 / (downTurn-previousDownTurn);
            averageRpm = averageRpm + (Double.valueOf(delta) - averageRpm) / 100;
        
            previousDownTurn = downTurn;
        }
          
        
        previouslyDescending = descending;
        previousValue = currentValue;
            
        
        return averageRpm;
    }
    
    
    
}
