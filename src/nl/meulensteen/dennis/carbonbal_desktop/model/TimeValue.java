/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.model;


public class TimeValue<T> {
    public T time;
    public T value;
    
    public TimeValue(T time, T value){
        this.time=time;
        this.value=value;
    }
}
