/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.view.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import nl.meulensteen.dennis.carbonbal_laptop.control.Dispatcher;
import nl.meulensteen.dennis.carbonbal_laptop.model.TimeValue;
import nl.meulensteen.dennis.carbonbal_laptop.view.MainScreen;

/**
 *
 * @author dennis
 */
public class SaveAction extends AbstractAction{
    private Component parent;
    
    public SaveAction(Component parent, String name) {
        super(name);
        this.parent = parent;
        
        putValue(SHORT_DESCRIPTION, "Save Recording as a new file.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(() -> {
            File csvOutputFile = new File("CarbOnBalDump.csv");
            JFileChooser chooser = new JFileChooser(csvOutputFile);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    ".csv documents", "csv");
            chooser.setFileFilter(filter);
            int result = chooser.showSaveDialog(parent);
            if (result == JFileChooser.APPROVE_OPTION) {
                try (final PrintWriter pw = new PrintWriter(chooser.getSelectedFile())) {
                    Dispatcher.getInstance().getRecordingdata().stream().map(this::convertToCSV).forEach(pw::println);
                }catch (FileNotFoundException ex) {
                    Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
        
    private String convertToCSV(List<TimeValue<Integer>> data) {
        return Stream.of(data)
          .map(this::convertToString)
          .collect(Collectors.joining(","));
    }
    
    private String convertToString(List<TimeValue<Integer>> data){
        StringBuilder sb = new StringBuilder();
        
        sb.append(data.get(0).time.toString());
        sb.append(',');
        sb.append(data.get(0).value.toString());
        sb.append(',');
        sb.append(data.get(1).value.toString());
        sb.append(',');
        sb.append(data.get(0).value.toString());
        sb.append(',');
        sb.append(data.get(0).value.toString());
            
        return sb.toString();
    }
    
}
