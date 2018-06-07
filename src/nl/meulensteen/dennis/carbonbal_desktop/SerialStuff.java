/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop;

import com.fazecast.jSerialComm.SerialPort;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dmeulensteen
 */
public class SerialStuff {

    static final String FILENAME = "data.txt";
    private static final boolean USE_ARDUINO = false;

    private static SerialStuff instance;
    private boolean isGettingTheData = false;

    BufferedReader br = null;
    FileReader fr = null;
    private byte[] packet;
    private Tuple[] arrayOfTuple = {new Tuple(1.0, 0.0), new Tuple(1.0, 0.0), new Tuple(1.0, 0.0), new Tuple(1.0, 0.0)};

    private SerialPort sp;

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

    protected void getTheData() throws InterruptedException, IOException {

        if (USE_ARDUINO && !openSerialPort()) {
            return;
        };

        try {
            fr = new FileReader(FILENAME);
            br = new BufferedReader(fr);

            String line;
            byte[] myByte = {0};
            int errCounter = 0;
            while ((line = br.readLine()) != null) {
                if (USE_ARDUINO) {
                    packet = getPacket(line);
                }

                notifyListeners(
                        "tuples",
                        this.arrayOfTuple,
                        this.arrayOfTuple = getTuples(line)
                );

                Thread.sleep(3);

                if (USE_ARDUINO) {
                    while (!(sp.getInputStream().read() == (int) 0xfe)) {
                        //do nothing
                    }
                    try {
                        sp.getOutputStream().write(packet);
                        System.out.println(line);

                    } catch (IOException e) {
                        System.out.println("Serial packet write error #" + errCounter);
                    }
                }
            }
            if (USE_ARDUINO) {
                sp.getOutputStream().close();
                sp.closePort();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + FILENAME);
        } catch (IOException e) {
            System.out.println("Something went wrong opening file");
            e.printStackTrace();
        } finally {
            if (USE_ARDUINO) {
                sp.closePort();
            }
        }

        return;
    }

    private static Tuple[] getTuples(String line) {
        final Double now = new Double(System.currentTimeMillis());
        Tuple[] tuples = {new Tuple(now, 0.0), new Tuple(now, 0.0),
            new Tuple(now, 0.0), new Tuple(now, 0.0)};

        String[] numbers = line.split(",");

        for (int i = 1; i < 5; i++) {
            tuples[i - 1].value = Double.parseDouble(numbers[i]);
        }
        return tuples;
    }

    private static byte[] getPacket(String line) {
        byte[] packet = new byte[10];
        packet[0] = (byte) 0xff;//start bytes per row
        packet[1] = (byte) 0x01;//start byte 2

        String[] numbers = line.split(",");
        int packetIndex = 1;
        int data;
        int mask = 0xff;
        for (int i = 1; i < numbers.length; i++) {
            data = Integer.parseInt(numbers[i]);
            packet[++packetIndex] = (byte) (data >>> 8);
            packet[++packetIndex] = (byte) (data & mask);
        }
        return packet;
    }

    private boolean openSerialPort() throws InterruptedException {
        sp = SerialPort.getCommPort("/dev/ttyUSB0");
        sp.setComPortParameters(38400, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (sp.openPort()) {
            System.out.println("Port is open, wait for Arduino DTR reboot");
            Thread.sleep(2000);
        } else {
            System.out.println("Failed to open port :(");
            return false;
        }
        return true;
    }

}
