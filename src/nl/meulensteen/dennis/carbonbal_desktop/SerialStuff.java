/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dmeulensteen
 */
public class SerialStuff {

    static final String FILENAME = "data.txt";
    private static final boolean USE_ARDUINO = true;

    private static SerialStuff instance;
    private boolean isGettingTheData = false;

    BufferedReader br = null;
    FileReader fr = null;
    private byte[] packet;
    private Tuple[] arrayOfTuple = {new Tuple(1.0, 0.0), new Tuple(1.0, 0.0), new Tuple(1.0, 0.0), new Tuple(1.0, 0.0)};

    private SerialPort sp;
    private Integer counter;

    private SerialStuff() {
    }

    private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();

    public static SerialStuff getInstance() {
        if (instance == null) {
            instance = new SerialStuff();
        }
        return instance;
    }

    private void notifyListeners(String property, Tuple[] oldValue, Tuple[] newValue) {
        for (PropertyChangeListener name : listener) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }

    protected void getTheData() throws InterruptedException {
        openSerialPort();
        return;
    }

    private Double lastTime = 0.0;
    private Double seconds = 0.0;

    private static byte[] getPacket(String line) {
        byte[] packet = new byte[9];
        packet[0] = (byte) 0xfe;//start bytes per row

        String[] numbers = line.split(",");
        int packetIndex = 0;
        int data;
        int mask = 0b01111111;
        for (int i = 1; i < numbers.length; i++) {
            data = Integer.parseInt(numbers[i]);
            packet[++packetIndex] = (byte) (data >>> 7);
            packet[++packetIndex] = (byte) (data & mask);//only allow the lowest 7 bits
        }
        return packet;
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

        int counter = 0;
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

            if (null != intValues) averages = calculateAverages(averages, intValues);

            counter++;
            if ((counter % 33) == 0) {

                notifyListeners(
                        "tuples",
                        arrayOfTuple,
                        arrayOfTuple = getTuples(averages)
                );

            }
        }

        private List<Double> calculateAverages(List<Double> averages, List<Integer> intValues) {
            
            for(int i=0;i<4;i++){
                averages.set(i, averages.get(i) + ( intValues.get(i) - averages.get(i)) / 100);
            }
            return averages;
        }

        List<Integer> parseValues(byte[] numbers) {
            int intValue;
            
            if(numbers.length < 9) return null;
            
            List<Integer> values = new ArrayList(4);
            
            for (int i = 0; i < 8; i++) {
                intValue = ((int) numbers[i]) << 8;
                intValue |= Byte.toUnsignedInt(numbers[i + 1]);

                values.add(intValue);
                i++;
            }
            return values;
        }

        Tuple[] getTuples(List<Double> averages) {
            Double now = Double.valueOf(Instant.now().getNano()/1000);
            
            Tuple[] tuples = {new Tuple(now, averages.get(0)), new Tuple(now, averages.get(1)),
                new Tuple(now, averages.get(2)), new Tuple(now, averages.get(3))};
            return tuples;
        }

    }

}
