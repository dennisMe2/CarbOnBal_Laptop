/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.fazecast.jSerialComm.SerialPortMessageListenerWithExceptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.meulensteen.dennis.carbonbal_laptop.control.Dispatcher;
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.CALIBRATION;
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.SETTINGS;
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.VACUUM;


/**
 *
 * @author dmeulensteen
 */
public class SerialStuff {
    private static SerialStuff instance;
    private SerialPort sp;
    private final Dispatcher dispatcher = Dispatcher.getInstance();
   
    
    private SerialStuff() {
    }
   
    public static SerialStuff getInstance() {
        if (instance == null) {
            instance = new SerialStuff();
        }
        return instance;
    }

    public void initSerialComms(String name) throws InterruptedException {
        if(sp != null && sp.isOpen()){
            sp.closePort();
        }
        sp = SerialPort.getCommPort(name);
        return;
    }
    
    public List<String>  listSerialPorts(){
        SerialPort[] ports = SerialPort.getCommPorts();
        List<String> portList = new ArrayList();
        for (SerialPort port : ports){
            portList.add(port.getSystemPortName());
        }
        return portList;
    }
    public boolean openSerialPort() throws InterruptedException {
        sp.setComPortParameters(230400, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING,0,0);
        sp.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        if (sp.openPort()) {
            System.out.println("Port is open, wait 1.5 S. for Arduino DTR reboot");
            Thread.sleep(1500);
            
            ValuesListener valuesListener = new ValuesListener();
            sp.addDataListener(valuesListener);
        
            
        } else {
            System.out.println("Failed to open port :(");
            return false;
        }
        getSettings();
        
        return true;
    }

    public void getSettings(){
        try {
            sp.getOutputStream().write(SETTINGS.getCommand()); //go to settings data dump mode
        } catch (IOException ex) {
            Logger.getLogger(SerialStuff.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getCalibration(){
           try {
            sp.getOutputStream().write(CALIBRATION.getCommand()); //go to calibration data dump mode
        } catch (IOException ex) {
            Logger.getLogger(SerialStuff.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getVacuum(){
           try {
            sp.getOutputStream().write(VACUUM.getCommand()); //go to vacuum data dump mode
        } catch (IOException ex) {
            Logger.getLogger(SerialStuff.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public final class ValuesListener implements SerialPortMessageListenerWithExceptions {
       
        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public byte[] getMessageDelimiter() {
            return new byte[]{(byte) 0xFE,(byte) 0xFD};
        }

        @Override
        public boolean delimiterIndicatesEndOfMessage() {
            return false;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            dispatcher.processNewValues( event.getReceivedData() );
        }

        @Override
        public void catchException(Exception e) {
            Logger.getLogger(SerialStuff.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
   

}
