/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.meulensteen.dennis.carbonbal_desktop.control.Dispatcher;
import nl.meulensteen.dennis.carbonbal_desktop.control.Utils;
import nl.meulensteen.dennis.carbonbal_desktop.model.Tuple;
import static nl.meulensteen.dennis.carbonbal_desktop.control.Utils.parseValues;

/**
 *
 * @author dmeulensteen
 */
public class SerialStuff {

    static final String FILENAME = "data.txt";
    private static SerialStuff instance;

    BufferedReader br = null;
    FileReader fr = null;
    private List<Tuple<Integer>> tuples;
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

    public void initSerialComms() throws InterruptedException {
        openSerialPort();
        return;
    }

    private boolean openSerialPort() throws InterruptedException {
        sp = SerialPort.getCommPort("/dev/ttyUSB0");
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
        List<Double> averages = Arrays.asList(0.0, 0.0, 0.0, 0.0);

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
            dispatcher.processNewValues( tuples = Utils.getRawTuples(counter, intValues) );
        }

    }

}
