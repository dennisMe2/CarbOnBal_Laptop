/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.meulensteen.dennis.carbonbal_desktop.control.Dispatcher;
import nl.meulensteen.dennis.carbonbal_desktop.control.Utils;
import static nl.meulensteen.dennis.carbonbal_desktop.control.Utils.parseValues;
import nl.meulensteen.dennis.carbonbal_desktop.model.TimeValue;

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
        sp.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 65535, 65535);
        sp.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        if (sp.openPort()) {
            System.out.println("Port is open, wait 2S. for Arduino DTR reboot");
            Thread.sleep(2000);
            MessageListener listener = new MessageListener();
            sp.addDataListener(listener);
        } else {
            System.out.println("Failed to open port :(");
            return false;
        }
        try {
            sp.getOutputStream().write(0xFC); //go to data dump mode
        } catch (IOException ex) {
            Logger.getLogger(SerialStuff.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public final class MessageListener implements SerialPortMessageListener {
        Integer counter = 0;

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public byte[] getMessageDelimiter() {
            return new byte[]{(byte) 0xFD};
        }

        @Override
        public boolean delimiterIndicatesEndOfMessage() {
            return true;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            byte[] delimitedMessage = event.getReceivedData();

            List<Integer> intValues = parseValues(delimitedMessage);
            
            if(null == intValues) return;
            
            counter++;
            dispatcher.processNewValues( Utils.getRawTimeValues(counter, intValues) );
        }

    }

}
