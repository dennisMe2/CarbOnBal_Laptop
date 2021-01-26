/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.view;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.Timer;
import nl.meulensteen.dennis.carbonbal_laptop.control.Dispatcher;

/**
 *
 * @author dennis
 */
public class CarbOnBalDisplay extends JFrame {
    private static String name;
    private PropertyChangeListener myListener;
    private ParentWindowListener parent;
    
    CarbOnBalDisplay(String title){
        super(title);
        CarbOnBalDisplay.name = title;
    }
    
    public String getName(){
        return name;
    }
    
    void setParentWindowListener(ParentWindowListener parent){
        this.parent = parent;
    }
    
    void setListener( PropertyChangeListener listener){
        myListener = listener;
    }
    
    void createPreferences(){
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        
        this.setSize(Integer.valueOf(prefs.get(name + "_SCREEN_WIDTH", String.valueOf(640))), Integer.valueOf(prefs.get(name + "_SCREEN_HEIGHT", String.valueOf(480))));
        this.setLocation(Integer.valueOf(prefs.get(name + "_SCREEN_X_POS", String.valueOf(320))), Integer.valueOf(prefs.get(name + "_SCREEN_Y_POS", String.valueOf(240)))); 
    
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                prefs.put(name + "_SCREEN_WIDTH", String.valueOf(e.getComponent().getSize().width));
                prefs.put(name + "_SCREEN_HEIGHT", String.valueOf(e.getComponent().getSize().height));
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                prefs.put(name + "_SCREEN_X_POS", String.valueOf(e.getComponent().getLocation().x));
                prefs.put(name + "_SCREEN_Y_POS", String.valueOf(e.getComponent().getLocation().y));
            }
        });
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            Dispatcher.getInstance().removeMeFromListeners(myListener);
            if(null != parent) parent.notifyWindowClosing(name);
            Timer timer = new Timer(1000, (ActionEvent e1) -> {
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
            super.processWindowEvent(e); 
        } else {        
            super.processWindowEvent(e); 
        }
    }
}
