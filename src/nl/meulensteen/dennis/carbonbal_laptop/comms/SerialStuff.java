/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListenerWithExceptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import nl.meulensteen.dennis.carbonbal_laptop.control.Dispatcher;
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.CALIBRATION;
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.SETTINGS;
import static nl.meulensteen.dennis.carbonbal_laptop.model.EventType.VACUUM;


/**
 *
 * @author dmeulensteen
 */
@Log4j
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
        if(sp == null) return false;
        
        sp.setComPortParameters(230400, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING,0,0);
        sp.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        if (sp.openPort()) {
            log.debug("Wait For Arduino reboot");
            Thread.sleep(1500);
            
            ValuesListener valuesListener = new ValuesListener();
            sp.addDataListener(valuesListener);
        
            
        } else {
            log.error("Failed to open serial port!");
            return false;
        }
        getSettings();
        
        return true;
    }

    public void getSettings(){
        if(sp == null) return;
        try {
            sp.getOutputStream().write(SETTINGS.getCommand()); //go to settings data dump mode
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
    
    public void getCalibration(){
        if(sp == null) return;
           try {
            sp.getOutputStream().write(CALIBRATION.getCommand()); //go to calibration data dump mode
        } catch (IOException ex) {
            log.error( ex.getMessage());
        }
    }
    
    public void getVacuum(){
        if(sp == null) return;
        
        try {
            sp.getOutputStream().write(VACUUM.getCommand()); //go to vacuum data dump mode
        } catch (IOException ex) {
            log.error( ex.getMessage());
        }
    }

    public void closeSerialPort() {
        if(sp != null) sp.closePort();
    }
    
    public final class ValuesListener implements SerialPortMessageListenerWithExceptions {
       
        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public byte[] getMessageDelimiter() {
            return new byte[]{(byte) 0xFE,(byte) 0xE4};
        }

        @Override
        public boolean delimiterIndicatesEndOfMessage() {
            return true;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            dispatcher.processNewValues( event.getReceivedData() );
        }

        @Override
        public void catchException(Exception ex) {
            log.error("Exception in serial packet processing.", ex);
        }
    }
    
   

}
